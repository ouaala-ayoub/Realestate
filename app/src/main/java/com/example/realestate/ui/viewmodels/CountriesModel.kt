package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest

class CountriesModel(private val staticDataRepository: StaticDataRepository) : ViewModel() {

    companion object {
        private const val TAG = "CountriesModel"
    }

    private val _countries = MutableLiveData<CountriesData?>()
    val countries: LiveData<CountriesData?>
        get() = getCountries()

    private fun getCountries(): MutableLiveData<CountriesData?> {
        handleApiRequest(staticDataRepository.getCountries(), null, _countries, TAG)
        return _countries
    }

}