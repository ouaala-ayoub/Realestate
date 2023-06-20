package com.example.realestate.ui.viewmodels.postaddmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepTwoModel : ViewModel() {

    //get from data source
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>>
        get() = _categories

    fun getCategories() {
//        handleApiRequest()
    }

    val mutableLiveDataWrapper = MutableLiveDataWrapper()
    private val _isValidData = MediatorLiveData<Boolean>(false).apply {
        addSource(mutableLiveDataWrapper._categoryLiveData) { category ->
            this.value = validateTheData(
                category,
                mutableLiveDataWrapper._priceLiveData.value,
                mutableLiveDataWrapper._currencyLiveData.value,
            )
        }
        addSource(mutableLiveDataWrapper._priceLiveData) { price ->
            this.value = validateTheData(
                mutableLiveDataWrapper._categoryLiveData.value,
                price,
                mutableLiveDataWrapper._currencyLiveData.value,
            )
        }
        addSource(mutableLiveDataWrapper._currencyLiveData) { currency ->
            this.value = validateTheData(
                mutableLiveDataWrapper._categoryLiveData.value,
                mutableLiveDataWrapper._priceLiveData.value,
                currency,
            )
        }
    }

    val liveDataWrapper = LiveDataWrapper(mutableLiveDataWrapper)
    val isValidData: LiveData<Boolean>
        get() = _isValidData

    private fun validateTheData(
        category: String?,
        price: String?,
        currency: String?
    ): Boolean {
        val isValidCategory = !category.isNullOrBlank()
        val isValidPrice = !price.isNullOrBlank()
        val isValidCurrency = !currency.isNullOrBlank()

        return isValidCategory && isValidPrice && isValidCurrency
    }

}

class MutableLiveDataWrapper {
    val _categoryLiveData = MutableLiveData<String>()
    val _priceLiveData = MutableLiveData<String>()
    val _currencyLiveData = MutableLiveData<String>()

    override fun toString(): String {
        return "category=${_categoryLiveData.value.toString()}, " +
                "price=${_priceLiveData.value.toString()}, " +
                "currency=${_currencyLiveData.value.toString()}"
    }
}

class LiveDataWrapper(private val liveDataWrapper: MutableLiveDataWrapper) {
    val categoryLiveData: LiveData<String>
        get() = liveDataWrapper._categoryLiveData
    val priceLiveData: LiveData<String>
        get() = liveDataWrapper._priceLiveData
    val currencyLiveData: LiveData<String>
        get() = liveDataWrapper._currencyLiveData
}
