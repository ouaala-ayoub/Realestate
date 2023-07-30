package com.example.realestate.ui.fragments.user_register_steps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.realestate.R
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentVerificationCodeBinding
import com.example.realestate.ui.viewmodels.userregistermodels.VerificationCodeModel
import com.example.realestate.utils.Task
import com.example.realestate.utils.disableBackButton
import com.example.realestate.utils.doOnFail
import com.example.realestate.utils.toast
import com.fraggjkee.smsconfirmationview.SmsConfirmationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher


class VerificationCodeFragment : Fragment() {

    companion object {
        private const val TAG = "VerificationCodeFragment"
    }

    private lateinit var smsVerifyCatcher: SmsVerifyCatcher
    private lateinit var binding: FragmentVerificationCodeBinding
    private val args: VerificationCodeFragmentArgs by navArgs()
    private val verificationModel: VerificationCodeModel by lazy {
        VerificationCodeModel(UsersRepository(Retrofit.getInstance()))
    }
    private val verificationId: String by lazy {
        args.verificationId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerificationCodeBinding.inflate(inflater, container, false)
        requireActivity().disableBackButton(viewLifecycleOwner)

        verificationModel.apply {

            //TODO
//            smsVerifyCatcher = SmsVerifyCatcher(
//                requireActivity()
//            ) { message ->
//                Log.d(TAG, "message: $message")
////                val code: String = parseCode(message) //Parse verification code
////                etCode.setText(code) //set code in edit text
//                //then you can send verification code to server
//            }

            binding.smsVerificationCodeEt.onChangeListener =
                SmsConfirmationView.OnChangeListener { code, _ ->
                    setVerificationCode(code)
                }


        }

        binding.verify.setOnClickListener {

//            val code = binding.verificationCodeEt.code
            val code = binding.smsVerificationCodeEt.enteredCode
            val credential = PhoneAuthProvider.getCredential(verificationId, code)

            verificationModel.signInWithPhoneAuthCredential(
                requireActivity(),
                credential,
                object : Task {
                    override fun onSuccess(user: FirebaseUser?) {

                        val userId = CurrentUser.prefs.get()
                        if (userId != null) {
                            val phoneNumber = user?.phoneNumber
                            Log.d(TAG, "phoneNumber: $phoneNumber")
                            if (phoneNumber != null) {
                                verificationModel.addNumber(userId, phoneNumber)
                            } else {
                                onFail(getString(R.string.error))
                            }
                        } else {
                            onFail(getString(R.string.error))
                        }

//                        user?.getIdToken(false)?.addOnCompleteListener {
//                            val tokenId = it.result.token
//                            if (tokenId != null) {
//                                val userId = CurrentUser.prefs.get()
//                                if (userId != null) {
////                                    goToAddData(userId, tokenId)
//                                    val phoneNumber = user.phoneNumber
//                                    if (phoneNumber != null) {
//                                        verificationModel.addNumber(userId, phoneNumber)
//                                    } else {
//                                        onFail(getString(R.string.error))
//                                    }
//                                } else {
//                                    onFail(getString(R.string.error))
//                                }
//                            } else {
//                                onFail(getString(R.string.wrong_code))
//                            }
//
//                        }
                    }

                    override fun onFail(e: Exception?) {
                        e?.printStackTrace()
                        onFail(getString(R.string.wrong_code))
                    }
                })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verificationModel.apply {
            isLoading.observe(viewLifecycleOwner) { loading ->
                binding.verifyProgressBar.isVisible = loading
                updateUi(loading)
            }

            isValid.observe(viewLifecycleOwner) { dataValid ->
                binding.verify.isEnabled = dataValid
            }

            messageResponse.observe(viewLifecycleOwner) { message ->
                if (message != null) {
                    finishActivity()
                } else {
                    requireActivity().doOnFail()
                }
            }
        }
    }

    private fun onFail(message: String) {
        requireContext().toast(message, Toast.LENGTH_SHORT)
        requireActivity().finish()
    }

    private fun updateUi(loading: Boolean) {
        binding.apply {
            smsVerificationCodeEt.isEnabled = !loading
            verify.isEnabled = !loading
        }
    }

    private fun goToAddData(userId: String, token: String) {
        val action =
            VerificationCodeFragmentDirections.actionVerificationCodeFragmentToAddInfoFragment3(
                userId,
                token
            )
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
//        smsVerifyCatcher.onStart()
    }

    override fun onStop() {
        super.onStop()
//        smsVerifyCatcher.onStop()
    }

    private fun finishActivity() {
        val resultIntent = requireActivity().intent
        resultIntent.putExtra(
            "phone_verified",
            true
        ) // Optionally, pass any data back to the AddPostActivity
        requireActivity().setResult(Activity.RESULT_OK, resultIntent)
        requireActivity().finish()
    }

}