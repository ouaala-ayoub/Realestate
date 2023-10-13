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


    // MediatorLiveData to observe changes in the above LiveData objects
    private val _validationLiveData = MediatorLiveData(false)
    val validationLiveData: LiveData<Boolean> get() = _validationLiveData

    //details MutableLiveData
    private val _features = MutableLiveData<MutableList<String>>(mutableListOf())
    private val _proprietyState = MutableLiveData<String>()
    private val _numberOfRooms = MutableLiveData<String>()
    private val _numberOfElevators = MutableLiveData<String>()
    private val _numberOfFloors = MutableLiveData<String>()
    private val _floorNumber = MutableLiveData<String>()
    private val _space = MutableLiveData<String?>()

    //details LiveData
    val featuresLiveData: LiveData<MutableList<String>> get() = _features
    val propertyState: LiveData<String> = _proprietyState
    val numberOfRooms: LiveData<String> = _numberOfRooms
    val numberOfElevators: LiveData<String> = _numberOfElevators
    val numberOfFloors: LiveData<String> = _numberOfFloors
    val floorNumberLiveData: LiveData<String> = _floorNumber
    val spaceLiveData: LiveData<String?> = _space

    init {
        // Add sources to MediatorLiveData
        _validationLiveData.apply {
            addSource(_proprietyState) { validateForm() }
            addSource(_numberOfRooms) { validateForm() }
            addSource(_numberOfElevators) { validateForm() }
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
            _numberOfElevators.value,
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

    fun addFeature(feature: String) {
        val current = _features.value!!
        current.add(feature)
        _features.postValue(current)
    }

    fun deleteFeature(feature: String) {
        val current = _features.value!!
        current.remove(feature)
        _features.postValue(current)
    }

    // Methods to update LiveData values
    fun setProprietyState(state: String) {
        _proprietyState.value = state
    }

    fun setNumberOfRooms(numberOfRooms: String) {
        _numberOfRooms.value = numberOfRooms
    }

    fun setNumberOfElevators(numberOfElevators: String) {
        _numberOfElevators.value = numberOfElevators
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
}