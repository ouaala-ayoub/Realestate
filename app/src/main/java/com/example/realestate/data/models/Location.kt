package com.example.realestate.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    @SerializedName("country")
    var country: String? = null,

    @SerializedName("city")
    var city: String? = null,

    @SerializedName("street")
    var street: String? = null
) : Parcelable
