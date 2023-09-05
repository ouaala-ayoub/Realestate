package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("post")
    val postId: String,

    @SerializedName("reasons")
    val reasons: List<String>,

    @SerializedName("message")
    var message: String? = null
)
