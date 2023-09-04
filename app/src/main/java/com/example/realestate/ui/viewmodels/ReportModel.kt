package com.example.realestate.ui.viewmodels

import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.lifecycle.*
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.Report
import com.example.realestate.data.repositories.ReportsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest
import com.example.realestate.utils.updateLiveData

class ReportModel(
    private val reportsRepository: ReportsRepository,
    private val staticDataRepository: StaticDataRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ReportModel"
        private const val MAX_MESSAGE_SIZE = 250
    }

    private val _reported = MutableLiveData<MessageResponse?>()
    private val _reasonsList = MutableLiveData<List<String>?>()
    private val _userReasons = MutableLiveData(mutableListOf<String>())
    private val _loading = MutableLiveData<Boolean>()
    private val _requestSent = MutableLiveData<Boolean>()
    private val _message = MutableLiveData("")
    val isDataValid = MediatorLiveData<Boolean>().apply {
        addSource(_userReasons) { this.value = validate() }
        addSource(message) { this.value = validate() }
    }

    val requestSent: LiveData<Boolean> get() = _requestSent
    val reported: LiveData<MessageResponse?> get() = _reported
    val loading: LiveData<Boolean> get() = _loading
    val reasonsList: LiveData<List<String>?> get() = _reasonsList
    val userReasons: LiveData<MutableList<String>?> get() = _userReasons
    val message: LiveData<String> get() = _message


    private fun validate(): Boolean {
        return validateData(_userReasons.value, _message.value)
    }

    private fun validateData(usersReasons: List<String?>?, message: String?): Boolean {
        val validReasons = !usersReasons.isNullOrEmpty()
        val containsOther = usersReasons?.contains(reasonsList.value?.last()) == true
        val validMessageSize = !message.isNullOrEmpty() && message.length < MAX_MESSAGE_SIZE
        val validMessage =
            containsOther && validMessageSize || !containsOther && message.isNullOrEmpty()
        return validReasons && validMessage
    }

    fun addReport(reportToAdd: Report) {
        _requestSent.postValue(true)
        handleApiRequest(reportsRepository.addReport(reportToAdd), _loading, _reported, TAG)
    }

    fun getReasons() {
        handleApiRequest(staticDataRepository.getReportReasons(), _loading, _reasonsList, TAG)
    }

    fun ViewGroup.fillWithCheckBoxes(reasonsList: List<String>) {
        reasonsList.forEach { reason ->
            val checkBox = CheckBox(context)
            checkBox.apply {
                text = reason
                id = ViewCompat.generateViewId()
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(5, 5, 5, 5)
                setOnClickListener {
                    val reasons = _userReasons.value

                    if (isChecked) {
                        val added = reasons?.add(text.toString())
                        if (added == true)
                            _userReasons.value = reasons
                    } else {
                        val removed = reasons?.remove(text.toString())
                        if (removed == true)
                            _userReasons.value = reasons

                    }
                }
            }
            addView(checkBox)
        }
    }

    fun handleMessageChanges(editText: EditText) {
        editText.updateLiveData(_message)
    }

}