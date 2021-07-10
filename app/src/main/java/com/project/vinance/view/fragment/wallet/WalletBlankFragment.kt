package com.project.vinance.view.fragment.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.vinance.R

/**
 * 아무 것도 표시하지 않는 프래그먼트
 * */
class WalletBlankFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_wallet_blank, container, false)
}