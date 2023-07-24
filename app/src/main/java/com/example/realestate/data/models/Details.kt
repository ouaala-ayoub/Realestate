package com.example.realestate.data.models

import com.google.gson.annotations.SerializedName

data class Details(
    @SerializedName("propertyCondition")
    var propertyCondition: String = "",

    @SerializedName("isFurnished")
    var isFurnished: Boolean = false,

    @SerializedName("hasBalcony")
    var hasBalcony: Boolean = false,

    @SerializedName("isNewProperty")
    var isNewProperty: Boolean = false,

    @SerializedName("hasGym")
    var hasGym: Boolean = false,

    @SerializedName("hasSwimmingPool")
    var hasSwimmingPool: Boolean = false,

    @SerializedName("hasParking")
    var hasParking: Boolean = false,

    @SerializedName("numberOfBedrooms")
    var numberOfBedrooms: String = "",

    @SerializedName("floorNumber")
    var floorNumber: String = "",

    @SerializedName("space")
    var space: Number = 0
) {

    fun getAvailableDetails(): List<Pair<String, Any>> {
        val validDetails = mutableListOf<Pair<String, Any>>()

        if (propertyCondition.isNotEmpty()) {
            validDetails.add("Building Age" to propertyCondition)
        }

        if (isFurnished) {
            validDetails.add("Is Furnished" to isFurnished)
        }

        if (hasBalcony) {
            validDetails.add("Has Balcony" to hasBalcony)
        }

        if (isNewProperty) {
            validDetails.add("Is New" to isNewProperty)
        }

        if (hasGym) {
            validDetails.add("Has Gym" to hasGym)
        }

        if (hasSwimmingPool) {
            validDetails.add("Has Swimming Pool" to hasSwimmingPool)
        }

        if (hasParking) {
            validDetails.add("Has Parking" to hasParking)
        }

        if (numberOfBedrooms.isNotEmpty()) {
            validDetails.add("Number of Bedrooms" to numberOfBedrooms)
        }

        if (floorNumber.isNotEmpty()) {
            validDetails.add("Floor Number" to floorNumber)
        }

        if (space.toDouble() > 0) {
            validDetails.add("Space" to space)
        }

        return validDetails
    }
}
