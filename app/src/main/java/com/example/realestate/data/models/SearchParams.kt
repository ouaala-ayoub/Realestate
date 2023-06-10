package com.example.realestate.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchParams(
    var title: String? = null,
    var type: String? = null,
    var category: String? = null,
    var price: Number? = null,
    var location: Location? = null,
    var page: Number? = null
) : Parcelable
