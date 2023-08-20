package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class PostWithoutId(

    @SerializedName("description") var description: String,
    @SerializedName("media") var media: List<String>,
    @SerializedName("category") var category: String,
    @SerializedName("details") var details: Map<String ,Any>? = null,
    @SerializedName("price") var price: Int,
    @SerializedName("location") var location: Location,
    @SerializedName("type") var type: String,
    @SerializedName("contact") var contact: Contact,

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
}

