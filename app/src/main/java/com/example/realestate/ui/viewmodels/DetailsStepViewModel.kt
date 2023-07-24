package com.example.realestate.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.Details

class DetailsStepViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailsStepViewModel"
    }

    private val _buildingAge = MutableLiveData<String>()
    private val _isFurnished = MutableLiveData(false)
    private val _hasBalcony = MutableLiveData(false)
    private val _isNew = MutableLiveData(false)
    private val _hasGym = MutableLiveData(false)
    private val _hasSwimmingPool = MutableLiveData(false)
    private val _hasParking = MutableLiveData(false)
    private val _numberOfBedrooms = MutableLiveData<String>()
    private val _floorNumber = MutableLiveData<String>()
    private val _space = MutableLiveData<Number>()
    // MediatorLiveData to observe changes in the above LiveData objects

    private val _validationLiveData = MediatorLiveData<Boolean>()
    val validationLiveData: LiveData<Boolean> get() = _validationLiveData

    init {
        // Add sources to MediatorLiveData
        _validationLiveData.apply {
            addSource(_buildingAge) { validateForm() }
            addSource(_isFurnished) { validateForm() }
            addSource(_hasBalcony) { validateForm() }
            addSource(_isNew) { validateForm() }
            addSource(_hasGym) { validateForm() }
            addSource(_hasSwimmingPool) { validateForm() }
            addSource(_hasParking) { validateForm() }
            addSource(_numberOfBedrooms) { validateForm() }
            addSource(_floorNumber) { validateForm() }
            addSource(_space) { validateForm() }
        }
    }

    // Validation function
    private fun validateForm() {
        val isValid = validateFields(
            _buildingAge.value,
            _isFurnished.value,
            _hasBalcony.value,
            _isNew.value,
            _hasGym.value,
            _hasSwimmingPool.value,
            _hasParking.value,
            _numberOfBedrooms.value,
            _floorNumber.value,
            _space.value
        )
        _validationLiveData.value = isValid
    }

    // Validation logic (customize this based on your requirements)
    private fun validateFields(
        buildingAge: String?,
        isFurnished: Boolean?,
        hasBalcony: Boolean?,
        isNew: Boolean?,
        hasGym: Boolean?,
        hasSwimmingPool: Boolean?,
        hasParking: Boolean?,
        numberOfBedrooms: String?,
        floorNumber: String?,
        space: Number?
    ): Boolean {
        // Perform your validation logic here
        // For example, check if required fields are not empty, and any other specific validations
        return true
    }

    // Methods to update LiveData values
    fun setBuildingAge(age: String) {
        _buildingAge.value = age
    }

    fun setIsFurnished(furnished: Boolean) {
        _isFurnished.value = furnished
    }

    fun setHasBalcony(hasBalcony: Boolean) {
        _hasBalcony.value = hasBalcony
    }

    fun setIsNew(isNew: Boolean) {
        _isNew.value = isNew
    }

    fun setHasGym(hasGym: Boolean) {
        _hasGym.value = hasGym
    }

    fun setHasSwimmingPool(hasSwimmingPool: Boolean) {
        _hasSwimmingPool.value = hasSwimmingPool
    }

    fun setHasParking(hasParking: Boolean) {
        _hasParking.value = hasParking
    }

    fun setNumberOfBedrooms(numberOfBedrooms: String) {
        _numberOfBedrooms.value = numberOfBedrooms
    }

    fun setFloorNumber(floorNumber: String) {
        _floorNumber.value = floorNumber
    }

    fun setSpace(space: Number) {
        _space.value = space
    }

    fun getFinalDetails(): Details {
        return Details(
            _buildingAge.value.toString(),
            _isFurnished.value!!,
            _hasBalcony.value!!,
            _isNew.value!!,
            _hasGym.value!!,
            _hasSwimmingPool.value!!,
            _hasParking.value!!,
            _numberOfBedrooms.value.toString(),
            _floorNumber.value.toString(),
            _space.value!!
        )
    }

}