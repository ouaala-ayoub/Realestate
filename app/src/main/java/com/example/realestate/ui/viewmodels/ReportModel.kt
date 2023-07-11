package com.example.realestate.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.realestate.data.models.MessageResponse
import com.example.realestate.data.models.Report
import com.example.realestate.data.repositories.ReportsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.utils.handleApiRequest

class ReportModel(
    private val reportsRepository: ReportsRepository,
    private val staticDataRepository: StaticDataRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ReportModel"
    }

    private val _reported = MutableLiveData<MessageResponse?>()
    private val _reasonsList = MutableLiveData<List<String>?>()
    private val _loading = MutableLiveData<Boolean>()

    val reported: LiveData<MessageResponse?>
        get() = _reported
    val loading: LiveData<Boolean>
        get() = _loading
    val reasonsList: LiveData<List<String>?>
        get() = _reasonsList

    fun addReport(reportToAdd: Report) {
        handleApiRequest(reportsRepository.addReport(reportToAdd), _loading, _reported, TAG)
    }

    fun getReasons() {
        handleApiRequest(staticDataRepository.getReportReasons(), _loading, _reasonsList, TAG)
    }

}