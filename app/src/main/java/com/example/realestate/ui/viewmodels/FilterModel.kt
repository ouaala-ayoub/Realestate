package com.example.realestate.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.handleApiRequest
import retrofit2.Response

class FilterModel(private val staticDataRepository: StaticDataRepository) : ViewModel() {

    companion object {
        private const val TAG = "FilterModel"
    }

    private val _categoriesList = MutableLiveData<List<String>?>()
    private val _countries = MutableLiveData<CountriesData?>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _cities = MutableLiveData<Map<String, List<String>>?>()
    private val _citiesToShow = MutableLiveData<List<String>>(listOf())
    private val _areas = MutableLiveData<List<String>>()

    val categoriesList: LiveData<List<String>?>
        get() = getCategories()
    val countries: LiveData<CountriesData?>
        get() = getCountries()
    val citiesToShow: LiveData<List<String>>
        get() = _citiesToShow
    val streets: LiveData<List<String>>
        get() = _areas
    val isProgressBarTurning: LiveData<Boolean>
        get() = _isLoading

    private fun getCategories(): MutableLiveData<List<String>?> {
        handleApiRequest(staticDataRepository.getCategories(), _isLoading, _categoriesList, TAG)
        return _categoriesList
    }

    private fun getAllCities(additionalCode: AdditionalCode<Map<String, List<String>>>) {
        handleApiRequest(
            staticDataRepository.getAllCities(),
            _isLoading,
            _cities,
            TAG,
            additionalCode
        )
    }

    fun getCities(country: String) {

        Log.d(TAG, "getCities country: $country")

        if (_cities.value == null) {
            getAllCities(object : AdditionalCode<Map<String, List<String>>> {
                override fun onResponse(responseBody: Response<Map<String, List<String>>>) {
                    val res =
                        responseBody.body()?.entries?.find {
                            it.key.equals(
                                country,
                                ignoreCase = true
                            )
                        }?.value
                            ?: listOf()
                    _citiesToShow.postValue(res)
                }

                override fun onFailure() {
                    _citiesToShow.postValue(listOf())
                }

            })
        } else {
            _cities.value?.apply {
                val res =
                    this.entries.find { it.key.equals(country, ignoreCase = true) }?.value
                        ?: listOf()

                _citiesToShow.postValue(res)
            }
        }
    }

    private fun getCountries(): MutableLiveData<CountriesData?> {
        handleApiRequest(
            staticDataRepository.getCountries(), _isLoading, _countries,
            TAG
        )
        return _countries
    }
}