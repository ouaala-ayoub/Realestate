package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.Post
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.utils.handleApiRequest

class StepThreeModel(private val repository: PostsRepository) : ViewModel() {

    //request related live data
    private val _requestResponse = MutableLiveData<MessageResponse?>()
    private val _loading = MutableLiveData<Boolean>()

    val requestResponse: LiveData<MessageResponse?>
        get() = _requestResponse
    val loading: LiveData<Boolean>
        get() = _loading

    //input related live data

    val _typeLiveData = MutableLiveData<String>()
    val _countryLiveData = MutableLiveData<String>()
    val _cityLiveData = MutableLiveData<String>()
    val _streetLiveData = MutableLiveData<String>()
    val _descriptionLiveData = MutableLiveData<String>()

    //location data live data
    private val _countries = MutableLiveData<List<String>>()
    private val _cities = MutableLiveData<List<String>>()
    private val _streets = MutableLiveData<List<String>>()

    val countries: LiveData<List<String>>
        get() = _countries
    val cities: LiveData<List<String>>
        get() = _cities
    val streets: LiveData<List<String>>
        get() = _streets


    val isDataValid = MediatorLiveData<Boolean>(false).apply {
        addSource(_typeLiveData) { type ->
            validateTheData(
                type,
                _countryLiveData.value,
                _cityLiveData.value
            )
        }
        addSource(_countryLiveData) { country ->
            validateTheData(
                _typeLiveData.value,
                country,
                _cityLiveData.value
            )
        }
        addSource(_cityLiveData) { city ->
            validateTheData(
                _typeLiveData.value,
                _countryLiveData.value,
                city
            )
        }
    }

    val typeLiveData: LiveData<String>
        get() = _typeLiveData
    val countryLiveData: LiveData<String>
        get() = _countryLiveData
    val cityLiveData: LiveData<String>
        get() = _cityLiveData
    val streetLiveData: LiveData<String>
        get() = _streetLiveData
    val descriptionLiveData: LiveData<String>
        get() = _descriptionLiveData

    fun addPost(post: Post) {
        handleApiRequest(repository.addPost(post), _loading, _requestResponse, AddPostModel.TAG)
    }

    fun getCountries(){
//        handleApiRequest()
    }
    fun getCities(){
//        handleApiRequest()
    }

    private fun validateTheData(
        type: String?,
        country: String?,
        city: String?,
    ): Boolean {
        val isValidType = !type.isNullOrBlank()
        val isValidCountry = !country.isNullOrBlank()
        val isValidCity = !city.isNullOrBlank()

        return isValidType && isValidCountry && isValidCity
    }

}