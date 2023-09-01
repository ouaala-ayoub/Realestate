package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class PostWithOwnerId(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("description") val description: String,
    @SerializedName("media") val media: List<String>,
    @SerializedName("category") val category: String,
    @SerializedName("price") val price: Int,
    @SerializedName("location") val location: Location,
    @SerializedName("owner") val owner: String, // Assuming ObjectId is represented as String
    @SerializedName("type") val type: String,
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("contact") val contact: Contact,
    @SerializedName("features") var features: MutableList<String>? = null,
    @SerializedName("condition") var condition: String? = null,
    @SerializedName("rooms") var rooms: Number? = null,
    @SerializedName("bathrooms") var bathrooms: Number? = null,
    @SerializedName("floors") var floors: Number? = null,
    @SerializedName("floorNumber") var floorNumber: Number? = null,
    @SerializedName("space") var space: Number? = null
)
