package com.project.vinance.network.socket.dto

import com.google.gson.annotations.SerializedName

data class PartialBookDepthDTO(
    @SerializedName("e")  val eventType         : String,
    @SerializedName("E")  val eventTime         : Long,
    @SerializedName("T")  val transactionTime   : Long,
    @SerializedName("s")  val symbol            : String,
    @SerializedName("U")  val firstUpdateId     : Long,
    @SerializedName("u")  val finalUpdateId     : Long,
    @SerializedName("pu") val finalUpdateIdLast : Long,
    @SerializedName("b")  val bidsUpdated       : List<List<String>>,
    @SerializedName("a")  val asksUpdated       : List<List<String>>
)