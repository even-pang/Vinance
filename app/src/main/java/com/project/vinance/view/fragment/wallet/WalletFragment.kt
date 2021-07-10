package com.project.vinance.view.fragment.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.vinance.R
import com.project.vinance.view.implementation.ColorChangeListener
import com.project.vinance.view.implementation.FocusListenable
import com.project.vinance.view.sub.NeedToChangeList
import com.project.vinance.view.sub.OverViewBot
import com.project.vinance.view.sub.OverViewTop

/**
 * Wallet(지갑) 영역을 표시하는 프래그먼트
 *
 * @see WalletFragment.newInstance
 * */
class WalletFragment : Fragment(), ColorChangeListener {
    companion object {
        const val PARAM: String = "PARAM"

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
        initDesign()
        initFunction()
    }

    /**
     * 기본 디자인 설정
     */
    private fun initDesign() {
        // 페이저 할당
        topPager?.adapter = WalletViewAdapter(requireActivity())

        (topPager?.getChildAt(0) as RecyclerView).overScrollMode = View.OVER_SCROLL_NEVER
        TabLayoutMediator(topTab!!, topPager!!) { tab, position ->
            val titles = listOf(
                R.string.wallet_tab1_title,
                R.string.wallet_tab2_title,
                R.string.wallet_tab3_title,
                R.string.wallet_tab4_title,
                R.string.wallet_tab5_title,
                R.string.wallet_tab6_title,
                R.string.wallet_tab7_title
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
    }

    /**
     * 상단 뷰 페이저 어댑터 클래스
     */
    private inner class WalletViewAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        private val fragment: List<Fragment>

        init {
            val overView = WalletOverviewFragment.newInstance(OverViewTop.createInstance(), OverViewBot.createInstance())
            val future = WalletFutureFragment.newInstance("")
            NeedToChangeList.fragmentList.addAll(listOf(overView, future))

            fragment = listOf(
                overView,
                WalletBlankFragment(),
                WalletBlankFragment(),
                future,
                WalletBlankFragment(),
                WalletBlankFragment(),
                WalletBlankFragment(),
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