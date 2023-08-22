package com.example.realestate.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.utils.isNumeric

class DetailsStepViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailsStepViewModel"
    }

    val mapOfFunctions = mapOf(
        Pair("Furnished", ::setIsFurnished),
        Pair("Balcony", ::setHasBalcony),
        Pair("Elevator", ::setHasElevator),
        Pair("Security", ::setHasSecurity),
        Pair("New Property", ::setIsNew),
        Pair("Gym", ::setHasGym),
        Pair("Swimming Pool", ::setHasSwimmingPool),
        Pair("Parking", ::setHasParking),
    )
    private val _proprietyState = MutableLiveData<String>()
    private val _isFurnished = MutableLiveData(false)
    private val _hasBalcony = MutableLiveData(false)
    private val _hasElevator = MutableLiveData(false)
    private val _hasSecurity = MutableLiveData(false)
    private val _isNew = MutableLiveData(false)
    private val _hasGym = MutableLiveData(false)
    private val _hasSwimmingPool = MutableLiveData(false)
    private val _hasParking = MutableLiveData(false)
    private val _numberOfRooms = MutableLiveData<String>()
    private val _numberOfBathrooms = MutableLiveData<String>()
    private val _numberOfFloors = MutableLiveData<String>()
    private val _floorNumber = MutableLiveData<String>()
    private val _space = MutableLiveData<String?>()
    // MediatorLiveData to observe changes in the above LiveData objects

    private val _validationLiveData = MediatorLiveData(false)
    val validationLiveData: LiveData<Boolean> get() = _validationLiveData

    init {
        // Add sources to MediatorLiveData
        _validationLiveData.apply {
            addSource(_proprietyState) { validateForm() }
            addSource(_numberOfRooms) { validateForm() }
            addSource(_numberOfBathrooms) { validateForm() }
            addSource(_floorNumber) { validateForm() }
            addSource(_numberOfFloors) { validateForm() }
            addSource(_space) { validateForm() }
        }
    }

    // Validation function
    private fun validateForm() {
        val isValid = validateFields(
            _proprietyState.value,
            _numberOfRooms.value,
            _numberOfBathrooms.value,
            _floorNumber.value,
            _numberOfFloors.value,
            _space.value
        )
        _validationLiveData.value = isValid
    }

    // Validation logic (customize this based on your requirements)
    private fun validateFields(
        proprietyState: String?,
        numberOfRooms: String?,
        numberOfBathrooms: String?,
        floorNumber: String?,
        numberOfFloors: String?,
        space: String?
    ): Boolean {
        //TODO
        // Perform your validation logic here
        // For example, check if required fields are not empty, and any other specific validations

        Log.d(
            TAG,
            "proprietyState $proprietyState, numberOfRooms $numberOfRooms, numberOfBathrooms $numberOfBathrooms floorNumber $floorNumber numberOfFloors $numberOfFloors space $space"
        )

        val isValidPropertyState = !proprietyState.isNullOrEmpty()
        val isValidNumberOfRooms = numberOfRooms.toString().isNumeric()
        val isValidNumberOfBathrooms = numberOfBathrooms.toString().isNumeric()
        val isValidFloorNumber = floorNumber.toString().isNumeric()
        val isValidNumberOfFloors = numberOfFloors.toString().isNumeric()
        val isValidSpace = space.toString().isNumeric()

        Log.d(TAG, "isValidNumberOfFloors: $isValidNumberOfFloors")

        return (isValidPropertyState
                && isValidNumberOfRooms
                && isValidNumberOfBathrooms
                && isValidFloorNumber
                && isValidNumberOfFloors
                && isValidSpace)
    }

    // Methods to update LiveData values
    fun setProprietyState(state: String) {
        _proprietyState.value = state
    }

    private fun setIsFurnished(furnished: Boolean) {
        _isFurnished.value = furnished
    }

    private fun setHasBalcony(hasBalcony: Boolean) {
        _hasBalcony.value = hasBalcony
    }

    private fun setHasElevator(hasElevator: Boolean) {
        _hasElevator.value = hasElevator
    }

    private fun setHasSecurity(hasSecurity: Boolean) {
        _hasSecurity.value = hasSecurity
    }

    private fun setIsNew(isNew: Boolean) {
        _isNew.value = isNew
    }

    private fun setHasGym(hasGym: Boolean) {
        _hasGym.value = hasGym
    }

    private fun setHasSwimmingPool(hasSwimmingPool: Boolean) {
        _hasSwimmingPool.value = hasSwimmingPool
    }

    private fun setHasParking(hasParking: Boolean) {
        _hasParking.value = hasParking
    }

    fun setNumberOfRooms(numberOfRooms: String) {
        _numberOfRooms.value = numberOfRooms
    }

    fun setNumberOfBathrooms(numberOfBathrooms: String) {
        _numberOfBathrooms.value = numberOfBathrooms
    }

    fun setFloorNumber(floorNumber: String) {
        _floorNumber.value = floorNumber
    }

    fun setNumberOfFloors(numberOfFloors: String) {
        _numberOfFloors.value = numberOfFloors
    }

    fun setSpace(space: String?) {
        _space.value = space
    }

    fun getResult(): Map<String, Any> {
        //TODO
        val result = mutableMapOf<String, Any>()
        result["isFurnished"] = _isFurnished.value!!
        result["hasBalcony"] = _hasBalcony.value!!
        result["isNewProperty"] = _isNew.value!!
        result["hasSwimmingPool"] = _hasSwimmingPool.value!!
        result["hasGym"] = _hasGym.value!!
        result["hasParking"] = _hasParking.value!!
        result["hasElevator"] = _hasElevator.value!!
        result["hasSecurity"] = _hasSecurity.value!!

        if (_proprietyState.value != null)
            result["propertyCondition"] = _proprietyState.value.toString()
        if (_numberOfRooms.value != null)
            result["numberOfRooms"] = _numberOfRooms.value.toString()
        if (_numberOfBathrooms.value != null)
            result["numberOfBathrooms"] = _numberOfBathrooms.value.toString()
        if (_numberOfFloors.value != null)
            result["numberOfFloors"] = _numberOfFloors.value.toString()
        if (_floorNumber.value != null)
            result["floorNumber"] = _floorNumber.value.toString()
        if (_space.value != null)
            result["space"] = _space.value.toString()

        return result.filterValues { value -> value != false }
    }
}