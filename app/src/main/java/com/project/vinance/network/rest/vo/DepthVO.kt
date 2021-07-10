package com.project.vinance.network.rest.vo

import com.google.gson.annotations.SerializedName

data class DepthVO(
    val lastUpdateId: Long,
    @SerializedName("E") val messageOutputTime: Long,
    @SerializedName("T") val transactionTime: Long,
    val bids: List<List<String>>,
    val asks: List<List<String>>
)