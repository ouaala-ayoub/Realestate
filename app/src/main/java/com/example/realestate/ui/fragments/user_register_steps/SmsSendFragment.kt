package com.example.realestate.ui.fragments.user_register_steps

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.realestate.R
import com.example.realestate.databinding.FragmentSmsSendBinding
import com.example.realestate.ui.viewmodels.userregistermodels.SmsSendModel
import com.example.realestate.utils.OnVerificationCompleted
import com.example.realestate.utils.Task
import com.example.realestate.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential

class SmsSendFragment : Fragment() {

    companion object {
        private const val TAG = "SmsSendFragment"
    }

    private lateinit var binding: FragmentSmsSendBinding
    private val smsSendModel: SmsSendModel by lazy {
        SmsSendModel(FirebaseAuth.getInstance())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSmsSendBinding.inflate(inflater, container, false)


        binding.send.setOnClickListener {
            smsSendModel.sendVerification("+212658729171", object : OnVerificationCompleted {
                override fun onCodeSent(verificationId: String) {
                    goToVerifyCode(verificationId)
                }

                override fun onCompleted(credential: PhoneAuthCredential): Nothing? {
                    logUser(credential)
                    return super.onCompleted(credential)
                }

            }, requireActivity())
        }

        return binding.root
    }

    private fun goToVerifyCode(verificationId: String) {
        val action =
            SmsSendFragmentDirections.actionSmsSendFragmentToVerificationCodeFragment(verificationId)
        findNavController().navigate(action)
    }

    private fun goToAddInfo(phoneNumber: String) {
        val action = SmsSendFragmentDirections.actionSmsSendFragmentToAddInfoFragment2(phoneNumber)
        findNavController().navigate(action)
    }

    private fun logUser(credential: PhoneAuthCredential) {
        smsSendModel.signInWithPhoneAuthCredential(requireActivity(), credential, object : Task {
            override fun onSuccess(user: FirebaseUser?) {

                //consider sending the whole user

                val phoneNumber = user?.phoneNumber
                if (phoneNumber != null) {
                    goToAddInfo(phoneNumber)
                } else {
                    Log.e(TAG, "phoneNumber: null")
                }
            }

            override fun onFail(e: Exception?) {
                requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
                requireActivity().finish()
            }
        })
    }

}