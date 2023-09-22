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
    val image: String? = "https://upload.wikimedia.org/wikipedia/commons/2/2f/Flag_of_the_United_Nations.svg",

    @SerializedName("name")
    var name: String? = null
) : Parcelable