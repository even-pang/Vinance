package com.project.vinance.view.fragment.future

import android.graphics.Color
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.vinance.R
import com.project.vinance.client.model.market.OrderBookEntry
import com.project.vinance.databinding.FragmentFutureBinding
import com.project.vinance.network.socket.SocketClient
import com.project.vinance.view.FutureData
import com.project.vinance.view.GlobalData
import com.project.vinance.view.implementation.ColorChangeListener
import com.project.vinance.view.implementation.FocusListenable
import com.project.vinance.view.implementation.TextChangeListenable
import com.project.vinance.view.sub.StickScrollView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Feature(선물) 영역을 표시하는 프래그먼트
 *
 * @see FutureFragment.newInstance
 * */
class FutureFragment : Fragment(), FocusListenable, ColorChangeListener, TextChangeListenable {
    companion object {

        private const val COIN_TYPE: String = "COIN_TYPE"
        private const val COIN_UNIT: String = "COIN_TETHER"

        /**
         * FeatureFragment 인스턴스를 생성함
         *
         * @param coinType 코인 유형 (BTC, ETH 등)
         * @param coinUnit 코인 단위 (USD, USDT)
         * */
        @JvmStatic
        fun newInstance(coinType: String, coinUnit: String = "USDT") = FutureFragment().apply {
            arguments = Bundle().apply {
                putString(COIN_TYPE, coinType)
                putString(COIN_UNIT, coinUnit)
            }
        }

        private var closeData = BigDecimal.ZERO
    }

    private val coinType: String by lazy { arguments?.getString(COIN_TYPE) ?: "ERR" }
    private val coinUnit: String by lazy { arguments?.getString(COIN_UNIT) ?: "ERR" }

    // 컴포넌트 바인딩

    //    private val innerTab: TabLayout by lazy { requireActivity().findViewById(R.id.future_inner_tab) }
//    private val innerPager: ViewPager2 by lazy { requireActivity().findViewById(R.id.future_pager) }
//    private val scrollView: StickScrollView by lazy { requireActivity().findViewById(R.id.future_scroll_view) }
//    private val status: ConstraintLayout by lazy { requireActivity().findViewById(R.id.future_status) }
//    private val imgContainer: ConstraintLayout by lazy { requireActivity().findViewById(R.id.future_image_container) }
//    private val imgLeft: ImageView by lazy { requireActivity().findViewById(R.id.future_type_buy) }
    private val innerTab: TabLayout by lazy { binding.futureInnerTab }
    private val innerPager: ViewPager2 by lazy { binding.futurePager }
    private val scrollView: StickScrollView by lazy { binding.futureScrollView }
    private val status: ConstraintLayout by lazy { binding.topTitle.futureStatus }
    private val imgContainer: ConstraintLayout by lazy { binding.cardLeft.futureImageContainer }
    private val imgLeft: ImageView by lazy { binding.cardLeft.futureTypeBuy }

    /** 아래 */
    private val bidsWeight: List<FrameLayout> by lazy {
        listOf(
            requireActivity().findViewById(R.id.future_chart_buy1),
            requireActivity().findViewById(R.id.future_chart_buy2),
            requireActivity().findViewById(R.id.future_chart_buy3),
            requireActivity().findViewById(R.id.future_chart_buy4),
            requireActivity().findViewById(R.id.future_chart_buy5),
            requireActivity().findViewById(R.id.future_chart_buy6),
            requireActivity().findViewById(R.id.future_chart_buy7),
        )
    }

    /** 위 */
    private val asksWeight: List<View> by lazy {
        listOf(
            requireActivity().findViewById(R.id.future_chart_sell1),
            requireActivity().findViewById(R.id.future_chart_sell2),
            requireActivity().findViewById(R.id.future_chart_sell3),
            requireActivity().findViewById(R.id.future_chart_sell4),
            requireActivity().findViewById(R.id.future_chart_sell5),
            requireActivity().findViewById(R.id.future_chart_sell6),
            requireActivity().findViewById(R.id.future_chart_sell7),
        )
    }


