package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.Post
import com.example.realestate.data.remote.network.BooleanHolder
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.handleApiRequest
import retrofit2.Response

class SavedViewModel(private val repository: UsersRepository) : ViewModel() {
    companion object {
        private const val TAG = "SavedViewModel"
        private const val NO_FAVOURITES = "No Favourites"
        private const val ERROR = "Unexpected Error"
    }

    private val _savedList = MutableLiveData<List<Post>?>()
    private val _loading = MutableLiveData<Boolean>()
    private val _deletedFromFav = MutableLiveData<BooleanHolder?>()
    private val _postsMessage = MutableLiveData<String>()


    val savedList: LiveData<List<Post>?>
        get() = _savedList
    val loading: LiveData<Boolean>
        get() = _loading
    val deletedFromFav: LiveData<BooleanHolder?>
        get() = _deletedFromFav
    val postsMessage: LiveData<String>
        get() = _postsMessage

    fun getSavedPosts(userId: String) {
        handleApiRequest(
            repository.getSavedPosts(userId),
            _loading,
            _savedList,
            TAG,
            object : AdditionalCode<List<Post>> {
                override fun onResponse(responseBody: Response<List<Post>>) {
                    if (responseBody.isSuccessful) {
                        if (responseBody.body()!!.isEmpty()) {
                            _postsMessage.postValue(NO_FAVOURITES)
                        } else {
                            _postsMessage.postValue("")
                        }
                    } else {
                        _postsMessage.postValue(ERROR)
                    }
                }

                override fun onFailure() {
                    _postsMessage.postValue(ERROR)
                }
            }
        )
    }

    fun deleteFromFavourites(userId: String, postId: String) {
        handleApiRequest(
            repository.addToFavourites(userId, postId),
            null,
            _deletedFromFav,
            TAG
        )
    }

}