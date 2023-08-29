package com.example.realestate.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.realestate.PostNavArgs
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.Report
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.ReportsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.FragmentReportBinding
import com.example.realestate.ui.viewmodels.ReportModel
import com.example.realestate.utils.doOnFail
import com.example.realestate.utils.toast

class ReportFragment : Fragment() {

    companion object {
        private const val TAG = "ReportFragment"
    }

    private lateinit var binding: FragmentReportBinding
    private lateinit var reportModel: ReportModel
    private val args: PostNavArgs by navArgs()
    private val postId: String by lazy {
        args.postId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofitService = Retrofit.getInstance()
        reportModel = ReportModel(
            ReportsRepository(retrofitService),
            StaticDataRepository(retrofitService)
        ).also {
            it.getReasons()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)

        Log.d(TAG, "postId: $postId")

        binding.submit.setOnClickListener {
            if (CurrentUser.isConnected()) {
                val userId = CurrentUser.prefs.get()
                userId?.apply {
                    val reasons = reportModel.userReasons.value?.toList()
                    val message = reportModel.message.value

                    val report = Report(postId, userId, reasons!!)

                    if (!message.isNullOrEmpty()) {
                        report.message = message
                    }

                    reportModel.addReport(report)
                }
            } else {
                doOnFail()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportModel.apply {

            isDataValid.observe(viewLifecycleOwner) { valid ->
                binding.submit.isEnabled = valid
            }

            reasonsList.observe(viewLifecycleOwner) { reasonsList ->

                if (reasonsList != null) {

                    binding.reasonsContainer.apply {
                        fillWithCheckBoxes(reasonsList)
                        userReasons.observe(viewLifecycleOwner) { userReasons ->
                            Log.i(TAG, "userReasons: $userReasons")
                            if (userReasons != null) {
                                binding.messageEditText.isEnabled =
                                    userReasons.contains(reasonsList.last())
                            }

                        }
                    }

                } else {
//                    doOnFail()
                }
            }

            loading.observe(viewLifecycleOwner) { loading ->
                binding.reportProgressBar.isVisible = loading
                blockUi(loading)
            }

            reported.observe(viewLifecycleOwner) { message ->
                if (message != null) {
                    Log.d(TAG, "message: $message")
                    requireContext().toast(message.message, Toast.LENGTH_SHORT)
                    findNavController().popBackStack()
                } else {
                    doOnFail()
                }

            }

        }

    }

    private fun blockUi(loading: Boolean) {
        binding.apply {
            reasonsContainer.forEach { view ->
                view.isEnabled = loading
            }
            messageEditText.isEnabled = loading
            submit.isEnabled = loading
        }
    }
}

