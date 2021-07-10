package com.project.vinance.network.socket.dto

import com.google.gson.annotations.SerializedName

data class EventNameDTO(
    @SerializedName("e") val eventName: String?,
)