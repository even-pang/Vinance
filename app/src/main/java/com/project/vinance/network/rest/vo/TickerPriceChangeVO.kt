package com.project.vinance.network.rest.vo

data class TickerPriceChangeVO(
    val symbol: String,
    val priceChange: String,
    val priceChangePercent: String,
    val weightedAvgPrice: String,
    val lastPrice: String,
    val lastQty: String,
    val openPrice: String,
    val highPrice: String,
    val lowPrice: String,
    val volume: String,
    val quoteVolume: String,
    val openTime: Long,
    val closeTime: Long,
    val firstId: Long,
    val lastId: Long,
    val count: Long
)
