package com.example.realestate.ui.viewmodels.postaddmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.ContactType
import com.example.realestate.data.models.Type
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest

class StepTwoModel(private val staticDataRepository: StaticDataRepository) : ViewModel() {

    companion object {
        private const val TAG = "StepTwoModel"
    }

    //get from data source
    private val _categories = MutableLiveData<List<String>?>()
    private val _isLoading = MutableLiveData<Boolean>()
    val categories: LiveData<List<String>?>
        get() = _categories

    init {
        getCategories()
    }

    private fun getCategories() {
        handleApiRequest(
            staticDataRepository.getCategories(),
            _isLoading,
            _categories,
            TAG
        )
    }

    val mutableLiveDataWrapper = MutableLiveDataWrapper()
    private val _isValidData = MediatorLiveData(false).apply {
        mutableLiveDataWrapper.apply {
            addSource(_categoryLiveData) { validateForm() }
            addSource(_priceLiveData) { validateForm() }
            addSource(_periodLiveData) { validateForm() }
            addSource(_phoneNumberLiveData) { validateForm() }
            addSource(_contactTypeLiveData) { validateForm() }
        }
    }

    val liveDataWrapper = LiveDataWrapper(mutableLiveDataWrapper)
    val isValidData: LiveData<Boolean>
        get() = _isValidData

    private fun validateTheData(
        category: String?,
        price: String?,
        period: String?,
        contactType: String?,
        phoneNumber: String?,
        type: String?
    ): Boolean {
        val isValidCategory = !category.isNullOrEmpty()
        val isValidPrice = !price.isNullOrEmpty()
        val isValidPeriod = !period.isNullOrEmpty() || type != Type.RENT.value
        val isValidPhoneNumber = !phoneNumber.isNullOrEmpty()
        val isValidContactType = !contactType.isNullOrEmpty()

        return isValidCategory && isValidPrice && isValidPeriod && isValidPhoneNumber && isValidContactType
    }

    private fun validateForm() {
        mutableLiveDataWrapper.apply {
            val isValid = validateTheData(
                _categoryLiveData.value,
                _priceLiveData.value,
                _periodLiveData.value,
                _contactTypeLiveData.value,
                _phoneNumberLiveData.value,
                _typeLiveData.value
            )
            _isValidData.value = isValid
        }
    }

    fun updateSelectedOptions(isWhatsAppChecked: Boolean, isCallChecked: Boolean) {
        mutableLiveDataWrapper._contactTypeLiveData.value =
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

}


class MutableLiveDataWrapper {
    val _typeLiveData = MutableLiveData<String>()
    val _categoryLiveData = MutableLiveData<String>()
    val _priceLiveData = MutableLiveData<String?>()
    val _periodLiveData = MutableLiveData<String?>()
    val _contactTypeLiveData = MutableLiveData<String?>("")
    val _phoneNumberLiveData = MutableLiveData<String>("")

    fun clearPeriod() {
        _periodLiveData.postValue(null)
    }
}

class LiveDataWrapper(private val liveDataWrapper: MutableLiveDataWrapper) {
    val categoryLiveData: LiveData<String>
        get() = liveDataWrapper._categoryLiveData
    val priceLiveData: LiveData<String?>
        get() = liveDataWrapper._priceLiveData
    val periodLiveData: LiveData<String?>
        get() = liveDataWrapper._periodLiveData
    val typeLiveData: LiveData<String>
        get() = liveDataWrapper._typeLiveData
    val contactTypeLiveData: LiveData<String?> get() = liveDataWrapper._contactTypeLiveData
    val phoneLiveData: LiveData<String?> get() = liveDataWrapper._phoneNumberLiveData
}
