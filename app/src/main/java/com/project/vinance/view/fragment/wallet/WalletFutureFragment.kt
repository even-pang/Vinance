package com.project.vinance.view.fragment.wallet

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.vinance.R
import com.project.vinance.view.implementation.TextChangeListenable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.roundToInt

class WalletFutureFragment : Fragment(), TextChangeListenable {
    companion object {
        private const val PARAM = "PARAM"

        @JvmStatic
        fun newInstance(param: String) = WalletFutureFragment().apply {
            arguments = Bundle().apply {
                putString(PARAM, param)
            }
        }
    }

    private val param: String by lazy { arguments?.getString(PARAM) ?: "ERR" }

    private val innerTab: TabLayout by lazy { requireActivity().findViewById(R.id.wallet_future_inner_tab) }
    private val innerPager: ViewPager2 by lazy { requireActivity().findViewById(R.id.wallet_future_inner_pager) }

    override fun changeText(resId: Int, data: String?) {
        // TODO
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_future, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        CoroutineScope(Dispatchers.Main).launch {
            initDesign()
            initFunction()
        }
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
            val titles = listOf(R.string.wallet_future_tab1_title, R.string.wallet_future_tab2_title, R.string.wallet_future_tab3_title)

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
            WalletFuturePositionsFragment(),
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
}