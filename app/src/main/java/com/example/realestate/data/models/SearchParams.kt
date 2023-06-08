package com.example.realestate.data.models

data class SearchParams(
    val title: String? = null,
    val type: String? = null,
    val category: String? = null,
    val price: Number? = null,
    val location: Location? = null,
    val page: Number? = null
)
