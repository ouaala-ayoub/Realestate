package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: Phone,

    @SerializedName("isAdmin")
    val isAdmin: Boolean,

    @SerializedName("communicationMethod")
    val string: String
)
