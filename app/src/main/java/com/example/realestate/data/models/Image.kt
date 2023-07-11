package com.example.realestate.data.models

import android.net.Uri

data class Media(
    var uriHolders: MutableList<UriHolder>,
    var selectedPosition: Int = 0
) {
    fun countNonNullElements(): Int {
        return uriHolders.count { it.uri != null }
    }
}

data class UriHolder(
    var uri: Uri? = null,
    var uploadProgress: Int? = null
)