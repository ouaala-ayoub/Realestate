package com.example.realestate.ui.viewmodels.postaddmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
            addSource(_whatsappNumberLiveData) { validateForm() }
            addSource(_callNumberLiveData) { validateForm() }
        }
    }

    val liveDataWrapper = LiveDataWrapper(mutableLiveDataWrapper)
    val isValidData: LiveData<Boolean>
        get() = _isValidData

    private fun validateTheData(
        category: String?,
        price: String?,
        whatsappNumber: String?,
        callNumber: String?
    ): Boolean {
        //TODO handle whatsapp and call numbers
        val isValidCategory = !category.isNullOrEmpty()
        val isValidPrice = !price.isNullOrEmpty()
        val isValidPhone = !whatsappNumber.isNullOrEmpty() || !callNumber.isNullOrEmpty()

        return isValidCategory && isValidPrice && isValidPhone
    }

    private fun validateForm() {
        mutableLiveDataWrapper.apply {
            val isValid = validateTheData(
                _categoryLiveData.value,
                _priceLiveData.value,
                _whatsappNumberLiveData.value,
                _callNumberLiveData.value
            )
            _isValidData.value = isValid
        }
    }

}

class MutableLiveDataWrapper {
    val _typeLiveData = MutableLiveData<String>()
    val _categoryLiveData = MutableLiveData<String>()
    val _priceLiveData = MutableLiveData<String?>()
    val _whatsappNumberLiveData = MutableLiveData<String>()
    val _callNumberLiveData = MutableLiveData<String>()

    override fun toString(): String {
        return "category=${_categoryLiveData.value.toString()}, " +
                "price=${_priceLiveData.value.toString()}, " +
                "type=${_typeLiveData.value.toString()}, " +
                "whatsappNumber=${_whatsappNumberLiveData.value.toString()}" +
                "callNumber=${_callNumberLiveData.value.toString()}"
    }
}

class LiveDataWrapper(private val liveDataWrapper: MutableLiveDataWrapper) {
    val categoryLiveData: LiveData<String>
        get() = liveDataWrapper._categoryLiveData
    val priceLiveData: LiveData<String?>
        get() = liveDataWrapper._priceLiveData
    val typeLiveData: LiveData<String>
        get() = liveDataWrapper._typeLiveData
    val whatsappNumberLiveData: LiveData<String>
        get() = liveDataWrapper._whatsappNumberLiveData
    val callNumberLiveData: LiveData<String>
        get() = liveDataWrapper._callNumberLiveData
}
