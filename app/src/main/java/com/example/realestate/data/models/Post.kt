package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Post(

    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("media")
    var media: List<String>,

    @SerializedName("type")
    val type: String,

    @SerializedName("price")
    val price: Number,

    @SerializedName("location")
    val location: Location,

    @SerializedName("ownerId")
    val ownerId: String,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("category")
    val category: String
) {
    companion object {
        val emptyPost = Post(
            title = "",
            type = Type.RENT.value,
            price = 0,
            category = "",
            currency = "",
            media = listOf(),
            ownerId = "",
            location = Location(
                "",
                ""
            )
        )
    }
}
