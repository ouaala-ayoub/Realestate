package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("postId")
    val postId: String,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("reasons")
    val reasons: List<String>,

    @SerializedName("message")
    var message: String? = null
)
