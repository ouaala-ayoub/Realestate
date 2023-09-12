package com.example.realestate.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    @SerializedName("call") var call: String? = "",
    @SerializedName("whatsapp") var whatsapp: String? = ""
) : Parcelable
