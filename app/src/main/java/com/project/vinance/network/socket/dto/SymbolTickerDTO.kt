package com.project.vinance.network.socket.dto

import com.google.gson.annotations.SerializedName

data class SymbolTickerDTO(
    @SerializedName("e") val eventType: String,
    @SerializedName("E") val eventTime: Long,
    @SerializedName("s") val symbol: String,
    @SerializedName("p") val priceChange: String,
    @SerializedName("P") val priceChangePercent: String,
    @SerializedName("w") val weightedAveragePrice: String,
    @SerializedName("c") val lastPrice: String,
    @SerializedName("Q") val lastQuantity: String,
    @SerializedName("o") val openPrice: String,
    @SerializedName("h") val highPrice: String,
    @SerializedName("l") val lowPrice: String,
    @SerializedName("v") val totalTradedBaseAssetVolume: String,
    @SerializedName("q") val totalTradedQuoteAssetVolume: String,
    @SerializedName("O") val statisticsOpenTime: Long,
    @SerializedName("C") val statisticsCloseTime: Long,
    @SerializedName("F") val firstTradeId: Long,
    @SerializedName("L") val lastTradeId: Long,
    @SerializedName("n") val totalNumberOfTrade: Long
)
