package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.utils.handleApiRequest

class StepTwoModel : ViewModel() {

    //get from data source
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>>
        get() = _categories

    fun getCategories(){
//        handleApiRequest()
    }

    val mutableLiveDataWrapper = MutableLiveDataWrapper()
    private val _isValidData = MediatorLiveData<Boolean>(false).apply {
        addSource(mutableLiveDataWrapper._titleLiveData) { name ->
            this.value = validateTheData(
                name,
                mutableLiveDataWrapper._categoryLiveData.value,
                mutableLiveDataWrapper._priceLiveData.value,
                mutableLiveDataWrapper._currencyLiveData.value,
            )
        }
        addSource(mutableLiveDataWrapper._categoryLiveData) { category ->
            this.value = validateTheData(
                mutableLiveDataWrapper._titleLiveData.value,
                category,
                mutableLiveDataWrapper._priceLiveData.value,
                mutableLiveDataWrapper._currencyLiveData.value,
            )
        }
        addSource(mutableLiveDataWrapper._priceLiveData) { price ->
            this.value = validateTheData(
                mutableLiveDataWrapper._titleLiveData.value,
                mutableLiveDataWrapper._categoryLiveData.value,
                price,
                mutableLiveDataWrapper._currencyLiveData.value,
            )
        }
        addSource(mutableLiveDataWrapper._currencyLiveData) { currency ->
            this.value = validateTheData(
                mutableLiveDataWrapper._titleLiveData.value,
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
        title: String?,
        category: String?,
        price: String?,
        currency: String?
    ): Boolean {
        val isValidTitle = !title.isNullOrBlank()
        val isValidCategory = !category.isNullOrBlank()
        val isValidPrice = !price.isNullOrBlank()
        val isValidCurrency = !currency.isNullOrBlank()

        return isValidTitle && isValidCategory && isValidPrice && isValidCurrency
    }

}

class MutableLiveDataWrapper {
    val _titleLiveData = MutableLiveData<String>()
    val _categoryLiveData = MutableLiveData<String>()
    val _priceLiveData = MutableLiveData<String>()
    val _currencyLiveData = MutableLiveData<String>()

    override fun toString(): String {
        return "title=${_titleLiveData.value.toString()}, " +
                "category=${_categoryLiveData.value.toString()}, " +
                "price=${_priceLiveData.value.toString()}, " +
                "currency=${_currencyLiveData.value.toString()}"
    }
}

class LiveDataWrapper(private val liveDataWrapper: MutableLiveDataWrapper) {
    val titleLiveData: LiveData<String>
        get() = liveDataWrapper._titleLiveData
    val categoryLiveData: LiveData<String>
        get() = liveDataWrapper._categoryLiveData
    val priceLiveData: LiveData<String>
        get() = liveDataWrapper._priceLiveData
    val currencyLiveData: LiveData<String>
        get() = liveDataWrapper._currencyLiveData
}
