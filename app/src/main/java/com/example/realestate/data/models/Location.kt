package com.example.realestate.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationData(
    @SerializedName("country")
    var country: CountriesDataItem? = null,

    @SerializedName("city")
    var city: String? = null,

    @SerializedName("area")
    var area: String? = null
) : Parcelable

data class Location(
    @SerializedName("country")
    var country: String? = null,

    @SerializedName("city")
    var city: String? = null,

    @SerializedName("area")
    var area: String? = null
)
