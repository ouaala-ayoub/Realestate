package com.example.realestate.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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
    var location: PostLocationData,

    @SerializedName("category")
    var category: String,

    @SerializedName("details")
    var details: Map<String, Any>? = null
) {
    companion object {
        val emptyPost = PostWithoutId(
            type = Type.RENT.value,
            price = 0,
            category = "",
            media = listOf(),
            location = PostLocationData(
                "",
                ""
            )
        )
    }
}

@Parcelize
data class PostLocationData(
    @SerializedName("country")
    var country: String? = null,

    @SerializedName("city")
    var city: String? = null,

    @SerializedName("area")
    var area: String? = null
) : Parcelable
