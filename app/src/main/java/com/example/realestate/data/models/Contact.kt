package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("call") var call: String? = "",
    @SerializedName("whatsapp") var whatsapp: String? = ""
)
