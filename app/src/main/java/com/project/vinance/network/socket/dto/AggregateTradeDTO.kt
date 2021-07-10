package com.project.vinance.network.socket.dto

import com.google.gson.annotations.SerializedName

data class AggregateTradeDTO(
    @SerializedName("e") val eventType          : String,
    @SerializedName("E") val eventTime          : Long,
    @SerializedName("s") val symbol             : String,
    @SerializedName("a") val aggregateTradeId   : Long,
    @SerializedName("p") val price              : String,
    @SerializedName("q") val quantity           : String,
    @SerializedName("f") val firstTradeId       : Long,
    @SerializedName("l") val lastTradeId        : Long,
    @SerializedName("T") val tradeTime          : Long,
    @SerializedName("m") val isBuyerMarketMaker : Boolean
)
