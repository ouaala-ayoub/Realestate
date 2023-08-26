package com.example.realestate.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchParams(
    var title: String? = null,
    var type: String? = null,
    var category: String? = null,
    var price: PriceFilter? = PriceFilter.NONE,
    var location: LocationData? = LocationData(),
    var page: Number? = null
) : Parcelable {
    fun setCountry(country: String?) {
        location?.country = CountriesDataItem(name = country)
    }

    fun setCity(city: String?) {
        location?.city = city
    }

    fun setArea(area: String?) {
        location?.area = area
    }
}

enum class PriceFilter {
    NONE, UP, DOWN
}
