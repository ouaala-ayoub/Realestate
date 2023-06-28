package com.example.realestate.data.models

import android.net.Uri

data class Media(
    var uris: MutableList<Uri?>,
    var selectedPosition: Int = 0
) {
    fun countNonNullElements(): Int {
        return uris.count { it != null }
    }
}
