package com.example.realestate.data.models

import android.net.Uri

data class Images(
    var imageUris: MutableList<Uri?>,
    var selectedPosition: Int = 0
)
