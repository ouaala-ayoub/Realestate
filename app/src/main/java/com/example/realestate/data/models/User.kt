package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String?,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("likes")
    var likes: List<String> = listOf(),
//
//    @SerializedName("isAdmin")
//    val isAdmin: Boolean,

    @SerializedName("communicationMethod")
    val communicationMethod: String
)
