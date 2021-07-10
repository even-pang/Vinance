package com.project.vinance.network.socket.dto

import com.google.gson.annotations.SerializedName

data class MarkPriceDTO(
    @SerializedName("e") val eventType            : String,
    @SerializedName("E") val eventTime            : Long,
    @SerializedName("s") val symbol               : String,
    @SerializedName("p") val markPrice            : String,
    @SerializedName("i") val indexPrice           : String,
    @SerializedName("P") val estimatedSettlePrice : String,
    @SerializedName("r") val fundingRate          : String,
    @SerializedName("T") val nextFundingTime      : Long
)
