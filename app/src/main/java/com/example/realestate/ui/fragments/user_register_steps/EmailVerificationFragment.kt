package com.example.realestate.ui.fragments.user_register_steps

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.realestate.R
import com.example.realestate.data.models.CurrentUser
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

    private var _binding: FragmentEmailVerificationBinding?=null
    private val binding get() = _binding!!
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
                user?.getIdToken(false)?.addOnCompleteListener { task ->
                    val tokenId = task.result.token
                    if (tokenId != null) {
                        viewModel.login(tokenId)

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)

        binding.signInGoogle.setOnClickListener {
            viewModel.launchGmailAuth(signInLauncher)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.user.observe(viewLifecycleOwner) { user ->
            Log.d(TAG, "user: $user")
            if (user != null) {
                CurrentUser.set(user)
                goToAddData(user.id!!)
            } else
                onFail(getString(R.string.error))
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.emailVerificationProgressbar.isVisible = loading
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun goToAddData(userId: String) {
        val action =
            EmailVerificationFragmentDirections.actionEmailVerificationFragmentToAddInfoFragment2(
                userId
            )
        findNavController().navigate(action)
    }
    fun onFail(message: String) {
        requireContext().toast(message, Toast.LENGTH_SHORT)
    }
}