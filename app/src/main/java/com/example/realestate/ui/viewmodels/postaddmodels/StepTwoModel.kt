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
    private val _isValidData = MediatorLiveData<Boolean>(false).apply {
        addSource(mutableLiveDataWrapper._categoryLiveData) { category ->
            this.value = validateTheData(
                category,
                mutableLiveDataWrapper._priceLiveData.value,
            )
        }
        addSource(mutableLiveDataWrapper._priceLiveData) { price ->
            this.value = validateTheData(
                mutableLiveDataWrapper._categoryLiveData.value,
                price,
            )
        }
    }

    val liveDataWrapper = LiveDataWrapper(mutableLiveDataWrapper)
    val isValidData: LiveData<Boolean>
        get() = _isValidData

    private fun validateTheData(
        category: String?,
        price: String?,
    ): Boolean {
        val isValidCategory = !category.isNullOrBlank()
        val isValidPrice = !price.isNullOrBlank()

        return isValidCategory && isValidPrice
    }

}

class MutableLiveDataWrapper {
    val _categoryLiveData = MutableLiveData<String>()
    val _priceLiveData = MutableLiveData<String>()
    val _typeLiveData = MutableLiveData<String>()

    override fun toString(): String {
        return "category=${_categoryLiveData.value.toString()}, " +
                "price=${_priceLiveData.value.toString()}, " +
                "type=${_typeLiveData.value.toString()}"
    }
}

class LiveDataWrapper(private val liveDataWrapper: MutableLiveDataWrapper) {
    val categoryLiveData: LiveData<String>
        get() = liveDataWrapper._categoryLiveData
    val priceLiveData: LiveData<String>
        get() = liveDataWrapper._priceLiveData
    val typeLiveData: LiveData<String>
        get() = liveDataWrapper._typeLiveData
}
