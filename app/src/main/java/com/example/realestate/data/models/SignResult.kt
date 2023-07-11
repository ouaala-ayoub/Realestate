package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class SignResult(
    @SerializedName("signature")
    val signature: String,

    @SerializedName("timestamp")
    val timeStamp: Long,
)