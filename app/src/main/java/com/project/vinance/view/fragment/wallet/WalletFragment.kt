package com.project.vinance.view.fragment.wallet

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.vinance.R
import com.project.vinance.view.FutureData
import com.project.vinance.view.GlobalData
import com.project.vinance.view.WalletData
import com.project.vinance.view.implementation.ColorChangeListener
import com.project.vinance.view.sub.MyTabLayout
import com.project.vinance.view.sub.NeedToChangeList
import com.project.vinance.view.sub.OverViewBot
import com.project.vinance.view.sub.OverViewTop
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.math.MathContext
import java.util.*
import kotlin.concurrent.timer

/**
 * Wallet(지갑) 영역을 표시하는 프래그먼트
 *
 * @see WalletFragment.newInstance
 * */
class WalletFragment : Fragment(), ColorChangeListener {
    companion object {
        const val PARAM: String = "PARAM"
        private val TAG = WalletFragment::class.java.simpleName

        private var positionTimer: Timer? = null
        private var exchangeTimer: Timer? = null
        private var overViewTimer: Timer? = null

        /**
         * WalletFragment 인스턴스를 생성함
         *
         * @param
         *
         * */
        @JvmStatic
        fun newInstance(param: String) = WalletFragment().apply {
            arguments = Bundle().apply {
                putString(PARAM, param)
            }
        }
    }

