package com.project.vinance.network.socket

import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.project.vinance.R
import com.project.vinance.calculate.Cal
import com.project.vinance.network.socket.dto.*
import com.project.vinance.view.FutureData
import com.project.vinance.view.GlobalData
import com.project.vinance.view.recycler.RecycleFuturePosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

/**
 * 소켓을 연결 및 해제하고 데이터를 바인딩 시킨다
 * */
object SimpleSocketClient {
    private var REQUEST_ID = 1
        get() = field++
    private const val EXIT_CODE = 1000
    const val METHOD_SUBSCRIBE = "SUBSCRIBE"
    private const val METHOD_UNSUBSCRIBE = "UNSUBSCRIBE"

    private val TAG = SimpleSocketClient::class.java.simpleName

    private var socket: WebSocket? = null

    /**
     * ticker 속도 : 0.5초(기본)
     * aggTrade 속도 : 0.1초(기본)
     * markPrice 속도 : 1초, 3초(기본)
     * depth 속도 : 0.01초, 0.25초(기본), 0.5초
     */
    fun createMessage(method: String, symbol: String) = """
                {
                "method" : "$method",
                "params" : [
                    "$symbol@ticker",
                    "$symbol@aggTrade",
                    "$symbol@markPrice@1s",
                    "$symbol@depth10@500ms"
                ],
                "id" : $REQUEST_ID
            }""".trimIndent()

    fun deleteMessage() = """
        {
                "method" : "UNSUBSCRIBE",
                "params" : [
                    "${GlobalData.showCoin.value!!.lowercase() + GlobalData.showTether.value!!.lowercase()}@ticker",
                    "${GlobalData.showCoin.value!!.lowercase() + GlobalData.showTether.value!!.lowercase()}@aggTrade",
                    "${GlobalData.showCoin.value!!.lowercase() + GlobalData.showTether.value!!.lowercase()}@markPrice@1s",
                    "${GlobalData.showCoin.value!!.lowercase() + GlobalData.showTether.value!!.lowercase()}@depth10@500ms"
                ],
                "id" : $REQUEST_ID
            }""${'"'}.trimIndent()
    """.trimIndent()

    /**
     * 데이터로 입력한 코인의 시장가를 추가로 구독함
     * */
    fun additionSubscribe() {
        var result = """
            {
                "method" : "$METHOD_SUBSCRIBE",
                "params" : [
        """.trimIndent()
        FutureData.inputDataList.forEachIndexed { index, inputDataDTO ->
            result += "\"${inputDataDTO.coinName.first.lowercase()}@markPrice@1s\""

            if (index + 1 != FutureData.inputDataList.size) result += ","
        }
        result += """
                ],
                "id" : $REQUEST_ID
            }
        """.trimIndent()

        socket?.send(result)
    }

    fun additionUnsubscribe() {
        var result = """
            {
                "method" : "$METHOD_UNSUBSCRIBE",
                "params" : [
        """.trimIndent()
        FutureData.inputDataList.forEachIndexed { index, inputDataDTO ->
            result += "\"${inputDataDTO.coinName.first.lowercase()}@markPrice@1s\""

            if (index + 1 != FutureData.inputDataList.size) result += ","
        }
        result += """
                ],
                "id" : $REQUEST_ID
            }
        """.trimIndent()

        socket?.send(result)
    }

    fun create() {
        val request = Request.Builder().url("wss://fstream.binance.com/ws").build()

        val client = OkHttpClient().newBuilder().readTimeout(0, TimeUnit.MILLISECONDS).build()
        socket = client.newWebSocket(request, SimpleSocket())
        client.dispatcher.executorService.shutdown()
    }

    private var timer: Timer? = null
    private const val INTERVAL = 500L

