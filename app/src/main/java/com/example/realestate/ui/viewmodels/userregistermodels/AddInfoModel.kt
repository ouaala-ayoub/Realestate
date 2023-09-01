package com.example.realestate.ui.viewmodels.userregistermodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.AdditionalInfo
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.User
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.handleApiRequest

class AddInfoModel(private val repository: UsersRepository) :
    ViewModel() {

    companion object {
        private const val TAG = "AddInfoModel"
    }

    private val _user = MutableLiveData<User?>()
    private val _name = MutableLiveData<String>()
    private val _commMethod = MutableLiveData<String>()
    private val _messageResponse = MutableLiveData<MessageResponse?>()
    private val _loading = MutableLiveData<Boolean>()

    val isValid = MediatorLiveData<Boolean>().apply {
        addSource(_name) { name ->
            this.value = !name.isNullOrBlank()
        }
    }

    val user: LiveData<User?>
        get() = getAuth()
    val name: LiveData<String>
        get() = _name
    val commMethod: LiveData<String>
        get() = _commMethod
    val messageResponse: LiveData<MessageResponse?>
        get() = _messageResponse
    val loading: LiveData<Boolean>
        get() = _loading

    private fun getAuth(): MutableLiveData<User?> {
        handleApiRequest(repository.getAuth(), _loading, _user, TAG)
        return _user
    }

    fun updateName(name: String) {
        _name.postValue(name)
    }

    fun updateCommMethod(method: String) {
        _commMethod.postValue(method)
    }

    fun addInfoToUser(userId: String, data: AdditionalInfo) {
        handleApiRequest(repository.addData(userId, data), _loading, _messageResponse, TAG)
    }

}