package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Post(

    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("media")
    var media: List<String>,

    @SerializedName("type")
    var type: String,

    @SerializedName("price")
    var price: Number,

    @SerializedName("location")
    var location: LocationData,

    @SerializedName("ownerId")
    var ownerId: String,

    @SerializedName("category")
    var category: String
) {
    companion object {
        val emptyPost = Post(
            type = Type.RENT.value,
            price = 0,
            category = "",
            media = listOf(),
            ownerId = "",
            location = LocationData(
                "",
                ""
            )
        )
    }
}
