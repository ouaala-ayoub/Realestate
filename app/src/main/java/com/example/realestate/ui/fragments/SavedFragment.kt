package com.example.realestate.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.realestate.databinding.FragmentSavedBinding
import com.example.realestate.ui.viewmodels.SavedViewModel

class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private val viewModel: SavedViewModel by lazy {
        SavedViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(inflater, container, false)



        return binding.root
    }

}