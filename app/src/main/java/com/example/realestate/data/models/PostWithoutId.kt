package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class PostWithoutId(

    @SerializedName("description") var description: String,
    @SerializedName("media") var media: List<String>,
    @SerializedName("category") var category: String,
    @SerializedName("price") var price: Number,
    @SerializedName("period") var period: String? = null,
    @SerializedName("location") var location: Location,
    @SerializedName("type") var type: String,
    @SerializedName("contact") var contact: Contact,
    @SerializedName("features") var features: MutableList<String>? = null,
    @SerializedName("condition") var condition: String? = null,
    @SerializedName("rooms") var rooms: String? = null,
    @SerializedName("elevators") var elevators: String? = null,
    @SerializedName("floors") var floors: String? = null,
    @SerializedName("floorNumber") var floorNumber: String? = null,
    @SerializedName("space") var space: String? = null

) {
    companion object {
        val emptyPost = PostWithoutId(
            type = Type.RENT.value,
            price = 0,
            category = "",
            media = listOf(),
            location = Location(
                "",
                ""
            ),
            description = "",
            contact = Contact()
        )
    }

    fun fillFields() {}
}

