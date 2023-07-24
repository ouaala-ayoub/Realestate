package com.example.realestate.ui.viewmodels.postaddmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.realestate.data.models.Media
import com.example.realestate.data.models.UriHolder
import com.example.realestate.ui.adapters.ImagesAdapter
import com.example.realestate.utils.RandomGenerator
import com.example.realestate.utils.swap
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.OnPausedListener
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.File


class ImagesSelectModel(private val imagesNumber: Int) : ViewModel() {

    companion object {
        private const val TAG = "ImagesSelectModel"
    }

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    private fun uploadTest(fileUri: Uri, media: Media, position: Int) {
        // File or Blob
//        val file = Uri.fromFile(File("path/to/mountains.jpg"))

        // Create the file metadata
//        val metadata = storageMetadata {
//            contentType = "image/jpeg"
//        }
        val fileName = RandomGenerator.createUniqueImageName()

        // Upload file and metadata to the path 'images/mountains.jpg'
        val uploadTask = storageRef.child("images/${fileName}").putFile(fileUri)

        // Listen for state changes, errors, and completion of the upload.
        // You'll need to import com.google.firebase.storage.ktx.component1 and
        // com.google.firebase.storage.ktx.component2

        uploadTask.addCallBacks(media, position)

    }

    private fun UploadTask.addCallBacks(images: Media, position: Int) {
        addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            Log.d(TAG, "Upload is $progress% done")

            if (progress != 100.0)
                _progressList[position].postValue(progress.toInt())

        }.addOnPausedListener {

        }.addOnFailureListener { e ->
            // Handle unsuccessful uploads
            e.printStackTrace()
            _uploading.postValue(false)
        }.addOnSuccessListener { taskSnapShot ->
            // Handle successful uploads on complete
            // ...
            //TODO
            taskSnapShot.storage.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                Log.d(TAG, "downloadUrl: $downloadUrl")

                urlsList.add(downloadUrl)
                updateIsValid(urlsList, images)
                _uploading.postValue(false)
                _progressList[position].postValue(100)
                _uploading.postValue(false)
            }

        }
    }

    private fun uploadCallback(images: Media, position: Int) = object : UploadCallback {
        override fun onStart(requestId: String) {
            // Called when the upload starts
            _progressList[position].postValue(0)
        }

        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
            // Called during the upload progress
            val progress = (bytes * 100 / totalBytes).toInt()

            if (progress != 100)
                _progressList[position].postValue(progress)
//            Log.i(TAG, "progress: $progress")
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
            _uploading.postValue(false)
            _progressList[position].postValue(100)
        }

        override fun onError(requestId: String, error: ErrorInfo) {
            // Called if an error occurs during the upload
            Log.e(TAG, "onError: ${error.description}")
            _uploading.postValue(false)
        }

        override fun onReschedule(requestId: String, error: ErrorInfo) {
            // Called if the upload needs to be rescheduled
            _uploading.postValue(false)
        }
    }

    private val _isFull = MutableLiveData(false)
    private val _isValid = MutableLiveData(false)
    private val _uploading = MutableLiveData<Boolean>()

    //    private val _progressList = MutableLiveData<List<Int?>>()
    private val _progressList = List<MutableLiveData<Int?>>(imagesNumber) { MutableLiveData(null) }
    private val urlsList = mutableListOf<String>()

    val isFull: LiveData<Boolean>
        get() = _isFull
    val isValid: LiveData<Boolean>
        get() = _isValid
    val uploading: LiveData<Boolean>
        get() = _uploading
    val progress: List<LiveData<Int?>>
        get() = _progressList

    private fun updateIsValid(uploadedImages: List<String>, images: Media) {
        _isValid.postValue(uploadedImages.size == images.countNonNullElements())
    }

    private fun updateIsFull(images: Media) {
        _isFull.postValue(!images.uriHolders.map { uriHolder -> uriHolder.uri }.contains(null))
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

        images.uriHolders.apply {
            removeAt(position)
            add(UriHolder())

            rv.notifyDataSetChanged()
        }


        updateIsFull(images)
        updateIsValid(urlsList, images)
    }


    fun addImages(
        listToAdd: List<Uri>,
        images: Media,
        rv: RecyclerView.Adapter<ImagesAdapter.ImagesHolder>,
        mimeTypes: List<String?>
    ) {
        val startIndex = images.uriHolders.indexOf(UriHolder())
        for (i in listToAdd.indices) {
            if (startIndex + i <= images.uriHolders.lastIndex && startIndex + i >= 0) {
                images.uriHolders[startIndex + i].uri = listToAdd[i]
                newUpload(listToAdd[i], mimeTypes[i], startIndex + i, images)
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

    private fun uploadImage(uri: Uri, media: Media, position: Int) {
        MediaManager.get()
            .upload(uri)
            .option("resource_type", "image")
            .callback(uploadCallback(media, position))
            .dispatch()
    }

    private fun uploadVideo(uri: Uri, media: Media, position: Int) {
        MediaManager.get()
            .upload(uri)
            .option("resource_type", "video")
            .callback(uploadCallback(media, position))
            .dispatch()
    }

    private fun upload(uri: Uri, mimeType: String?, position: Int, imagesList: Media) {
        when {
            mimeType == null -> return
            mimeType.contains("image") -> {
                uploadImage(uri, imagesList, position)
            }
            mimeType.contains("video") -> {
                uploadVideo(uri, imagesList, position)
            }
        }
    }

    fun newUpload(uri: Uri, mimeType: String?, position: Int, imagesList: Media){
        uploadTest(uri, imagesList, position)
    }

    fun cancelAllUploads(): Int {
        return MediaManager.get().cancelAllRequests()
    }

}