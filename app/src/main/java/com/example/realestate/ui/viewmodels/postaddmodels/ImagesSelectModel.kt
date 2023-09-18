package com.example.realestate.ui.viewmodels.postaddmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ImagesSelectModel() : ViewModel() {

    companion object {
        private const val TAG = "ImagesSelectModel"
    }

    private val _imagesUri = MutableLiveData<MutableList<Uri?>>(mutableListOf())
    private val _isValid = MediatorLiveData(false).apply {
        addSource(_imagesUri) { uris -> this.value = uris.filterNotNull().isNotEmpty() }
    }

    val imagesUri: LiveData<MutableList<Uri?>> get() = _imagesUri
    val isValid: LiveData<Boolean>
        get() = _isValid

    fun setImagesUri(imagesMedia: MutableList<Uri?>) {
        _imagesUri.value = imagesMedia
    }


    fun deleteElementAt(position: Int) {
        val imagesUriList = _imagesUri.value?.toMutableList()
        imagesUriList?.apply {
            removeAt(position)
        }
        _imagesUri.value = imagesUriList!!
    }

}