    override fun changeStatusColor() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.default_background)
        }
    }

    private val param: String by lazy { arguments?.getString(PARAM) ?: "ERR" }

    private var topTab: TabLayout? = null
    private var topPager: ViewPager2? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        topTab = view.findViewById(R.id.wallet_top_tab)
        topPager = view.findViewById(R.id.wallet_top_pager)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    /**
     * 초기 설정
     */
    private fun init() {
        CoroutineScope(Dispatchers.Main).launch {
            initSocket()
            initDesign()
            initFunction()
        }
    }

    override fun onStart() {
        super.onStart()

        doTimer()
    }

    override fun onStop() {
        super.onStop()

        stopTimer()
    }

    /**
     * 소켓 연결
     * */
    private suspend fun initSocket() {
        withContext(Dispatchers.IO) {
            val busd = GlobalData.getClient(GlobalData.API_URL).getPrice("BUSDUSDT")
            WalletData.exchangeRate = BigDecimal(2) - BigDecimal(busd.price)
        }
    }

    val overView = WalletOverviewFragment.newInstance(OverViewTop.createInstance(), OverViewBot.createInstance())
    val future = WalletFutureFragment.newInstance("")
    private fun calculateWallet() {
        FutureData.let { future ->
            WalletData.walletBalance = BigDecimal.ZERO
            WalletData.totalUnrealizedPnl = BigDecimal.ZERO
            WalletData.marginBalance = BigDecimal.ZERO

            // 지갑 잔고, 총 미실현 PNL
            future.inputDataList.forEach {
                WalletData.walletBalance += it.margin
                WalletData.totalUnrealizedPnl += it.pnl
            }
            WalletData.walletBalance += future.futureBalance

            // 마진 잔고, 총 잔고
            WalletData.marginBalance = WalletData.walletBalance + WalletData.totalUnrealizedPnl
//                Log.d(TAG, "Called: $walletBalance, $totalUnrealizedPnl, $marginBalance")
        }
    }

    private fun walletUiUpdate() {
        WalletData.run {
            calculateWallet()

            future.changeText(0, null)
        }
    }

    fun updateOverview() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            Log.d(TAG, "qqqqq:(${WalletData.walletBalance} * ${WalletData.exchangeRate} + ${WalletData.totalUnrealizedPnl})")
            overView.changeText(0, null)
        }
    }

    private fun doTimer() {
        Log.d(TAG, "doTimer: FIRST")
        exchangeTimer = timer(period = 2000) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val busd = GlobalData.getClient(GlobalData.API_URL).getPrice("BUSDUSDT")

                    WalletData.exchangeRate = BigDecimal(2) - BigDecimal(busd.price)
                    withContext(Dispatchers.Main) { walletUiUpdate() }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        positionTimer = timer(period = 1000) {
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    future.changePosition()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        overViewTimer = timer(period = 3000) {
            try {

                CoroutineScope(Dispatchers.IO).launch {
                    val price = GlobalData.getClient(GlobalData.API_URL).getPrice("BTCBUSD")

                    val future = (WalletData.walletBalance * WalletData.exchangeRate + WalletData.totalUnrealizedPnl)
                        .divide(BigDecimal(price.price), MathContext.DECIMAL128)
                    val cash = FutureData.cashBalance.divide(BigDecimal(price.price), MathContext.DECIMAL128)

                    val total = future + cash
                    val total2 = total * BigDecimal(price.price)

                    WalletData.totalOverviewValue = Pair(WalletData.totalOverviewValue.first, total2)
                    withContext(Dispatchers.Main) { overView.subTotalChange() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun afterCal() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val busd = GlobalData.getClient(GlobalData.API_URL).getPrice("BUSDUSDT")

                WalletData.exchangeRate = BigDecimal(2) - BigDecimal(busd.price)
                calculateWallet()

                val price = GlobalData.getClient(GlobalData.API_URL).getPrice("BTCBUSD")
                //윗값을 먼저 세팅을 할거야 총가치 빼고
                val future = (WalletData.walletBalance * WalletData.exchangeRate + WalletData.totalUnrealizedPnl)
                    .divide(BigDecimal(price.price), MathContext.DECIMAL128)
                val cash = FutureData.cashBalance.divide(BigDecimal(price.price), MathContext.DECIMAL128)

                delay(500)
                //위쪽값 쭉 입히고 아랫값을 입히는 친구
                val price2 = GlobalData.getClient(GlobalData.API_URL).getPrice("BTCBUSD")

                val future2 = future * BigDecimal(price2.price)
                val cash2 = cash * BigDecimal(price2.price)

                //총가치값 입력
                val total = future + cash
                //총가치 오른쪽값 입력
                val total2 = future2 + cash2

                WalletData.totalOverviewValue = Pair(total, total2)
                WalletData.overviewCash = Pair(cash, cash2)
                WalletData.overviewUsdsFuture = Pair(future, future2)

                withContext(Dispatchers.Main) {
                    overView.changeText(0, null)
                }

                Log.d(TAG, "price:(${price.price},  ${price2.price} )")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopTimer() {
        exchangeTimer?.cancel()
        positionTimer?.cancel()
    }

    /**
     * 기본 디자인 설정
     */
    private fun initDesign() {
        // 페이저 할당
        topPager?.adapter = WalletViewAdapter(requireActivity())

//        (topPager?.getChildAt(0) as RecyclerView).overScrollMode = View.OVER_SCROLL_NEVER
        TabLayoutMediator(topTab!!, topPager!!) { tab, position ->
            val titles = listOf(
                R.string.wallet_tab1_title,
                R.string.wallet_tab2_title,
                R.string.wallet_tab3_title,
                R.string.wallet_tab4_title,
                R.string.wallet_tab5_title,
                R.string.wallet_tab6_title,
//                R.string.wallet_tab7_title,
//                R.string.wallet_tab8_title
            )

            tab.text = requireContext().getString(titles[position])
        }.attach()

        topPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    afterCal()
                }
            }
        })

        topTab?.setOnTouchListener { v, event -> false }
        topTab?.isClickable = false
        topTab?.isEnabled = false
        topTab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position > 2) {
                    Log.d(TAG, "================================ NEED SCROLL ================================")
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    /**
     * 기본 기능 설정
     */
    private fun initFunction() {
        // 툴팁 비활성화
        val disableLongClick = View.OnLongClickListener { true }
        for (i in 0 until 7) {
            topTab?.getTabAt(i)?.view?.setOnLongClickListener(disableLongClick)
        }

        // 부드럽게 넘기기 비활성화
        topTab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                topPager?.setCurrentItem(tab.position, false)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        topTab?.setOnClickListener {

        }
    }

    /**
     * 상단 뷰 페이저 어댑터 클래스
     */
    private inner class WalletViewAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        private val fragment: List<Fragment>

        init {
            NeedToChangeList.fragmentList.addAll(listOf(overView, future))

            fragment = listOf(
                overView,
                WalletBlankFragment(),
                WalletBlankFragment(),
                WalletBlankFragment(),
                future,
                WalletBlankFragment(),
//                WalletBlankFragment(),
//                WalletBlankFragment()
            )
        }

        override fun createFragment(position: Int): Fragment {
            return fragment[position]
        }

        override fun getItemCount(): Int {
            return fragment.size
        }
    }
}