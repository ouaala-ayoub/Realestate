package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest

open class CountriesModel(private val staticDataRepository: StaticDataRepository) : ViewModel() {

    companion object {
        private const val TAG = "CountriesModel"
    }

    private val _cities = MutableLiveData<Map<String, List<String>>?>()
    private val _citiesToShow = MutableLiveData<List<String>>(listOf())
    private val _countries = MutableLiveData<CountriesData?>()

    val cities: LiveData<List<String>>
        get() = _citiesToShow
    val countries: LiveData<CountriesData?>
        get() = getCountries()

    fun getCountries(): MutableLiveData<CountriesData?> {
        handleApiRequest(staticDataRepository.getCountries(), null, _countries, TAG)
        return _countries
    }

    fun getAllCities() {
        handleApiRequest(staticDataRepository.getAllCities(), null, _cities, TAG)
    }
    fun getCities(country: String) {

        _cities.value?.apply {
            val res =
                this.entries.find { it.key.equals(country, ignoreCase = true) }?.value ?: listOf()
            _citiesToShow.postValue(res)
        }
    }

}