package com.example.realestate.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    @SerializedName("country")
    var country: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("street")
    val street: String? = null
) : Parcelable
