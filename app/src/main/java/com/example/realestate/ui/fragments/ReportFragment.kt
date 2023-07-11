package com.example.realestate.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.realestate.PostNavArgs
import com.example.realestate.R
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.Report
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.ReportsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.FragmentReportBinding
import com.example.realestate.ui.viewmodels.ReportModel
import com.example.realestate.utils.doOnFail
import com.example.realestate.utils.setWithList
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
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)

        val list = listOf(
            "test1",
            "test2",
            "test3",
            "other"
        )
        binding.reasonEditText.setWithList(list)

        reportModel.apply {

            //temporary
            binding.reasonEditText.doOnTextChanged { text, _, _, _ ->
                binding.submit.isEnabled = list.contains(text.toString())
                //To change
                binding.messageEditText.isEnabled = text.toString() == list.last()
            }
            loading.observe(viewLifecycleOwner) { loading ->
                binding.reportProgressBar.isVisible = loading
            }
            reasonsList.observe(viewLifecycleOwner) { reasonsList ->
                if (reasonsList != null) {
                    binding.reasonEditText.apply {
                        //initialise the choice
                        setText(reasonsList[0])

                        //then set the other choices
                        setWithList(reasonsList)

                        binding.reasonEditText.doOnTextChanged { text, _, _, _ ->
                            binding.submit.isEnabled = reasonsList.contains(text.toString())

                            //To change
                            binding.messageEditText.isEnabled =
                                text.toString() == reasonsList.last()
                        }
                    }
                } else {
//                    doOnFail()
                }
            }
            reported.observe(viewLifecycleOwner) { message ->
                if (message != null) {
                    Log.d(TAG, "message: $message")
                    requireContext().toast(message.message, Toast.LENGTH_SHORT)
                } else {
                    doOnFail()
                }
                findNavController().popBackStack()
            }
        }

        binding.submit.setOnClickListener {
            val userId = CurrentUser.prefs.get()
            userId?.apply {
                val reason = binding.reasonEditText.text.toString()
                val message = binding.messageEditText.text.toString()

                val report = Report(postId, userId, reason)

                if (message.isNotEmpty()) {
                    report.message = message
                }

                reportModel.addReport(report)
            }

        }

        return binding.root
    }
}