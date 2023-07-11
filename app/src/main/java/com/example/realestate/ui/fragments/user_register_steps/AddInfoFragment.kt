package com.example.realestate.ui.fragments.user_register_steps

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.navArgs
import com.example.realestate.R
import com.example.realestate.data.models.AdditionalInfo
import com.example.realestate.data.models.CommunicationMethod
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentAddInfoBinding
import com.example.realestate.ui.viewmodels.userregistermodels.AddInfoModel
import com.example.realestate.utils.toast

class AddInfoFragment : Fragment() {

    companion object {
        private const val TAG = "AddInfoFragment"
    }

    private lateinit var binding: FragmentAddInfoBinding
    private val args: AddInfoFragmentArgs by navArgs()
    private val userId: String by lazy {
        args.userId
    }
    private val addInfoModel: AddInfoModel by lazy {
        AddInfoModel(UsersRepository(Retrofit.getInstance()), userId)
    }
    private val tokenId: String by lazy {
        args.tokenId
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

        //imitate back button from activity behaviour
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }

            })


        binding.apply {

            //all by default
            addInfoModel.apply {
                user.observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        user.name?.apply {
                            nameEditText.setText(this)
                        }
                        updateCommMethod(user.communicationMethod)
                    }
                }
            }


            nameEditText.doOnTextChanged { name, _, _, _ ->
                addInfoModel.updateName(name.toString())
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val selectedMethod = when (checkedId) {
                    R.id.whatsapp -> CommunicationMethod.WHATSAPP
                    R.id.call -> CommunicationMethod.CALL
                    R.id.both -> CommunicationMethod.ALL
                    else -> null
                }
                selectedMethod?.let { addInfoModel.updateCommMethod(it.value) }
            }

            finish.apply {
                addInfoModel.loading.observe(viewLifecycleOwner) { loading ->
                    binding.progressBar2.isVisible = loading
                    updateUi(loading)
                }
                addInfoModel.messageResponse.observe(viewLifecycleOwner) { message ->
                    if (message != null) {
                        Log.d(TAG, "message: ${message.message}")
                        requireContext().toast(getString(R.string.success), Toast.LENGTH_SHORT)
                        requireActivity().finish()
                    } else {
                        onFail()
                    }
                }
                addInfoModel.isValid.observe(viewLifecycleOwner) { isValid ->
                    Log.d(TAG, "isValid: $isValid")
                    isEnabled = isValid

                }
                setOnClickListener {
                    val name = addInfoModel.name.value!!
                    val method = addInfoModel.commMethod.value!!

                    addInfoModel.addInfoToUser(userId, AdditionalInfo(name, method))
                }
            }
        }

        return binding.root
    }

    private fun updateUi(loading: Boolean) {
        binding.apply {
            finish.isEnabled = !loading
            nameEditText.isEnabled = !loading
            for (e in radioGroup.children) {
                e.isEnabled = !loading
            }
        }
    }

    private fun onFail() {
        requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
        requireActivity().finish()
    }
}