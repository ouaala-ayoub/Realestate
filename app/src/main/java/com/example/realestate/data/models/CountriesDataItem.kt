package com.example.realestate.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountriesDataItem(
    @SerializedName("code")
    var code: String? = null,

    @SerializedName("dial_code")
    val dial_code: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("name")
    var name: String? = null
) : Parcelable