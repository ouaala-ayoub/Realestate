package com.example.realestate.ui.fragments.user_register_steps

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.realestate.databinding.FragmentVerificationCodeBinding
import com.example.realestate.ui.viewmodels.userregistermodels.VerificationCodeModel
import com.example.realestate.utils.Task
import com.example.realestate.utils.disableBackButton
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider

class VerificationCodeFragment : Fragment() {

    companion object {
        private const val TAG = "VerificationCodeFragment"
    }

    private lateinit var binding: FragmentVerificationCodeBinding
    private val args: VerificationCodeFragmentArgs by navArgs()
    private val verificationModel: VerificationCodeModel by lazy {
        VerificationCodeModel()
    }
    private val verificationId: String by lazy {
        args.verificationId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "verificationId: $verificationId")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)
        requireActivity().disableBackButton(viewLifecycleOwner)

        binding.verify.setOnClickListener {

            val code = binding.verificationCodeEt.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationId, code)

            verificationModel.signInWithPhoneAuthCredential(
                requireActivity(),
                credential,
                object : Task {
                    override fun onSuccess(user: FirebaseUser?) {
                        val phone = user?.phoneNumber
                        if (phone != null) {
                            goToAddData(phone)
                        } else {
                            Log.d(TAG, "phone: is null")
                        }
                    }

                    override fun onFail(e: Exception?) {
                        Log.e(TAG, "onFail: ${e?.message}")
                        e?.printStackTrace()
                    }
                })
        }

        return binding.root
    }

    private fun goToAddData(phoneNumber: String) {
        val action =
            VerificationCodeFragmentDirections.actionVerificationCodeFragmentToAddInfoFragment3(
                phoneNumber
            )
        findNavController().navigate(action)
    }

}