package com.example.realestate.ui.viewmodels.postaddmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ImagesSelectModel(private val imagesNumber: Int) : ViewModel() {

    companion object {
        private const val TAG = "ImagesSelectModel"
    }

    private val _imagesUri = MutableLiveData<MutableList<Uri?>>(MutableList(imagesNumber) { null })
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
        Log.d(TAG, "imagesUriList: $imagesUriList")
        imagesUriList?.apply {
            removeAt(position)
            add(null)
        }
        Log.d(TAG, "imagesUriList result: $imagesUriList")
        _imagesUri.value = imagesUriList!!
    }

}