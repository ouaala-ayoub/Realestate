package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Phone(
    @SerializedName("code")
    val code: String,

    @SerializedName("number")
    val number: String,

    @SerializedName("_id")
    val id: String
)
