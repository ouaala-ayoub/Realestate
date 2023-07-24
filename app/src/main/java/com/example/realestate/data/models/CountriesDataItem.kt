package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class CountriesDataItem(
    @SerializedName("code")
    val code: String,

    @SerializedName("dial_code")
    val dial_code: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("name")
    val name: String
)