package com.project.vinance.view.sub

import android.os.Parcelable
import com.project.vinance.view.implementation.OverView
import kotlinx.parcelize.Parcelize

@Parcelize
data class OverViewBot(
    override var spot: Double?,
    override var crossMargin: Double?,
    override var isolatedMargin: Double?,
    override var usdPresent: Double?,
    override var coinPresent: Double?,
    override var stock: Double?,
    override var p2p: Double?,
    override var earn: Double?,
    override var pool: Double?,
    override var tOption: Double?,
) : Parcelable, OverView {
    companion object {
        fun createInstance() =
            OverViewBot(null, null, null, null, null, null, null, null, null, null)
    }
}