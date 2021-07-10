package com.project.vinance.view.fragment.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.vinance.R
import com.project.vinance.view.recycler.RecycleWalletFutureCollateral
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalletFutureCollateralsFragment : Fragment() {
    companion object {
        private const val PARAM = "PARAM"

        @JvmStatic
        fun newInstance(param1: String) =
            WalletFutureCollateralsFragment().apply {
                arguments = Bundle().apply {
                    putString(PARAM, param1)
                }
            }
    }

    private val param: String by lazy { arguments?.getString(PARAM) ?: "ERR" }

    private val pager: RecyclerView by lazy { requireActivity().findViewById(R.id.wallet_future_collaterals_recycler) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_future_collaterals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            delay(1)
            pager.adapter = RecycleWalletFutureCollateral()
        }
    }
}