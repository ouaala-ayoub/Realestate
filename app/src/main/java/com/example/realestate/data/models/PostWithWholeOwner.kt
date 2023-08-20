package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class PostWithWholeOwner(

    @SerializedName("_id") val id: String? = null,
    @SerializedName("description") val description: String,
    @SerializedName("media") val media: List<String>,
    @SerializedName("category") val category: String,
    @SerializedName("details") val details: Details? = null,
    @SerializedName("price") val price: Int,
    @SerializedName("location") val location: Location,
    @SerializedName("owner") val owner: User?, // Assuming ObjectId is represented as String
    @SerializedName("type") val type: String,
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("contact") val contact: Contact

)
