package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.Post
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest

class HomeViewModel(
    private val postsRepository: PostsRepository,
    private val staticDataRepository: StaticDataRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _categoriesList = MutableLiveData<List<String>?>()
    private val _countries = MutableLiveData<List<String>?>()
    private val _postsList = MutableLiveData<MutableList<Post>?>()
    private val _isLoading = MutableLiveData<Boolean>()

    val isProgressBarTurning: LiveData<Boolean>
        get() = _isLoading
    val postsList: LiveData<MutableList<Post>?>
        get() = getPosts()
    val categoriesList: LiveData<List<String>?>
        get() = _categoriesList
    val countries: LiveData<List<String>?>
        get() = _countries


    // no filters by default
    fun getPosts(searchParams: SearchParams = SearchParams()): MutableLiveData<MutableList<Post>?> {
        handleApiRequest(
            postsRepository.getPosts(searchParams),
            _isLoading,
            _postsList,
            TAG
        )
        return _postsList
    }

    fun getCategories(): MutableLiveData<List<String>?> {
        handleApiRequest(
            staticDataRepository.getCategories(),
            _isLoading,
            _categoriesList,
            TAG
        )
        return _categoriesList
    }

}