    fun start(symbol: String) {
        Log.d(TAG, "START")
        socket?.send(createMessage(METHOD_SUBSCRIBE, symbol.lowercase()))

        timer(period = 100) {
            if (Cal.fin) {
                timer = timer(period = 1000) {
                    try {
                        Cal.subCal()

                        futureRecycle?.let { layoutManager ->
                            for (i in 0 until layoutManager.itemCount) {
                                val pnl = layoutManager.findViewByPosition(i)?.findViewById<TextView>(R.id.recycle_position_pnl_value)
                                val roe = layoutManager.findViewByPosition(i)?.findViewById<TextView>(R.id.recycle_position_roe_value)

                                pnl?.let {
                                    FutureData.inputDataList[i].let {
                                        if (it.roe < BigDecimal.ZERO) {
                                            futurePosition!!.toRedColor(pnl, roe!!, null)
                                        } else {
                                            futurePosition!!.toGreenColor(pnl, roe!!, null)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Log.d(TAG, "SUPER TIMER")
                this.cancel()
            }
        }
    }

    fun pause(symbol: String) {
        Log.d(TAG, "PAUSE")
//        socket?.send(createMessage(METHOD_UNSUBSCRIBE, symbol.lowercase()))
//        timer?.cancel()
//
//        additionUnsubscribe()
    }

    fun stop() {
        Log.d(TAG, "STOP")
//        socket?.close(EXIT_CODE, null)
//        socket = null
        Log.d(TAG, "stop 멈춰제발씨발: ${GlobalData.showCoin.value!!.lowercase() + GlobalData.showTether.value!!.lowercase()}")
        socket?.send(deleteMessage())
        timer?.cancel()
        futurePosition = null
        Cal.fin = false
    }

    var futurePosition: RecycleFuturePosition? = null
    var futureRecycle: RecyclerView.LayoutManager? = null

    private class SimpleSocket : WebSocketListener() {
        private var changeable:Boolean = true
        private var i: Int = 0
            get() = field++

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "Socket Open")

            timer(period = INTERVAL) {
                changeable = true
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val dto = Gson().fromJson(text, EventNameDTO::class.java)
            CoroutineScope(Dispatchers.Main).launch {
                when (dto.eventName) {
                    "24hrTicker" -> {
                        // 전일대비
                        val ticker = Gson().fromJson(text, SymbolTickerDTO::class.java)
                        GlobalData.apply {
                            pricePercent.value = BigDecimal(ticker.priceChangePercent)
                        }
//                        Log.d(TAG, ticker)
                    }
                    "aggTrade" -> {
                        val aggTrade = Gson().fromJson(text, AggregateTradeDTO::class.java)
                        GlobalData.apply {
                            if (changeable) {
//                                Log.d(TAG, "$i RECEIVED ${aggTrade.price}")
                                contractPrice.value = BigDecimal(aggTrade.price)
                            }
                            changeable = false
                        }
//                        Log.d(TAG, aggTrade)
                    }
                    "markPriceUpdate" -> {
                        // 펀딩, 카운트다운, 시장가
                        val markPrice = Gson().fromJson(text, MarkPriceDTO::class.java)
//                        Log.d(TAG, "$markPrice")

                        GlobalData.let { it ->
                            it.fundingRate.value = BigDecimal(markPrice.fundingRate)
                            it.fundingTime.value = markPrice.nextFundingTime

                            if (markPrice.symbol == it.showCoin.value!! + it.showTether.value!!) {
                                it.markPrice.value = BigDecimal(markPrice.markPrice)
                            }

                            FutureData.inputDataList.filter { it.coinName.first == markPrice.symbol }.forEach {
                                it.marketPrice = BigDecimal(markPrice.markPrice)
                            }


                            futurePosition?.change()
//                            it.inputDataList.find { it.coinName.first == markPrice.symbol }?.marketPrice = BigDecimal(markPrice.markPrice)

//                            val index = it.inputDataList.indexOfFirst { it.coinName.first == markPrice.symbol }
//                            futurePosition?.change(index)

//                            Log.d(TAG, "RESULT: ${FutureViewModel.inputDataList}")
                        }
//                        Log.d(TAG, "OK")
                    }
                    "depthUpdate" -> {
                        val depth = Gson().fromJson(text, PartialBookDepthDTO::class.java)

                        GlobalData.apply {
                            bidsAndAsks.value = Pair(
                                depth.bidsUpdated.map {
                                    Pair(it[0], it[1])
                                }.take(ORDER_LIST_SIZE),
                                depth.asksUpdated.map {
                                    Pair(it[0], it[1])
                                }.take(ORDER_LIST_SIZE)
                            )
//                            asks.value = depth.asksUpdated.map { Pair(it[0], it[1]) }
                        }
//                        Log.d(TAG, depth)
                    }
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "Socket Closed")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            t.printStackTrace()
        }
    }
}