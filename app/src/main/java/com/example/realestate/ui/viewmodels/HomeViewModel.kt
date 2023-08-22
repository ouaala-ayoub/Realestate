package com.example.realestate.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.*
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

    var currentPage = MutableLiveData(1)
    private val _user = MutableLiveData<User?>()
    private val _categoriesList = MutableLiveData<List<String>?>()
    private val _countries = MutableLiveData<CountriesData?>()
    private val _postsList =
        MutableLiveData(mutableListOf<PostWithOwnerId>())
    private val _isLoading = MutableLiveData<Boolean>()
    private val _postsMessage = MutableLiveData<String>()
    private val _categoriesMessage = MutableLiveData<String>()
    private val _liked = MutableLiveData<MessageResponse?>()
    private val _unliked = MutableLiveData<MessageResponse?>()


    val user: LiveData<User?>
        get() = _user
    val isProgressBarTurning: LiveData<Boolean>
        get() = _isLoading
    val postsList: LiveData<MutableList<PostWithOwnerId>?>
        get() = _postsList
    val categoriesList: LiveData<List<String>?>
        get() = _categoriesList
    val countries: LiveData<CountriesData?>
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
        source: String,
        override: Boolean = true
    ): MutableLiveData<MutableList<PostWithOwnerId>?> {
        Log.i(TAG, "requested data yes source = $source")
        Log.d(TAG, "search params page: ${searchParams.page}")
        Log.d(TAG, "currentPage: $currentPage")
        if (override) currentPage.value = 1
        handleApiRequest(
            postsRepository.getPosts(searchParams),
            _isLoading,
            null,
            TAG,
            object : AdditionalCode<MutableList<PostWithOwnerId>> {
                override fun onResponse(responseBody: Response<MutableList<PostWithOwnerId>>) {
                    Log.d(TAG, "size: ${responseBody.body()?.size}")
                    if (responseBody.isSuccessful) {
                        if (override) {
                            Log.d(TAG, "override: yes")
                            currentPage.postValue(2)
                            if (responseBody.body()!!.isEmpty()) {
                                _postsMessage.postValue(NO_POST)
                            } else {
                                _postsMessage.postValue("")
                            }
                            _postsList.postValue(responseBody.body())
                        } else {
                            Log.d(TAG, "override: no")
                            if (responseBody.body()!!.isEmpty()) {
                                //TODO request posts ?
//                                currentPage.postValue(1)
//                                getPosts(
//                                    source = "responseBody.body()!!.isEmpty()",
//                                    searchParams = searchParams, override = false
//                                )
                            } else {
                                currentPage.value = currentPage.value!! + 1
                                val posts = _postsList.value
                                posts?.apply {
                                    this.addAll(responseBody.body()!!)
                                    _postsList.postValue(this)
                                }
                            }
                        }
                    } else {
                        _postsMessage.postValue(ERROR)
                        _postsList.postValue(null)
                    }
                }

                override fun onFailure() {
                    _postsMessage.postValue(ERROR)
                    _postsList.postValue(null)
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

    fun getCountries() {
        handleApiRequest(staticDataRepository.getCountries(), _isLoading, _countries, TAG)
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