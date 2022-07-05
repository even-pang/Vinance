package com.project.vinance.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.google.gson.GsonBuilder
import com.project.vinance.R
import com.project.vinance.calculate.Cal
import com.project.vinance.databinding.ActivityMainBinding
import com.project.vinance.network.rest.BinanceRest
import com.project.vinance.network.socket.SimpleSocketClient
import com.project.vinance.view.fragment.future.FutureFragment
import com.project.vinance.view.fragment.wallet.WalletFragment
import com.project.vinance.view.implementation.ColorChangeListener
import com.project.vinance.view.implementation.FocusListenable
import com.project.vinance.view.sub.NeedToChangeList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.mainBottomNavigation.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.mainBottomNavigation.viewTreeObserver.removeOnGlobalLayoutListener(this)
                FutureFragment.bottomHeight = binding.mainBottomNavigation.height
            }
        })

        setContentView(binding.root)
        init()
    }

    /**
     * 초기 설정
     */
    private fun init() {
        initBinding()
        initComponent()
        initFragment()
        initSocket()

        val chartText: TextView = findViewById(R.id.main_bottom_chart_text)
        val kind = GlobalData.showCoin.value ?: ""
        val name = GlobalData.showTether.value ?: ""
        chartText.setText("$kind$name Perpetual Chart")
    }

    private var fragmentView: FragmentContainerView? = null
    private var fragments: List<Fragment?> = emptyList()
    private var menus: List<TextView> = emptyList()

    private var currentFragment: Pair<Fragment, Int>? = null
    private val viewModel = GlobalData
    private val menuDrawables: Pair<List<Int>, List<Int?>> = Pair(
        listOf(R.drawable.ic_bottom_1, R.drawable.ic_bottom_2, R.drawable.ic_bottom_3, R.drawable.ic_bottom_4, R.drawable.ic_bottom_5),
        listOf(null, null, null, R.drawable.ic_bottom_4_selected, R.drawable.ic_bottom_5_selected)
    )

    private fun initBinding() {
        CoroutineScope(Dispatchers.Main).launch {
            val gson = GsonBuilder().setLenient().create()
            val instance: Retrofit =
                Retrofit.Builder().baseUrl(BinanceRest.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build()
            val api = instance.create(BinanceRest::class.java)

            val depth = api.getDepth(GlobalData.showCoin.value + GlobalData.showTether.value)
            val markPrice = api.getMarkPrice(GlobalData.showCoin.value + GlobalData.showTether.value)
            val symbolPrice = api.getSymbolPrice(GlobalData.showCoin.value + GlobalData.showTether.value)
            val tickerPrice = api.getTickerPriceChange(GlobalData.showCoin.value + GlobalData.showTether.value)

            // 시장가, 펀딩, 카운트다운
            viewModel.contractPrice.value = BigDecimal(markPrice.markPrice)
            viewModel.fundingRate.value = BigDecimal(markPrice.lastFundingRate) * BigDecimal(100)
            viewModel.fundingTime.value = markPrice.nextFundingTime

            // 현재가
            viewModel.contractPrice.value = BigDecimal(symbolPrice.price)
            viewModel.contractPriceLeft.value = BigDecimal(markPrice.markPrice)
//            findViewById<TextView>(R.id.future_order_book_scale).text = BigDecimal(1).movePointLeft(viewModel.contractPrice.value!!.scale()).toPlainString()

            // 전일대비
            viewModel.pricePercent.value = BigDecimal(tickerPrice.priceChangePercent ?: "0")

            // 호가
            val result = runCatching {
                depth.let { block ->
                    val bid = block.bids.map { Pair(it[0], it[1]) }
                    val ask = block.asks.map { Pair(it[0], it[1]) }

                    Pair(bid, ask)
                }
            }.getOrDefault(Pair(listOf(), listOf()))
            viewModel.bidsAndAsks.value = result

            // 차트 표시
        }
    }

    /**
     * 레이아웃 컴포넌트 연결 및 데이터 입력
     */
    private fun initComponent() {
        // 프래그먼트 컨테이너
        fragmentView = findViewById(R.id.main_fragment)

        // 하단 메뉴 텍스트
        menus = listOf(
            findViewById(R.id.main_menu_1),
            findViewById(R.id.main_menu_2),
            findViewById(R.id.main_menu_3),
            findViewById(R.id.main_menu_4),
            findViewById(R.id.main_menu_5)
        )

        val future = FutureFragment.newInstance("BTC", "USDT")
        val wallet = WalletFragment.newInstance("")
        NeedToChangeList.fragmentList.add(future)
        // 프래그먼트 리스트
        fragments = listOf(
            null, null, null, future, wallet
        )

        // 메뉴 클릭 이벤트 설정
        for (menu in menus) menu.setOnClickListener(this)
    }

    /**
     * 첫 표시 화면 설정
     */
    private fun initFragment() {
        val transaction = supportFragmentManager.beginTransaction()

        for (fragment in fragments) {
            fragment?.let { transaction.add(R.id.main_fragment, fragment).hide(fragment) }
        }

        currentFragment = Pair(fragments[3]!!, 3)
        transaction.show(currentFragment!!.first).commit()
    }

    /**
     * 프래그먼트 변경
     */
    private fun changeFragment(show: Fragment, position: Int) {
        if (currentFragment?.first == show) return

        if (position == 3) {
            binding.mainChartArea.visibility = View.VISIBLE
        } else {
            binding.mainChartArea.visibility = View.GONE
        }

        // 비활성 표시
        currentFragment?.let { currentFragment ->
            menus[currentFragment.second].setCompoundDrawablesWithIntrinsicBounds(0, menuDrawables.first[currentFragment.second], 0, 0)
            menus[currentFragment.second].setTextColor(getColor(R.color.un_selected))

            menus[position].setCompoundDrawablesWithIntrinsicBounds(0, menuDrawables.second[position]!!, 0, 0)
            menus[position].setTextColor(getColor(R.color.selected))
        }

        supportFragmentManager.beginTransaction().also {
            it.show(show)
            it.hide(currentFragment!!.first).commit()
        }

        currentFragment = Pair(show, position)
    }

    private fun initSocket() {
        SimpleSocketClient.createMessage(
            SimpleSocketClient.METHOD_SUBSCRIBE,
            GlobalData.showCoin.value!!.lowercase() + GlobalData.showTether.value!!.lowercase()
        )
        SimpleSocketClient.additionSubscribe()

        CoroutineScope(Dispatchers.Main).launch {
            val gson = GsonBuilder().setLenient().create()
            val instance: Retrofit =
                Retrofit.Builder().baseUrl(BinanceRest.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build()
            val api = instance.create(BinanceRest::class.java)

            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                FutureData.inputDataList.forEach { dto ->
                    FutureData.exchangeInfo?.let { exchange ->
                        val market = api.getMarkPrice(dto.coinName.first)

                        Log.d(TAG, "RESULT(${dto.coinName.first}) : $market")
                        dto.marketPrice = BigDecimal(market.markPrice)

//                    FutureViewModel.coinRoundingScale[dto.coinName.first] = exchange.symbols.find { it.symbol == dto.coinName.first }!!.pricePrecision
                        val symbol = exchange.symbols.find { it.symbol == dto.coinName.first }!!
                        dto.roundingPrice = symbol.pricePrecision
                        dto.roundingQuantity = symbol.quantityPrecision
                    }
                }
            }
            Log.d(TAG, "FINAL : ${FutureData.inputDataList}")

//            Log.d(TAG, "AFTER : ${FutureViewModel.inputDataList}")

//            Log.d("API RESULT", FutureViewModel.inputMarketData.toString())


            CoroutineScope(Dispatchers.Default).launch {
                Cal.superCal()

                (fragments[4] as WalletFragment).afterCal()
                Log.d("$TAG AFTER", FutureData.inputDataList.toString())
            }

//            println(FutureViewModel.inputDataList)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        val future = fragments[3]
        if (future is FocusListenable) {
            future.onWindowFocusChanged(hasFocus)
        }
    }

    /**
     * 화면 보이기 전
     */
    override fun onResume() {
        super.onResume()

        SimpleSocketClient.start(GlobalData.showCoin.value + GlobalData.showTether.value)
    }

    /**
     * 화면이 안 보일 때
     */
    override fun onStop() {
        super.onStop()

        SimpleSocketClient.pause(GlobalData.showCoin.value + GlobalData.showTether.value)
    }

    /**
     * 액티비티 종료료
     */
    override fun onDestroy() {
        super.onDestroy()

        SimpleSocketClient.stop()
        FutureData.inputDataList.clear()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 선물
            menus[3].id -> {
                fragments[3]?.let {
                    changeFragment(it, 3)
                    viewModel.contractPriceLeft.value = viewModel.markPrice.value
//                    SocketClient.openLimitCondition()
//                    String.valueOf(myMoney.divide(limitPrice, RoundingMode.HALF_UP).multiply(scale))

                    if (it is ColorChangeListener) it.changeStatusColor()
                }
            }
            // 지갑
            menus[4].id -> {
                fragments[4]?.let {
                    changeFragment(it, 4)
                    (fragments[4] as WalletFragment).updateOverview()

                    if (it is ColorChangeListener) it.changeStatusColor()
                }
            }
        }
    }
}
