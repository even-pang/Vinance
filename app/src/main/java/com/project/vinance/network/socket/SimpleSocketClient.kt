package com.project.vinance.network.socket

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.project.vinance.network.socket.dto.*
import com.project.vinance.view.FutureViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

/**
 * 소켓을 연결 및 해제하고 데이터를 바인딩 시킨다
 * */
object SimpleSocketClient {
    private var REQUEST_ID = 1
        get() = field++
    private const val EXIT_CODE = 1000
    private const val METHOD_SUBSCRIBE = "SUBSCRIBE"
    private const val METHOD_UNSUBSCRIBE = "UNSUBSCRIBE"

    private var socket: WebSocket? = null

    /**
     * ticker 속도 : 0.5초(기본)
     * aggTrade 속도 : 0.1초(기본)
     * markPrice 속도 : 1초, 3초(기본)
     * depth 속도 : 0.01초, 0.25초(기본), 0.5초
     */
    private fun createMessage(method: String, symbol: String) = """
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

    fun create() {
        val request = Request.Builder().url("wss://fstream.binance.com/ws").build()

        val client = OkHttpClient().newBuilder().readTimeout(0, TimeUnit.MILLISECONDS).build()
        socket = client.newWebSocket(request, SimpleSocket())
        client.dispatcher.executorService.shutdown()
    }

    fun start(symbol: String) {
        socket?.send(createMessage(METHOD_SUBSCRIBE, symbol.lowercase()))
    }

    fun pause(symbol: String) {
        socket?.send(createMessage(METHOD_UNSUBSCRIBE, symbol.lowercase()))
    }

    fun stop() {
        socket?.close(EXIT_CODE, null)
    }

    private class SimpleSocket : WebSocketListener() {
        private val handler by lazy { Handler(Looper.getMainLooper()) }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("Socket Open")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val dto = Gson().fromJson(text, EventNameDTO::class.java)

            CoroutineScope(Dispatchers.Main).launch {
                when (dto.eventName) {
                    "24hrTicker" -> {
                        // 전일대비
                        val ticker = Gson().fromJson(text, SymbolTickerDTO::class.java)
                        FutureViewModel.apply {
                            pricePercent.value = BigDecimal(ticker.priceChangePercent)
                        }
//                        println(ticker)
                    }
                    "aggTrade" -> {
                        val aggTrade = Gson().fromJson(text, AggregateTradeDTO::class.java)
                        FutureViewModel.apply {
                            contractPrice.value = BigDecimal(aggTrade.price)
                        }
//                        println(aggTrade)
                    }
                    "markPriceUpdate" -> {
                        // 펀딩, 카운트다운, 시장가
                        val markPrice = Gson().fromJson(text, MarkPriceDTO::class.java)

                        FutureViewModel.let {
                            it.fundingRate.value = BigDecimal(markPrice.fundingRate)
                            it.fundingTime.value = markPrice.nextFundingTime

                            it.markPrice.value = BigDecimal(markPrice.markPrice)
                        }
//                        println(markPrice)
                    }
                    "depthUpdate" -> {
                        val depth = Gson().fromJson(text, PartialBookDepthDTO::class.java)

                        FutureViewModel.apply {
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
//                        println(depth)
                    }
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            println("Socket Closed")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            t.printStackTrace()
        }
    }
}