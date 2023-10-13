package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class NewsElement(
    @SerializedName("contents") val contents: List<String>,
    @SerializedName("image") val image: String,
    @SerializedName("title") val title: String
)