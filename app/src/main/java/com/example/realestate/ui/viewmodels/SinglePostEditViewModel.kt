package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.*
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest
import com.example.realestate.utils.isNumeric

class SinglePostEditViewModel(
    private val repository: PostsRepository,
    private val staticDataRepository: StaticDataRepository,
    private val post: PostWithOwnerId
) : CountriesModel(staticDataRepository) {

    companion object {
        private const val TAG = "SinglePostEditViewModel"
    }

    // Define MutableLiveData variables for your attributes
    private val mutableFeatures = MutableLiveData<MutableList<String>?>(post.features)
    val mutableCondition = MutableLiveData<String?>(post.type)
    val mutableType = MutableLiveData<String>(post.condition)
    val mutableRooms = MutableLiveData(post.rooms.toString())
    val mutableElevators = MutableLiveData(post.elevators.toString())
    val mutableFloors = MutableLiveData(post.floors.toString())
    val mutableFloorNumber = MutableLiveData(post.floorNumber.toString())
    private val mutableSpace = MutableLiveData(post.space.toString())

    private val mutableContactType = MutableLiveData(post.contact.type)
    val mutablePhoneNumber = MutableLiveData(post.contact.phoneNumber)

    //    val mutableCategory = MutableLiveData(post.category)
    val mutablePrice = MutableLiveData(post.price)
    val mutablePeriod = MutableLiveData<String?>(post.period)
    val mutableCountry = MutableLiveData<String>(post.location.country)
    val mutableCity = MutableLiveData<String>(post.location.city)
    val mutableArea = MutableLiveData<String>(post.location.area)
    val mutableDescription = MutableLiveData<String>(post.description)

    val isDataValid = MediatorLiveData<Boolean>().apply {
        addSource(mutableFeatures) { updateValidity() }
        addSource(mutableCondition) { updateValidity() }
        addSource(mutableRooms) { updateValidity() }
        addSource(mutableElevators) { updateValidity() }
        addSource(mutableFloors) { updateValidity() }
        addSource(mutableFloorNumber) { updateValidity() }
        addSource(mutableSpace) { updateValidity() }
//        addSource(mutableCategory) { updateValidity() }
        addSource(mutablePrice) { updateValidity() }
        addSource(mutablePeriod) { updateValidity() }
        addSource(mutableContactType) { updateValidity() }
        addSource(mutablePhoneNumber) { updateValidity() }
        addSource(mutableCountry) { updateValidity() }
        addSource(mutableCity) { updateValidity() }
        addSource(mutableArea) { updateValidity() }
        addSource(mutableDescription) { updateValidity() }
    }

    fun updateSelectedOptions(isWhatsAppChecked: Boolean, isCallChecked: Boolean) {
        mutableContactType.value =
            if (isWhatsAppChecked && isCallChecked) {
                ContactType.Both.value
            } else if (isWhatsAppChecked) {
                ContactType.WHATSAPP.value
            } else if (isCallChecked) {
                ContactType.CALL.value
            } else {
                ""
            }
    }

    private fun updateValidity() {
        // Check the data in each MutableLiveData and update isDataValid accordingly
        val isValidDetails = !mutableFeatures.value.isNullOrEmpty() &&
                !mutableCondition.value.isNullOrBlank() &&
                !mutableRooms.value.isNullOrBlank() &&
                !mutableElevators.value.isNullOrBlank() &&
                !mutableFloors.value.isNullOrBlank() &&
                !mutableFloorNumber.value.isNullOrBlank() &&
                !mutableSpace.value.isNullOrBlank()
        val isValidData =
//            !mutableCategory.value.isNullOrBlank() &&
            !mutablePrice.value.isNullOrBlank() &&
                    (!mutablePeriod.value.isNullOrBlank() || mutableType.value != Type.RENT.value) &&
                    !mutableContactType.value.isNullOrBlank() &&
                    !mutablePhoneNumber.value.isNullOrBlank() &&
                    !mutableCountry.value.isNullOrBlank() &&
                    !mutableCity.value.isNullOrBlank() &&
                    !mutableDescription.value.isNullOrBlank()

        isDataValid.value = if (extras.contains(post.category)) {
            isValidData && isValidDetails
        } else {
            isValidData
        }


    }

    // Define LiveData getters for your attributes
    val featuresLd: LiveData<MutableList<String>?> = mutableFeatures
    val typeLd: LiveData<String> = mutableType
    val conditionLd: LiveData<String?> = mutableCondition
    val roomsLd: LiveData<String> = mutableRooms
    val elevatorsLd: LiveData<String> = mutableElevators
    val floorsLd: LiveData<String> = mutableFloors
    val floorNumberLd: LiveData<String> = mutableFloorNumber
    val spaceLd: LiveData<String?> = mutableSpace
    val contactType: LiveData<String?> = mutableContactType
    val phoneNumber: LiveData<String?> = mutablePhoneNumber

    //    val categoryLd: LiveData<String> = mutableCategory
    val priceLd: LiveData<String?> = mutablePrice
    val periodLd: LiveData<String?> = mutablePeriod
    val countryLd: LiveData<String> = mutableCountry
    val cityLd: LiveData<String> = mutableCity
    val areaLd: LiveData<String> = mutableArea
    val descriptionLd: LiveData<String> = mutableDescription

    private val _loading = MutableLiveData<Boolean>()
    private val _response = MutableLiveData<MessageResponse?>()

    val loading: LiveData<Boolean> get() = _loading
    val response: LiveData<MessageResponse?> get() = _response

    fun setSpace(space: String?) {
        mutableSpace.postValue(space)
    }

    fun addFeature(feature: String) {
        var current = mutableFeatures.value
        if (current == null) {
            current = mutableListOf()
            current.add(feature)
            mutableFeatures.postValue(current)
        }
    }

    fun deleteFeature(feature: String) {
        val current = mutableFeatures.value!!
        current.remove(feature)
        mutableFeatures.postValue(current)
    }

    fun updatePost(postId: String, newPost: PostWithOwnerId) {
        handleApiRequest(repository.updatePost(postId, newPost), _loading, _response, TAG)
    }

    fun clearPeriod() {
        mutablePeriod.postValue(null)
    }

    fun clearAllDetails() {
        mutableCondition.postValue(null)
        mutableRooms.postValue(null)
        mutableElevators.postValue(null)
        mutableFloors.postValue(null)
        mutableFloorNumber.postValue(null)
        mutableSpace.postValue(null)
        mutableFeatures.postValue(null)
    }

//    fun combineLiveDataValues(): String {
//        val features = featuresLd.value?.joinToString(", ") ?: ""
//        val type = typeLd.value ?: ""
//        val condition = conditionLd.value ?: ""
//        val rooms = roomsLd.value ?: ""
//        val bathrooms = bathroomsLd.value ?: ""
//        val floors = floorsLd.value ?: ""
//        val floorNumber = floorNumberLd.value ?: ""
//        val space = spaceLd.value ?: ""
//        val category = categoryLd.value ?: ""
//        val price = priceLd.value ?: ""
//        val period = periodLd.value ?: ""
//        val country = countryLd.value ?: ""
//        val city = cityLd.value ?: ""
//        val area = areaLd.value ?: ""
//        val description = descriptionLd.value ?: ""
//
//        return "Features: $features\nType: $type\nCondition: $condition\nRooms: $rooms\nBathrooms: $bathrooms\nFloors: $floors\nFloor Number: $floorNumber\nSpace: $space\nCategory: $category\nPrice: $price\nPeriod: $period\nCountry: $country\nCity: $city\nArea: $area\nDescription: $description"
//    }
//
//    fun trueOfFalse(): String {
//        return """
//    Condition for mutableFeatures: ${!mutableFeatures.value.isNullOrEmpty()}
//    Condition for mutableCondition: ${!mutableCondition.value.isNullOrBlank()}
//    Condition for mutableRooms: ${!mutableRooms.value.isNullOrBlank()}
//    Condition for mutableBathrooms: ${!mutableBathrooms.value.isNullOrBlank()}
//    Condition for mutableFloors: ${!mutableFloors.value.isNullOrBlank()}
//    Condition for mutableFloorNumber: ${!mutableFloorNumber.value.isNullOrBlank()}
//    Condition for mutableSpace: ${!mutableSpace.value.isNullOrBlank()}
//    Condition for mutableCategory: ${!mutableCategory.value.isNullOrBlank()}
//    Condition for mutablePrice: ${!mutablePrice.value.isNullOrBlank()}
//    Condition for mutablePeriod: ${(!mutablePeriod.value.isNullOrBlank() || mutableType.value != Type.RENT.value)}
//    Condition for mutableWhatsappNumber: ${!mutableWhatsappNumber.value.isNullOrBlank() || !mutableCallNumber.value.isNullOrBlank()}
//    Condition for mutableCountry: ${!mutableCountry.value.isNullOrBlank()}
//    Condition for mutableCity: ${!mutableCity.value.isNullOrBlank()}
//    Condition for mutableDescription: ${!mutableDescription.value.isNullOrBlank()}
//"""
//    }

}