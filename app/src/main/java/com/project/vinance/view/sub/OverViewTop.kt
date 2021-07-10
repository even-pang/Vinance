package com.project.vinance.view.sub

import android.os.Parcelable
import com.project.vinance.view.implementation.OverView
import kotlinx.parcelize.Parcelize

@Parcelize
data class OverViewTop(
    override var spot: Double,
    override var crossMargin: Double,
    override var isolatedMargin: Double,
    override var usdPresent: Double,
    override var coinPresent: Double,
    override var stock: Double,
    override var p2p: Double,
    override var earn: Double,
    override var pool: Double,
    override var tOption: Double,
) : Parcelable, OverView {
    companion object {
        fun createInstance() = OverViewTop(zero, zero, zero, zero, zero, zero, zero, zero, zero, zero)

        private const val zero = 0.00
    }

    override fun get(position: Int): Double {
        return super.get(position) ?: 0.0
    }
}