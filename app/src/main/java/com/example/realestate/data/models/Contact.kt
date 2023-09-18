package com.example.realestate.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    @SerializedName("code") val code: String = "",
    @SerializedName("phone") val phoneNumber: String ="",
    @SerializedName("type") val type: String=""
) : Parcelable
