package com.example.realestate.data.remote.network

import com.google.gson.annotations.SerializedName

data class NewFavouritesRequest(
    @SerializedName("favouriteId")
    val favouriteId: String
)