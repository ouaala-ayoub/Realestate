package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("postId")
    val postId: String,

    @SerializedName("reason")
    val reason: String,

    @SerializedName("message")
    val message: String? = null
)
