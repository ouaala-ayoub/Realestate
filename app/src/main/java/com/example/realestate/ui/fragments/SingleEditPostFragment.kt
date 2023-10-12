package com.example.realestate.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.realestate.R
import com.example.realestate.databinding.FragmentSingleEditPostBinding
import com.example.realestate.ui.viewmodels.SingleEditPostViewModel

class SingleEditPostFragment : Fragment() {

    companion object {
        private const val TAG = "SingleEditPostFragment"
    }

    private val viewModel: SingleEditPostViewModel by viewModels()
    private var _binding: FragmentSingleEditPostBinding? = null
    private val binding get() = _binding!!
    private val args: SingleEditPostFragmentArgs by navArgs()
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = args.postId
        Log.d(TAG, "postId: $postId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val startTime = System.nanoTime()
        _binding = FragmentSingleEditPostBinding.inflate(inflater, container, false)
        val endTime = System.nanoTime()
        val elapsedTime = (endTime - startTime) / 1000000
        Log.d(TAG, "onCreateView inflating function took $elapsedTime ms to execute ")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}