    override fun changeStatusColor() {
        if (scrollView.isHeaderStick) {
            activity?.window?.apply {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.card_color)
            }
        } else {
            activity?.window?.apply {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.default_background)
            }
        }
    }

    override fun changeText(resId: Int, data: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            activity?.findViewById<TextView>(resId)?.let {
                it.text = /*String.format("%.2f", data?.toDouble() ?: 0.0)*/data
            }
        }
    }

    fun changeTextCurrent(resId: Int, data: BigDecimal) {
        CoroutineScope(Dispatchers.Main).launch {
            activity?.findViewById<TextView>(resId)?.let {
                it.text = data.toPlainString()

                when {
                    data > closeData -> {
                        it.setTextColor(requireActivity().getColor(R.color.sell_color))
                    }
                    data < closeData -> {
                        it.setTextColor(requireActivity().getColor(R.color.buy_color))
                    }
                    else -> {
                        it.setTextColor(requireActivity().getColor(R.color.white))
                    }
                }

                closeData = data
            }
        }
    }

    fun changeTextChangePercent(resId: Int, data: BigDecimal) {
        CoroutineScope(Dispatchers.Main).launch {
            activity?.findViewById<TextView>(resId)?.let {
                when {
                    data > BigDecimal.ZERO -> {
                        it.text = String.format("+%.2f%%", data.toDouble())
                        it.setTextColor(requireActivity().getColor(R.color.buy_color))
                    }
                    data < BigDecimal.ZERO -> {
                        it.text = String.format("%.2f%%", data.toDouble())
                        it.setTextColor(requireActivity().getColor(R.color.sell_color))
                    }
                    else -> {
                        it.text = "0.00%"
                        it.setTextColor(requireActivity().getColor(R.color.white))
                    }
                }
            }
        }
    }

    private val len = 7
    fun changeBackgroundWeight(bids: List<OrderBookEntry>, asks: List<OrderBookEntry>) {
        SocketClient.canFutureBackground = false

        CoroutineScope(Dispatchers.Main).launch {
            val bidSum = mutableListOf<BigDecimal>()
            val askSum = mutableListOf<BigDecimal>()

            bidSum.add(bids[0].qty)
            askSum.add(asks[0].qty)
            for (i in 1 until len) {
                bidSum.add(bidSum[i - 1] + bids[i].qty)
                askSum.add(askSum[i - 1] + asks[i].qty)
            }
            val max = if (bidSum[len - 1] > askSum[len - 1]) {
                bidSum[len - 1] * bids[len - 1].price
            } else {
                askSum[len - 1] * asks[len - 1].price
            }

            for (i in 0 until len) {
                val bid = bids[i].price * bidSum[i] / max * BigDecimal(1000)
                val ask = asks[i].price * askSum[i] / max * BigDecimal(1000)

//            println("bid : ${bid.toFloat()} / ask : ${ask.toFloat()}")

                asksWeight[i].layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, bid.toFloat())
                bidsWeight[i].layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, ask.toFloat())
            }
            SocketClient.canFutureBackground = true
        }
    }

    private var _binding: FragmentFutureBinding? = null
    private val binding get() = _binding!!
    private val viewModel = GlobalData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFutureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    /**
     * 초기 설정
     */
    private fun init() {
        initBinding()
        initSocket()
        initString(requireView())
        initDesign()
        initFunction()
    }

    /**
     * 뷰 바인딩 설정
     * */
    private fun initBinding() {
        CoroutineScope(Dispatchers.Main).launch {


            // usdt
            viewModel.showTether.observe(requireActivity()) {
                binding.topTitle.futureTitleTether.text = it
                binding.cardRight.futurePriceTether.text = it
                binding.cardLeft.futureTypeMarketCostTether.text = it
                binding.cardLeft.futureTradeTether.text = it
                binding.cardLeft.futureViewTypeTether.text = it
            }
            // btc
            viewModel.showCoin.observe(requireActivity()) {
                binding.topTitle.futureTitleCoin.text = it
                binding.cardRight.futureAmountCoin.text = it
                binding.cardLeft.futureTypeMarketPriceCoin.text = it
                binding.cardLeft.futureTypeMarketMaxCoin.text = it
                binding.cardLeft.futureViewTypeCoin.text = it
            }
            // 전일대비
            viewModel.pricePercent.observe(requireActivity()) {
                val component = binding.topTitle.futureTitlePercentage
                val change: String = if (it > BigDecimal.ZERO) { // 0보다 큼
                    component.setTextColor(requireActivity().getColor(R.color.buy_color))
                    "+" + it.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%"
                } else if (it < BigDecimal.ZERO) { // 0보다 작음
                    component.setTextColor(requireActivity().getColor(R.color.sell_color))
                    it.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%"
                } else { // 0
                    component.setTextColor(Color.WHITE)
                    it.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%"
                }

                component.text = change
            }

            // 펀딩
            viewModel.fundingRate.observe(requireActivity()) {
                val component = binding.cardTop.futureFunding
                val fundingPercent = (it * BigDecimal(100)).setScale(4, RoundingMode.HALF_UP).toPlainString()

                component.text = fundingPercent + "%"
            }
            // 카운트다운
            viewModel.fundingTime.observe(requireActivity()) {
                val remaining = it - System.currentTimeMillis()
                binding.cardTop.futureCountdown.text = StringBuilder().run {
                    append(String.format(Locale.getDefault(), "%02d", remaining / 3600000))
                    append(":")
                    append(String.format(Locale.getDefault(), "%02d", remaining / 60000 % 60))
                    append(":")
                    append(String.format(Locale.getDefault(), "%02d", remaining / 1000 % 60))
                }
            }

            // 현재가
            binding.cardRight.futureOrderBookScale.text = BigDecimal(1).movePointLeft(viewModel.contractPrice.value!!.scale()).toPlainString()
            viewModel.contractPrice.observe(requireActivity()) {
                val component = binding.cardRight.futureContractPrice
                val before = BigDecimal(component.text.toString())

                component.text = it.toPlainString()

                when {
                    before > it -> { // 가격이 떨어짐
                        component.setTextColor(requireActivity().getColor(R.color.sell_color))
                    }
                    before < it -> { // 가격이 올라감
                        component.setTextColor(requireActivity().getColor(R.color.buy_color))
                    }
                    else -> { // 가격이 동일함
                        component.setTextColor(Color.WHITE)
                    }
                }
            }

            // 배율
            binding.cardTop.futureLeverage.text = String.format("%dx", FutureData.scale.toInt())

            // 시장가
            viewModel.markPrice.observe(requireActivity()) {
                binding.cardRight.futureOrderBookMarketPrice.text = it.setScale(viewModel.contractPrice.value!!.scale(), RoundingMode.HALF_UP).toPlainString()
            }

            // 사용 가능
            binding.cardLeft.futureTradeValue.text = FutureData.futureBalance.setScale(2, RoundingMode.HALF_UP).toPlainString()
            /*viewModel.usableMoney.observe(requireActivity()) {
                binding.cardLeft.futureTradeValue.text = it.setScale(2, RoundingMode.HALF_UP).toPlainString()
            }*/

            // 파랑-초록 호가
            viewModel.bidsAndAsks.observe(requireActivity()) {
                val bids = listOf(
                    Pair(binding.cardRight.futureBuy1Left, binding.cardRight.futureBuy1Right),
                    Pair(binding.cardRight.futureBuy2Left, binding.cardRight.futureBuy2Right),
                    Pair(binding.cardRight.futureBuy3Left, binding.cardRight.futureBuy3Right),
                    Pair(binding.cardRight.futureBuy4Left, binding.cardRight.futureBuy4Right),
                    Pair(binding.cardRight.futureBuy5Left, binding.cardRight.futureBuy5Right),
                    Pair(binding.cardRight.futureBuy6Left, binding.cardRight.futureBuy6Right),
                    Pair(binding.cardRight.futureBuy7Left, binding.cardRight.futureBuy7Right),
                )
                val bidsBackground = listOf(
                    binding.cardRight.futureChartBuy1,
                    binding.cardRight.futureChartBuy2,
                    binding.cardRight.futureChartBuy3,
                    binding.cardRight.futureChartBuy4,
                    binding.cardRight.futureChartBuy5,
                    binding.cardRight.futureChartBuy6,
                    binding.cardRight.futureChartBuy7,
                )

                val asks = listOf(
                    Pair(binding.cardRight.futureSell1Left, binding.cardRight.futureSell1Right),
                    Pair(binding.cardRight.futureSell2Left, binding.cardRight.futureSell2Right),
                    Pair(binding.cardRight.futureSell3Left, binding.cardRight.futureSell3Right),
                    Pair(binding.cardRight.futureSell4Left, binding.cardRight.futureSell4Right),
                    Pair(binding.cardRight.futureSell5Left, binding.cardRight.futureSell5Right),
                    Pair(binding.cardRight.futureSell6Left, binding.cardRight.futureSell6Right),
                    Pair(binding.cardRight.futureSell7Left, binding.cardRight.futureSell7Right)
                )
                val asksBackground = listOf(
                    binding.cardRight.futureChartSell1,
                    binding.cardRight.futureChartSell2,
                    binding.cardRight.futureChartSell3,
                    binding.cardRight.futureChartSell4,
                    binding.cardRight.futureChartSell5,
                    binding.cardRight.futureChartSell6,
                    binding.cardRight.futureChartSell7,
                )

                val bidAndAskSum: MutableList<Pair<BigDecimal, BigDecimal>> = mutableListOf(Pair(BigDecimal.ZERO, BigDecimal.ZERO))
//            val bid = bids[i].price * bidSum[i] / max * BigDecimal(1000)

                // 가격, 금액 입력
                for (i in bids.indices) {
                    val bidPrice = it.first[i].first
                    val bidQty = it.first[i].second
                    val askPrice = it.second[i].first
                    val askQty = it.second[i].second

                    bids[i].first.text = bidPrice
                    bids[i].second.text = bidQty

                    asks[i].first.text = askPrice
                    asks[i].second.text = askQty

                    // 다음 반복문을 위한 최대값 저장 용도
                    bidAndAskSum.add(Pair(bidAndAskSum[i].first + BigDecimal(bidQty), bidAndAskSum[i].second + BigDecimal(askQty)))
                }

//            bidSum[len - 1] * bids[len - 1].price
                val max = if (bidAndAskSum[GlobalData.ORDER_LIST_SIZE].first > bidAndAskSum[GlobalData.ORDER_LIST_SIZE].second) {
                    bidAndAskSum[GlobalData.ORDER_LIST_SIZE].first * BigDecimal(it.first[GlobalData.ORDER_LIST_SIZE - 1].first)
                } else {
                    bidAndAskSum[GlobalData.ORDER_LIST_SIZE].second * BigDecimal(it.second[GlobalData.ORDER_LIST_SIZE - 1].first)
                }

                for (i in bids.indices) {
//                val bid = bids[i].price * bidSum[i] / max * BigDecimal(1000)
                    val bid = BigDecimal(it.first[i].first) * bidAndAskSum[i + 1].first / max * BigDecimal(1000)
                    val ask = BigDecimal(it.second[i].first) * bidAndAskSum[i + 1].second / max * BigDecimal(1000)

                    bidsBackground[i].layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, bid.toFloat())
                    asksBackground[i].layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, ask.toFloat())
                }
            }

            // 왼쪽 시장가
            val dto = FutureData.exchangeInfo?.let { dto ->
                dto.symbols.find { s -> s.symbol == GlobalData.showCoin.value + GlobalData.showTether.value }
            }
            val roundQuantity = dto?.quantityPrecision ?: 0
            val roundPrice = dto?.pricePrecision ?: 0
            viewModel.contractPriceLeft.observe(requireActivity()) {
                binding.cardLeft.futureTypePrice.text = it.setScale(roundPrice, RoundingMode.HALF_UP).toPlainString()

                // 최대비용 계산
                if (it != BigDecimal.ZERO) binding.cardLeft.mainTypeMarketMaxValue.text =
                    (FutureData.futureBalance / it * FutureData.scale).setScale(roundQuantity, RoundingMode.HALF_UP).toPlainString()
            }
        }
    }

    private fun initSocket() {
//        val socket = SocketClient()
//        socket.futureChanger()
//        SocketClient.futureChanger()
    }

    /**
     * 문자열 치환
     */
    private fun initString(view: View) {
        /*// 코인 종류 입력
        listOf<TextView>(
            view.findViewById(R.id.future_type_market_max_unit),
            view.findViewById(R.id.future_amount_coin),
            view.findViewById(R.id.future_type_market_price_left)
        ).forEach {
            it.text = String.format(it.text.toString(), coinType)
        }

        // USD 혹은 USDT 입력
        listOf<TextView>(
            view.findViewById(R.id.future_price_tether),
            view.findViewById(R.id.future_trade_unit),
            view.findViewById(R.id.future_type_market_cost_unit)
        ).forEach {
            it.text = String.format(it.text.toString(), coinUnit)
        }

        // 제목
        val mixed = view.findViewById<TextView>(R.id.future_title_coin)
        mixed.text = String.format(mixed.text.toString(), coinType, coinUnit)*/
    }

    /**
     * 기본 디자인 설정
     */
    private fun initDesign() {
        // 페이저 할당
        innerPager.adapter = FutureViewAdapter(requireActivity())

        (innerPager.getChildAt(0) as RecyclerView).overScrollMode = View.OVER_SCROLL_NEVER
        TabLayoutMediator(innerTab, innerPager) { tab, position ->
            val tab1 = String.format(getString(R.string.future_page1_title), 0)
            val tab2 = String.format(getString(R.string.future_page2_title), FutureData.inputDataList.size)
            val tabCount = listOf(tab1, tab2)
            tab.text = tabCount[position]
        }.attach()

        // 매수(화살표) 이미지 너비 재설정
        imgContainer.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                imgLeft.layoutParams = imgLeft.layoutParams.apply {
                    width = (imgContainer.width / 2 + requireContext().resources.displayMetrics.density * 5).toInt()
                }
                imgContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    /**
     * 컴포넌트 크기 설정
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        // 페이저 높이 맞춤
        innerPager.layoutParams = innerPager.layoutParams.apply {
            height = scrollView.height - status.height - innerTab.height
        }
    }

    /**
     * 기본 기능 설정
     */
    private fun initFunction() {
        // 툴팁 비활성화
        val disableLongClick = View.OnLongClickListener { true }
        innerTab.getTabAt(0)?.view?.setOnLongClickListener(disableLongClick)
        innerTab.getTabAt(1)?.view?.setOnLongClickListener(disableLongClick)

        // 헤더 설정
        scrollView.header = status
        // 헤더가 떨어졌을 때
        scrollView.freeListener = { view ->
            val toLight = ContextCompat.getColor(requireContext(), R.color.default_background)

            // 상태바 색상 변경
            requireActivity().window.apply {
//                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                statusBarColor = toLight
            }

            view.setBackgroundColor(toLight)
        }
        // 헤더가 붙었을 때
        scrollView.stickListener = { view ->
            val toDark = ContextCompat.getColor(requireContext(), R.color.card_color)

            // 상태바 색상 변경
            requireActivity().window.apply {
//                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                statusBarColor = toDark
            }

            view.setBackgroundColor(toDark)
        }

    }

    /**
     * 하단 뷰 페이저 어댑터 클래스
     */
    private class FutureViewAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        private val fragment: List<Fragment> = listOf(
            FutureOrdersFragment(),
            FuturePositionFragment()
        )

        override fun createFragment(position: Int): Fragment {
            return fragment[position]
        }

        override fun getItemCount(): Int {
            return fragment.size
        }
    }
}