package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class AdditionalInfo(
    @SerializedName("name") val name: String,
    @SerializedName("socials") val socials: List<String> = listOf(),
    @SerializedName("image") var image: String? = null
)

data class PhoneNumber(
    @SerializedName("phone") val phone: String,
)
