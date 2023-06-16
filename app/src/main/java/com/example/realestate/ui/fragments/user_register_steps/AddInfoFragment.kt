package com.example.realestate.ui.fragments.user_register_steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.realestate.databinding.FragmentAddInfoBinding

class AddInfoFragment : Fragment() {

    private lateinit var binding: FragmentAddInfoBinding
    private val args: AddInfoFragmentArgs by navArgs()
    private val phoneNumber: String by lazy {
        args.phoneNumber
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddInfoBinding.inflate(inflater, container, false)

        binding.phoneAdd.text = phoneNumber

        return binding.root
    }
}