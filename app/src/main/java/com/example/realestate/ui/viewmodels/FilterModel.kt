package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest

class FilterModel(private val staticDataRepository: StaticDataRepository) : ViewModel() {

    companion object {
        private const val TAG = "FilterModel"
    }

    private val _categoriesList = MutableLiveData<List<String>?>()
    val categoriesList: LiveData<List<String>?>
        get() = getCategories()

    private val _isLoading = MutableLiveData<Boolean>()
    val isProgressBarTurning: LiveData<Boolean>
        get() = _isLoading

    private fun getCategories(): MutableLiveData<List<String>?> {
        handleApiRequest(staticDataRepository.getCategories(), _isLoading, _categoriesList, TAG)
        return _categoriesList
    }
}