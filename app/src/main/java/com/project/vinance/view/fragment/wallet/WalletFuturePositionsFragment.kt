package com.project.vinance.view.fragment.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.project.vinance.R
import com.project.vinance.view.recycler.RecycleWalletFuturePosition

class WalletFuturePositionsFragment : Fragment() {
    private var itemView: View? = null
    private var positionAdapter: RecycleWalletFuturePosition? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_wallet_future_positions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemView = view

        init()
    }

    /**
     * 실행 순서 정의
     * */
    private fun init() {
        itemView?.let { itemView ->
            positionAdapter = RecycleWalletFuturePosition(requireContext())

            val recyclerView = itemView.findViewById<RecyclerView>(R.id.wallet_future_position_recycler_view)
            recyclerView.adapter = positionAdapter
        }
    }

    fun notifyPosition() {
        positionAdapter?.notifyDataSetChanged()
    }
}