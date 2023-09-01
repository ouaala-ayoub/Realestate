package com.example.realestate.ui.viewmodels.postaddmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.repositories.PostsRepository

class AddPostModel() : ViewModel() {

    companion object {
        const val TAG = "AddPostModel"
    }

    private val _isValidData = MutableLiveData(true)
    private val _isBackEnabled = MutableLiveData(true)

    val isBackEnabled: LiveData<Boolean> get() = _isBackEnabled
    val isValidData: LiveData<Boolean> get() = _isValidData

    fun updateIsValidData(newValue: Boolean) {
        _isValidData.postValue(newValue)
    }
    fun updateIsBackEnabled(newValue: Boolean) {
        _isBackEnabled.postValue(newValue)
    }

}