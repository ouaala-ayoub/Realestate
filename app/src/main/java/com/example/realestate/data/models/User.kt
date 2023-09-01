package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") var name: String?,
    @SerializedName("email") val email: String,
    @SerializedName("socials") val socials: List<String>? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("likes") var likes: List<String> = listOf(),
)
