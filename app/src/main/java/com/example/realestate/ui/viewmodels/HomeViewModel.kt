package com.example.realestate.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.Post
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.User
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.AdditionalCode
import com.example.realestate.utils.handleApiRequest
import retrofit2.Response

class HomeViewModel(
    private val postsRepository: PostsRepository,
    private val staticDataRepository: StaticDataRepository,
    private val usersRepository: UsersRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
        private const val NO_POST = "No posts"
        private const val NO_CATEGORIES = "No Categories"
        private const val ERROR = "Unexpected Error"
    }

    private val _user = MutableLiveData<User?>()
    private val _categoriesList = MutableLiveData<List<String>?>()
    private val _countries = MutableLiveData<List<String>?>()
    private val _postsList = MutableLiveData<MutableList<Post>?>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _postsMessage = MutableLiveData<String>()
    private val _categoriesMessage = MutableLiveData<String>()
    private val _liked = MutableLiveData<MessageResponse?>()
    private val _unliked = MutableLiveData<MessageResponse?>()


    val user: LiveData<User?>
        get() = _user
    val isProgressBarTurning: LiveData<Boolean>
        get() = _isLoading
    val postsList: LiveData<MutableList<Post>?>
        get() = _postsList
    val categoriesList: LiveData<List<String>?>
        get() = _categoriesList
    val countries: LiveData<List<String>?>
        get() = _countries
    val postsMessage: LiveData<String>
        get() = _postsMessage
    val categoriesMessage: LiveData<String>
        get() = _categoriesMessage
    val liked: LiveData<MessageResponse?>
        get() = _liked
    val unliked: LiveData<MessageResponse?>
        get() = _unliked

    // no filters by default
    fun getPosts(
        searchParams: SearchParams = SearchParams(),
        source: String
    ): MutableLiveData<MutableList<Post>?> {
        Log.i(TAG, "requested data yes source = $source")
        handleApiRequest(
            postsRepository.getPosts(searchParams),
            _isLoading,
            _postsList,
            TAG,
            object : AdditionalCode<MutableList<Post>> {
                override fun onResponse(responseBody: Response<MutableList<Post>>) {
                    if (responseBody.isSuccessful) {
                        if (responseBody.body()!!.isEmpty()) {
                            _postsMessage.postValue(NO_POST)
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
        return _postsList
    }

    fun getCategories(): MutableLiveData<List<String>?> {
        Log.d(TAG, "getCategories")
        handleApiRequest(
            staticDataRepository.getCategories(),
            _isLoading,
            _categoriesList,
            TAG,
            object : AdditionalCode<List<String>> {
                override fun onResponse(responseBody: Response<List<String>>) {
                    if (responseBody.isSuccessful) {
                        if (responseBody.body()!!.isEmpty()) {
                            _categoriesMessage.postValue(NO_CATEGORIES)
                        } else {
                            _categoriesMessage.postValue("")
                        }
                    } else {
                        _categoriesMessage.postValue(ERROR)
                    }
                }

                override fun onFailure() {
                    _categoriesMessage.postValue(ERROR)
                }
            }

        )
        return _categoriesList
    }

    fun getUserById(userId: String) {
        handleApiRequest(usersRepository.getUserById(userId), null, _user, TAG)
    }

    fun like(postId: String) {
        handleApiRequest(
            usersRepository.like(postId),
            null,
            _liked,
            TAG
        )
    }

    fun unlike(postId: String) {
        handleApiRequest(
            usersRepository.unlike(postId),
            null,
            _unliked,
            TAG
        )
    }
}