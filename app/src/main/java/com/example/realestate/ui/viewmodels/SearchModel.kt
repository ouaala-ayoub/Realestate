package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest

class SearchModel(private val staticDataRepository: StaticDataRepository) : ViewModel() {

    companion object {
        private const val TAG = "SearchModel"
    }

    private val _categoriesList = MutableLiveData<List<String>?>()
    val categoriesList: LiveData<List<String>?>
        get() = _categoriesList

    private val _isLoading = MutableLiveData<Boolean>()
    val isProgressBarTurning: LiveData<Boolean>
        get() = _isLoading

    fun getCategories() {
        handleApiRequest(staticDataRepository.getCategories(), _isLoading, _categoriesList, TAG)
    }
}