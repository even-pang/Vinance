package com.project.vinance.network.rest.vo

data class MarkPriceVO(
    val symbol: String,
    val markPrice: String,
    val indexPrice: String,
    val estimatedSettlePrice: String,
    val lastFundingRate: String,
    val interestRate: String,
    val nextFundingTime: Long,
    val time: Long
)
