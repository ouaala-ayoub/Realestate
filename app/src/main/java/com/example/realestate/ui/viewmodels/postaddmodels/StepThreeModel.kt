package com.example.realestate.ui.viewmodels.postaddmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.PostWithoutId
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.utils.handleApiRequest

class StepThreeModel(private val repository: PostsRepository) : ViewModel() {

    companion object {
        private const val TAG = "StepThreeModel"
    }

    //request related live data
    private val _requestResponse = MutableLiveData<MessageResponse?>()
    private val _loading = MutableLiveData<Boolean>()

    val requestResponse: LiveData<MessageResponse?>
        get() = _requestResponse
    val loading: LiveData<Boolean>
        get() = _loading

    //input related live data

    val _countryLiveData = MutableLiveData<String>()
    val _cityLiveData = MutableLiveData<String>()
    val _streetLiveData = MutableLiveData<String>()
    val _descriptionLiveData = MutableLiveData<String>()

    //location data live data
    private val _cities = MutableLiveData<List<String>>()
    private val _streets = MutableLiveData<List<String>>()

    val cities: LiveData<List<String>>
        get() = _cities
    val streets: LiveData<List<String>>
        get() = _streets


    val isDataValid = MediatorLiveData(false).apply {

        addSource(_countryLiveData) { country ->
            this.value = validateTheData(
                country,
                _cityLiveData.value
            )
        }
        addSource(_cityLiveData) { city ->
            this.value = validateTheData(
                _countryLiveData.value,
                city
            )
        }
    }

    val countryLiveData: LiveData<String>
        get() = _countryLiveData
    val cityLiveData: LiveData<String>
        get() = _cityLiveData
    val streetLiveData: LiveData<String>
        get() = _streetLiveData
    val descriptionLiveData: LiveData<String>
        get() = _descriptionLiveData

    fun addPost(post: PostWithoutId) {
        handleApiRequest(repository.addPost(post), _loading, _requestResponse, TAG)
    }

    fun getCities(country: String) {
//        handleApiRequest()
    }

    private fun validateTheData(
        country: String?,
        city: String?,
    ): Boolean {
        val isValidCountry = !country.isNullOrBlank()
        val isValidCity = !city.isNullOrBlank()

//        return isValidType && isValidCountry && isValidCity
        return isValidCountry && isValidCity
    }

}