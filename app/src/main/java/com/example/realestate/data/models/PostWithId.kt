package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class PostWithoutId(

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

    @SerializedName("category")
    var category: String,

    @SerializedName("details")
    var details: Details? = null
) {
    companion object {
        val emptyPost = PostWithoutId(
            type = Type.RENT.value,
            price = 0,
            category = "",
            media = listOf(),
            location = LocationData(
                "",
                ""
            )
        )
    }
}

