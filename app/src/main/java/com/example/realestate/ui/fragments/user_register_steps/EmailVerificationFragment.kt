package com.example.realestate.ui.fragments.user_register_steps

import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.realestate.R
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentEmailVerificationBinding
import com.example.realestate.ui.viewmodels.EmailVerificationModel
import com.example.realestate.utils.Task
import com.example.realestate.utils.toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseUser

class EmailVerificationFragment : Fragment() {

    companion object {
        private const val TAG = "EmailVerificationFragment"
    }

    private lateinit var binding: FragmentEmailVerificationBinding
    private val viewModel: EmailVerificationModel by lazy {
        EmailVerificationModel(
            UsersRepository(
                Retrofit.getInstance()
            )
        )
    }
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        viewModel.onSignInResult(res, object : Task {
            override fun onSuccess(user: FirebaseUser?) {
                Log.d(TAG, "user: ${user?.email}")
                user?.getIdToken(false)?.addOnCompleteListener { task ->
                    val tokenId = task.result.token
                    if (tokenId != null) {
                        Log.i(TAG, "tokenId: $tokenId")
                        viewModel.login(tokenId)
                        viewModel.user.observe(viewLifecycleOwner) { user ->
                            Log.i(TAG, "user: $user")
                            if (user != null)
                                goToAddData(user.id!!, tokenId)
                            else
                                onFail(getString(R.string.error))

                        }
                    } else {
                        onFail(getString(R.string.error))
                    }
                }
            }

            override fun onFail(e: Exception?) {
                e?.printStackTrace()
                requireContext().toast(getString(R.string.try_again), Toast.LENGTH_SHORT)
            }
        })

    }

    private fun goToAddData(userId: String, tokenId: String) {
        val action =
            EmailVerificationFragmentDirections.actionEmailVerificationFragmentToAddInfoFragment2(
                userId, tokenId
            )
        findNavController().navigate(action)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onFail(message: String) {
        requireContext().toast(message, Toast.LENGTH_SHORT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)

        binding.signInGoogle.setOnClickListener {
            viewModel.launchGmailAuth(signInLauncher)
        }

        return binding.root
    }

}