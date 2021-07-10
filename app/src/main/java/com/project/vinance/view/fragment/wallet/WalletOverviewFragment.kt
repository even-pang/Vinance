package com.project.vinance.view.fragment.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.project.vinance.R
import com.project.vinance.view.implementation.TextChangeListenable
import com.project.vinance.view.sub.OverViewBot
import com.project.vinance.view.sub.OverViewTop
import java.text.DecimalFormat

class WalletOverviewFragment : Fragment(), TextChangeListenable {
    companion object {
        private const val TOP_DATA = "TOP_DATA"
        private const val BOT_DATA = "BOT_DATA"
        private const val COIN_UNIT = "COIN_UNIT"

        @JvmStatic
        fun newInstance(topTop: OverViewTop, bottomTop: OverViewBot) = WalletOverviewFragment().apply {
            arguments = Bundle().apply {
                putParcelable(TOP_DATA, topTop)
                putParcelable(BOT_DATA, bottomTop)
                // TODO 추가 인자 처리 필요
                putString(COIN_UNIT, "BTC")
            }
        }
    }

    /** first : top, second : bottom */
    private var portfolioView: List<Pair<TextView, TextView>> = emptyList()

    /** first : top, second: bottom */
    private val portfolioData: Pair<OverViewTop, OverViewBot> by lazy {
        Pair(
            arguments?.getParcelable(TOP_DATA) ?: OverViewTop.createInstance(),
            arguments?.getParcelable(BOT_DATA) ?: OverViewBot.createInstance()
        )
    }

    private val coinUnit: String by lazy { arguments?.getString(COIN_UNIT) ?: "" }

    override fun changeText(resId: Int, data: String?) {
        activity?.findViewById<TextView>(resId)?.text = data
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wallet_overview, container, false)

        portfolioView = listOf(
            Pair(view.findViewById(R.id.wallet_overview_portfolio_0), view.findViewById(R.id.wallet_overview_portfolio_0_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_1), view.findViewById(R.id.wallet_overview_portfolio_1_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_2), view.findViewById(R.id.wallet_overview_portfolio_2_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_3), view.findViewById(R.id.wallet_overview_portfolio_3_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_4), view.findViewById(R.id.wallet_overview_portfolio_4_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_5), view.findViewById(R.id.wallet_overview_portfolio_5_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_6), view.findViewById(R.id.wallet_overview_portfolio_6_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_7), view.findViewById(R.id.wallet_overview_portfolio_7_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_8), view.findViewById(R.id.wallet_overview_portfolio_8_sub)),
            Pair(view.findViewById(R.id.wallet_overview_portfolio_9), view.findViewById(R.id.wallet_overview_portfolio_9_sub)),
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        for (i in portfolioView.indices) {
            val currentTop = portfolioView[i].first
            val currentTopValue = portfolioData.first[i]

            // 받은 값이 0인지 여부에 따른 분기 처리
            currentTop.text = if (currentTopValue == 0.0) String.format("%.2f %s", 0.0, coinUnit)
            else String.format("%s %s", currentTopValue.toBigDecimal().toPlainString(), coinUnit)

            val currentBot = portfolioView[i].second
            if (currentTopValue == 0.0) {
//                currentBot.visibility = View.GONE
            } else {
                currentBot.text = String.format("≈ \$%s %s", DecimalFormat("0.#").format(portfolioData.second[i]), coinUnit)
            }
        }
    }
}