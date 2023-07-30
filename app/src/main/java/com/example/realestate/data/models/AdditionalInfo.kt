package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class AdditionalInfo(
    @SerializedName("name")
    val name: String,

    @SerializedName("communicationMethod")
    val commMethod: String
)

data class PhoneNumber(
    @SerializedName("phone")
    val phone: String,
)
