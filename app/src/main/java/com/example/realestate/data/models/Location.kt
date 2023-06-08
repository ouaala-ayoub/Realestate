package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("country")
    val country: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("street")
    val street: String? = null
)
