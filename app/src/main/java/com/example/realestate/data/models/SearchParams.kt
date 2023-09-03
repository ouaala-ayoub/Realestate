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
    var page: String? = null,
    var features: MutableList<String>? = null,
    var condition: String? = null
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

    fun initialiseFeatures() {
        features = mutableListOf()
    }

    fun addFeature(element: String) {
        features?.add(element)
    }

    fun deletedFeature(element: String) {
        features?.remove(element)
    }

}

enum class PriceFilter {
    NONE, UP, DOWN
}
