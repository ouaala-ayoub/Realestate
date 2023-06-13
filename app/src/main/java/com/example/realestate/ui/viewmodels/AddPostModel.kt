package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.repositories.PostsRepository

class AddPostModel(private val repository: PostsRepository) : ViewModel() {

    companion object {
        const val TAG = "AddPostModel"
    }

    private val _isValidData = MutableLiveData(true)

    val isValidData: LiveData<Boolean>
        get() = _isValidData

    fun updateIsValidData(newValue: Boolean) {
        _isValidData.postValue(newValue)
    }


}