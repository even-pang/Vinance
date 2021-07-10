package com.project.vinance.view.implementation

import java.lang.Exception

interface OverView {
    val spot: Double?
    val crossMargin: Double?
    val isolatedMargin: Double?
    val usdPresent: Double?
    val coinPresent: Double?
    val stock: Double?
    val p2p: Double?
    val earn: Double?
    val pool: Double?
    val tOption: Double?

    operator fun get(position: Int): Double? {
        return when (position) {
            0 -> spot
            1 -> crossMargin
            2 -> isolatedMargin
            3 -> usdPresent
            4 -> coinPresent
            5 -> stock
            6 -> p2p
            7 -> earn
            8 -> pool
            9 -> tOption
            else -> 0.0
        }
    }
}