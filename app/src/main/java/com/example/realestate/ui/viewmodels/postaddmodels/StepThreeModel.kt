package com.example.realestate.ui.viewmodels.postaddmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.PostWithoutId
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import retrofit2.Response

class StepThreeModel(
    private val repository: PostsRepository,
    private val staticDataRepository: StaticDataRepository
) : ViewModel() {

    companion object {
        private const val TAG = "StepThreeModel"
    }

    fun getAllCities() {
        handleApiRequest(staticDataRepository.getAllCities(), _loading, _cities, TAG)
    }

    fun getCountries() {
        handleApiRequest(staticDataRepository.getCountries(), _loading, _countries, TAG)
    }

    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val urlsList = mutableListOf<String>()

    //request related live data
    private val _requestResponse = MutableLiveData<MessageResponse?>()
    private val _loading = MutableLiveData<Boolean>()

    val requestResponse: LiveData<MessageResponse?>
        get() = _requestResponse
    val loading: LiveData<Boolean>
        get() = _loading

    //input related live data

    val _countryLiveData = MutableLiveData<String>()
    val _cityLiveData = MutableLiveData<String>()
    val _streetLiveData = MutableLiveData<String>()
    val _descriptionLiveData = MutableLiveData<String>()

    //location data live data
    private val _countries = MutableLiveData<CountriesData?>()
    private val _cities = MutableLiveData<Map<String, List<String>>?>()
    private val _citiesToShow = MutableLiveData<List<String>>(listOf())
    private val _streets = MutableLiveData<List<String>>()

    val countries: LiveData<CountriesData?>
        get() = _countries
    val cities: LiveData<List<String>>
        get() = _citiesToShow
    val streets: LiveData<List<String>>
        get() = _streets


    val isDataValid = MediatorLiveData(false).apply {
        addSource(_countryLiveData) { validateForm() }
        addSource(_cityLiveData) { validateForm() }
        addSource(_descriptionLiveData) { validateForm() }
    }

    val countryLiveData: LiveData<String>
        get() = _countryLiveData
    val cityLiveData: LiveData<String>
        get() = _cityLiveData
    val streetLiveData: LiveData<String>
        get() = _streetLiveData
    val descriptionLiveData: LiveData<String>
        get() = _descriptionLiveData

    fun addPost(post: PostWithoutId, imagesList: List<Uri>, context: Context) {

        _loading.postValue(true)

        imagesList.forEach { uri ->
            val ext = getFileExtensionFromUri(context, uri)
            val fileName = RandomGenerator.createUniqueImageName(ext!!)
            val path = "posts/${fileName}"

            try {
                // Upload file and metadata to the path 'images/mountains.jpg'
                val uploadTask = storageRef.child(path).putFile(uri)

                // Listen for state changes, errors, and completion of the upload.
                // You'll need to import com.google.firebase.storage.ktx.component1 and
                // com.google.firebase.storage.ktx.component2

                uploadTask.addCallBacks(post, imagesList.size)
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

        }


    }

    private fun UploadTask.addCallBacks(post: PostWithoutId, fullSize: Int) {
        addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            Log.d(TAG, "Upload is $progress% done")

        }.addOnPausedListener {

        }.addOnFailureListener { e ->
            // Handle unsuccessful uploads
            e.printStackTrace()
            _loading.postValue(false)
        }.addOnSuccessListener { taskSnapShot ->
            // Handle successful uploads on complete
            // ...
            //TODO
            taskSnapShot.storage.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                Log.d(TAG, "downloadUrl: $downloadUrl")

                urlsList.add(downloadUrl)

                if (urlsList.size == fullSize) {
                    post.media = urlsList
                    Log.d(TAG, "post: $post")
                    handleApiRequest(
                        repository.addPost(post),
                        _loading,
                        null,
                        TAG,
                        object : AdditionalCode<MessageResponse> {
                            override fun onResponse(responseBody: Response<MessageResponse>) {
                                if (responseBody.isSuccessful) {
                                    _requestResponse.postValue(responseBody.body())
                                    Log.d(TAG, "onResponse: ${responseBody.body()}")
                                } else {
                                    val error =
                                        getError(TAG, responseBody.errorBody(), responseBody.code())
                                    _requestResponse.postValue(MessageResponse(error?.message))
                                }
                            }

                            override fun onFailure() {
                                _requestResponse.postValue(MessageResponse(message = "Unexpected Error"))
                            }

                        })
                }

            }

        }
    }

    fun getCities(country: String) {

        _cities.value?.apply {
            val res =
                this.entries.find { it.key.equals(country, ignoreCase = true) }?.value ?: listOf()
            _citiesToShow.postValue(res)
        }
    }


    private fun validateTheData(
        country: String?,
        city: String?,
        description: String?
    ): Boolean {
        val isValidCountry = Countries.get()?.find { data -> data.name == country } != null
        val isValidCity = !city.isNullOrEmpty()
        val isValidDescription = !description.isNullOrEmpty()

//        return isValidType && isValidCountry && isValidCity
        return isValidCountry && isValidCity && isValidDescription
    }

    private fun validateForm() {
        val isValid = validateTheData(
            _countryLiveData.value,
            _cityLiveData.value,
            _descriptionLiveData.value
        )
        isDataValid.value = isValid
    }

}