package com.example.realestate.ui.fragments.user_register_steps

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
import com.example.realestate.R
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentVerificationCodeBinding
import com.example.realestate.ui.viewmodels.userregistermodels.VerificationCodeModel
import com.example.realestate.utils.Task
import com.example.realestate.utils.disableBackButton
import com.example.realestate.utils.toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthProvider

class VerificationCodeFragment : Fragment() {

    companion object {
        private const val TAG = "VerificationCodeFragment"
    }

    private lateinit var binding: FragmentVerificationCodeBinding
    private val args: VerificationCodeFragmentArgs by navArgs()
    private val verificationModel: VerificationCodeModel by lazy {
        VerificationCodeModel(UsersRepository(Retrofit.getInstance()))
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

        verificationModel.apply {

            binding.verificationCodeEt.doOnTextChanged { code, _, _, _ ->
                setVerificationCode(code.toString())
            }

            isLoading.observe(viewLifecycleOwner) { loading ->
                binding.verifyProgressBar.isVisible = loading
                updateUi(loading)
            }
            isValid.observe(viewLifecycleOwner) { dataValid ->
                binding.verify.isEnabled = dataValid
            }


        }

        binding.verify.setOnClickListener {

            val code = binding.verificationCodeEt.text.toString()
            val credential = PhoneAuthProvider.getCredential(verificationId, code)

            verificationModel.signInWithPhoneAuthCredential(
                requireActivity(),
                credential,
                object : Task {
                    override fun onSuccess(user: FirebaseUser?) {
                        user?.getIdToken(false)?.addOnCompleteListener {
                            val tokenId = it.result.token
                            if (tokenId != null) {
                                Log.d(TAG, "tokenId: $tokenId")
                                verificationModel.login(tokenId)
                                verificationModel.userId.observe(viewLifecycleOwner) { userId ->
                                    Log.d(TAG, "userId: $userId")
                                    if (userId != null) {
                                        goToAddData(userId.id, tokenId)
                                    } else {
                                        onFail()
                                    }
                                }
                            } else {
                                onFail()
                            }

                        }
                    }

                    override fun onFail(e: Exception?) {
                        Log.e(TAG, "onFail: ${e?.message}")
                        e?.printStackTrace()
                        onFail()
                    }
                })
        }

        return binding.root
    }


    private fun onFail() {
        requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
        requireActivity().finish()
    }

    private fun updateUi(loading: Boolean) {
        binding.apply {
            verificationCodeEt.isEnabled = !loading
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

}