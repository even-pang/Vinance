package com.project.vinance.view.fragment.wallet

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.vinance.view.InputActivity
import com.project.vinance.R
import com.project.vinance.databinding.FragmentWalletFutureBinding
import com.project.vinance.view.FutureData
import com.project.vinance.view.WalletData
import com.project.vinance.view.implementation.TextChangeListenable
import java.lang.Exception
import java.math.RoundingMode
import java.util.*
import kotlin.math.roundToInt

class WalletFutureFragment : Fragment(), TextChangeListenable {
    companion object {
        private val TAG = WalletFutureFragment::class.java.simpleName
        private const val PARAM = "PARAM"
        private var i = 0; get() = field++

        @JvmStatic
        fun newInstance(param: String) = WalletFutureFragment().apply {
            arguments = Bundle().apply {
                putString(PARAM, param)
            }
        }
    }

    private val param: String by lazy { arguments?.getString(PARAM) ?: "ERR" }
    private var myView: View? = null

    private lateinit var innerTab: TabLayout
    private lateinit var innerPager: ViewPager2

    private var _binding: FragmentWalletFutureBinding? = null
    private val binding: FragmentWalletFutureBinding get() = _binding!!

    private val walletPosition = WalletFuturePositionsFragment()

    override fun changeText(resId: Int, data: String?) {
        myView?.also { itemView ->
            WalletData.let {
                val wallet = it.walletBalance * it.exchangeRate
                val pnl = it.totalUnrealizedPnl
                val margin = wallet + pnl
                // 마진 잔고 - 위
                itemView.findViewById<TextView>(R.id.wallet_future_margin_balance_value).text =
                    margin.setScale(4, RoundingMode.HALF_UP).toPlainString()
                // 마진 잔고 - 아래
                itemView.findViewById<TextView>(R.id.wallet_future_margin_balance_value_second).text =
                    "≈ \$${margin.setScale(2, RoundingMode.HALF_UP).toPlainString()}"

                // 지갑 잔고 - 위
                itemView.findViewById<TextView>(R.id.wallet_future_wallet_balance_value).text =
                    wallet.setScale(4, RoundingMode.HALF_UP).toPlainString()
                // 지갑 잔고 - 아래
                itemView.findViewById<TextView>(R.id.wallet_future_wallet_balance_value_second).text =
                    "≈ \$${wallet.setScale(2, RoundingMode.HALF_UP).toPlainString()}"

                // 총 미실현 PNL - 위
                itemView.findViewById<TextView>(R.id.wallet_future_total_unrealized_pnl_value).text =
                    pnl.setScale(4, RoundingMode.HALF_UP).toPlainString()
                // 총 미실현 PNL - 아래
                itemView.findViewById<TextView>(R.id.wallet_future_total_unrealized_pnl_value_second).text =
                    "≈ \$${ pnl.setScale(4, RoundingMode.HALF_UP).toPlainString()}00"

                // 총 잔고 - 왼쪽
                itemView.findViewById<TextView>(R.id.wallet_future_title_value).text =
                    margin.setScale(4, RoundingMode.HALF_UP).toPlainString()
                // 총 잔고 - 오른쪽
                itemView.findViewById<TextView>(R.id.wallet_future_subtitle_value).text =
                    "≈ \$${ margin.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
            }
        }
    }

    fun changePosition() {
        walletPosition.notifyPosition()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentWalletFutureBinding.inflate(inflater, container, false)

        val view = inflater.inflate(R.layout.fragment_wallet_future, container, false)

        innerTab = view.findViewById(R.id.wallet_future_inner_tab)
        innerPager = view.findViewById(R.id.wallet_future_inner_pager)

        val text = view.findViewById<TextView>(R.id.wallet_future_revenue_text)

        text.text = String.format(Locale.getDefault(), "%s 수익 기간", FutureData.revenuePeriod)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myView = view
        super.onViewCreated(view, savedInstanceState)

        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 초기 설정
     */
    private fun init() {
        initDesign()
        initFunction()
        initPut()
    }

    private fun initPut() {
        changeText(0, null)
    }

    /**
     * 기본 디자인 설정
     */
    private fun initDesign() {
        // 페이저 할당
        innerPager.adapter = WalletFutureViewAdapter(requireActivity())
//        innerPager[0].overScrollMode = View.OVER_SCROLL_NEVER
//        (innerPager.getChildAt(0) as RecyclerView).overScrollMode = View.OVER_SCROLL_NEVER

        TabLayoutMediator(innerTab, innerPager) { tab, position ->
            val titles = listOf(
                R.string.wallet_future_tab1_title,
                R.string.wallet_future_tab2_title,
//                R.string.wallet_future_tab3_title,
            )

            tab.text = requireContext().getString(titles[position])
        }.attach()
    }

    /**
     * 기본 기능 설정
     */
    private fun initFunction() {
        // 툴팁 비활성화
        val disableLongClick = View.OnLongClickListener { true }
        for (i in 0 until 3) {
            innerTab.getTabAt(i)?.view?.setOnLongClickListener(disableLongClick)
        }

        // 입력 액티비티 열기
        myView?.findViewById<ImageView>(R.id.wallet_future_go_next)?.setOnClickListener {
            startActivity(Intent(requireActivity(), InputActivity::class.java))
        }


        // 페이저 높이 설정
        println("height : " + resources.displayMetrics.heightPixels)
        println("density : ${resources.displayMetrics.density}")
        innerPager.layoutParams = innerPager.layoutParams.apply {
            height = resources.displayMetrics.heightPixels - (resources.displayMetrics.density * 215).roundToInt()
        }
    }

    /**
     * 하단 뷰 페이저 어댑터 클래스
     */
    private inner class WalletFutureViewAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        private val fragment: List<Fragment> = listOf(
            walletPosition,
            WalletFutureAssetFragment(),
            WalletFutureCollateralsFragment()
        )

        override fun createFragment(position: Int): Fragment {
            return fragment[position]
        }

        override fun getItemCount(): Int {
            return fragment.size
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
