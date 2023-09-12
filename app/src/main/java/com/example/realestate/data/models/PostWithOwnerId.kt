package com.example.realestate.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostWithOwnerId(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("description") var description: String,
    @SerializedName("media") val media: List<String>,
    @SerializedName("category") var category: String,
    @SerializedName("price") var price: String,
    @SerializedName("period") var period: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("location") val location: Location,
    @SerializedName("owner") val owner: String, // Assuming ObjectId is represented as String
    @SerializedName("type") var type: String,
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("contact") var contact: Contact,
    @SerializedName("features") var features: MutableList<String>? = null,
    @SerializedName("condition") var condition: String? = null,
    @SerializedName("rooms") var rooms: Number? = null,
    @SerializedName("bathrooms") var bathrooms: Number? = null,
    @SerializedName("floors") var floors: Number? = null,
    @SerializedName("floorNumber") var floorNumber: Number? = null,
    @SerializedName("space") var space: Number? = null
) : Parcelable {
}
