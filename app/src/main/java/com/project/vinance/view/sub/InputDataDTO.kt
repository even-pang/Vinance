package com.project.vinance.view.sub

import com.project.vinance.view.sub.enumerate.ADL
import com.project.vinance.view.sub.enumerate.LongShort
import java.math.BigDecimal

data class InputDataDTO(
    var coinName: Pair<String, Int>,
    var size: String,
    var leverage: String,
    var entryPrice: String,
    var longShort: LongShort,
) {
    companion object {
        fun createEmpty(): InputDataDTO = InputDataDTO(Pair("", 0), "", "", "", LongShort.None)
    }

    var marketPrice: BigDecimal = BigDecimal.ONE
    var margin: BigDecimal = BigDecimal.ONE
    var commission: BigDecimal = BigDecimal.ONE
    var pnl: BigDecimal = BigDecimal.ONE
    var maintenanceMargin: BigDecimal = BigDecimal.ONE
    var roe: BigDecimal = BigDecimal.ONE
    var liquidationPrice: BigDecimal = BigDecimal.ONE
    var danger: BigDecimal = BigDecimal.ONE
    var adl: ADL = ADL.ZERO

    // btc 기준
    var roundingPrice: Int = 0
    // 가격 표시
    var roundingQuantity: Int = 0

    override fun toString(): String =
        "InputDataDTO(marketPrice=$marketPrice, margin=$margin, commission=$commission, pnl=$pnl, maintenanceMargin=$maintenanceMargin, roe=$roe," +
                "liquidationPrice=$liquidationPrice, danger=$danger)"
}