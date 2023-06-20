package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.Post

class SavedViewModel : ViewModel() {
    private val _savedList = MutableLiveData<List<Post>?>()
    val savedList: LiveData<List<Post>?>
        get() = _savedList

    fun getSavedList(userId: String) {
        //TODO
    }

}