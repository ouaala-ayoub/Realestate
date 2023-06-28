package com.example.realestate.ui.viewmodels.postaddmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.data.models.Media
import com.example.realestate.ui.adapters.ImagesAdapter
import com.example.realestate.utils.swap
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class ImagesSelectModel : ViewModel() {

    companion object {
        private const val TAG = "ImagesSelectModel"
    }

    private fun uploadCallback(images: Media) = object : UploadCallback {
        override fun onStart(requestId: String) {
            // Called when the upload starts
        }

        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
            // Called during the upload progress
            val progress = (bytes * 100 / totalBytes).toInt()
            Log.i(TAG, "progress: $progress")
        }

        override fun onSuccess(requestId: String, resultData: MutableMap<Any?, Any?>?) {
            // Called when the upload is successful
            val publicId = resultData?.get("public_id")
            val secureUrl = resultData?.get("secure_url")
            // Handle the uploaded image URL and public ID

            Log.i(TAG, "secureUrl: $secureUrl")
            Log.i(TAG, "publicId: $publicId")

            urlsList.add(secureUrl.toString())
            Log.d(TAG, "urlsList: $urlsList")
            updateIsValid(urlsList, images)
        }

        override fun onError(requestId: String, error: ErrorInfo) {
            // Called if an error occurs during the upload
            Log.e(TAG, "onError: ${error.description}")
        }

        override fun onReschedule(requestId: String, error: ErrorInfo) {
            // Called if the upload needs to be rescheduled
        }
    }

    private val _isFull = MutableLiveData(false)
    private val _isValid = MutableLiveData(false)
    private val urlsList = mutableListOf<String>()

    val isFull: LiveData<Boolean>
        get() = _isFull
    val isValid: LiveData<Boolean>
        get() = _isValid

    private fun updateIsValid(uploadedImages: List<String>, images: Media) {
        _isValid.postValue(uploadedImages.size == images.countNonNullElements())
    }

    private fun updateIsFull(images: Media) {
        _isFull.postValue(!images.uris.contains(null))
    }

    fun checkImageAt(
        position: Int,
        images: Media,
        rv: RecyclerView.Adapter<ImagesAdapter.ImagesHolder>
    ) {
        val oldSelected = images.selectedPosition
        images.selectedPosition = position
        rv.apply {
            notifyItemChanged(position)
            notifyItemChanged(oldSelected)
        }
    }

    fun deleteImageAt(
        position: Int,
        images: Media,
        rv: RecyclerView.Adapter<ImagesAdapter.ImagesHolder>
    ) {
        images.uris.apply {
            removeAt(position)
            add(null)

            rv.notifyDataSetChanged()
        }


        updateIsFull(images)
        updateIsValid(urlsList, images)
    }


    fun addImages(
        listToAdd: List<Uri>,
        images: Media,
        rv: RecyclerView.Adapter<ImagesAdapter.ImagesHolder>
    ) {
        val startIndex = images.uris.indexOf(null)
        for (i in listToAdd.indices) {
            if (startIndex + i <= images.uris.lastIndex && startIndex + i >= 0) {
                images.uris[startIndex + i] = listToAdd[i]
                rv.notifyItemChanged(startIndex + i)
            } else {
                break
            }
        }
        updateIsFull(images)
        updateIsValid(urlsList, images)
    }

    fun getResult(selectedPosition: Int): List<String> {
        urlsList.swap(selectedPosition, 0)
        return urlsList
    }

    fun uploadImage(uri: Uri, media: Media) {
        MediaManager.get()
            .upload(uri)
            .option("resource_type", "image")
            .callback(uploadCallback(media))
            .dispatch()
    }

    fun uploadVideo(uri: Uri, media: Media) {
        MediaManager.get()
            .upload(uri)
            .option("resource_type", "video")
            .callback(uploadCallback(media))
            .dispatch()
    }

}