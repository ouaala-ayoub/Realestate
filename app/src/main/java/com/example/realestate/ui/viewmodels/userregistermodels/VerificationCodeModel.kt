package com.example.realestate.ui.viewmodels.userregistermodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.utils.handleApiRequest

class VerificationCodeModel(private val repository: UsersRepository) : LoginModel(repository) {

    companion object {
        private const val TAG = "VerificationCodeModel"
    }

    private val _verificationCode = MutableLiveData<String>()
    private val _messageResponse = MutableLiveData<MessageResponse?>()
    private val _loading = super._isLoading

    val messageResponse: LiveData<MessageResponse?> get() = _messageResponse
    val isValid = MediatorLiveData<Boolean>().apply {
        addSource(_verificationCode) { code ->
            this.value = code.length == 6
        }
    }

    fun setVerificationCode(code: String) {
        _verificationCode.postValue(code)
    }

    fun addNumber(userId: String, number: String) {
        handleApiRequest(repository.addPhoneNumber(userId, number), _loading, _messageResponse, TAG)
    }

}