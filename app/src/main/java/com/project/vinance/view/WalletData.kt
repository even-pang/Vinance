package com.project.vinance.view

import java.math.BigDecimal

object WalletData {
    var marginBalance: BigDecimal = BigDecimal.ZERO
    var walletBalance: BigDecimal = BigDecimal.ZERO
    var totalUnrealizedPnl: BigDecimal = BigDecimal.ZERO
    var exchangeRate: BigDecimal = BigDecimal.ONE

    var totalOverviewValue: Pair<BigDecimal, BigDecimal> = Pair(BigDecimal.ZERO, BigDecimal.ZERO)
    var overviewCash: Pair<BigDecimal, BigDecimal> = Pair(BigDecimal.ZERO, BigDecimal.ZERO)
    var overviewUsdsFuture: Pair<BigDecimal, BigDecimal> = Pair(BigDecimal.ZERO, BigDecimal.ZERO)
}