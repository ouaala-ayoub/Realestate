package com.example.realestate.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.realestate.R
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentPostPageBinding
import com.example.realestate.ui.adapters.MediaPagerAdapter
import com.example.realestate.ui.viewmodels.PostPageModel
import com.example.realestate.utils.*
import com.google.android.material.snackbar.Snackbar


class PostPageFragment : Fragment() {

    companion object {
        private const val TAG = "PostPageFragment"
    }

    private lateinit var binding: FragmentPostPageBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var phoneNumber: String
    private val args: PostPageFragmentArgs by navArgs()
    private lateinit var imagesAdapter: MediaPagerAdapter
    private val postId: String by lazy {
        args.postId
    }
    private val postPageModel: PostPageModel by lazy {
        val retrofit = Retrofit.getInstance()
        PostPageModel(PostsRepository(retrofit), UsersRepository(retrofit)).apply {
            getPost(postId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = requestPermissionLauncher(object : PermissionResult {
            override fun onGranted() {
                call(phoneNumber)
            }

            override fun onNonGranted() {
                val snackBar = makeSnackBar(
                    requireView(),
                    getString(R.string.permission),
                    Snackbar.LENGTH_INDEFINITE
                )
                snackBar.setAction(R.string.OK) {
                    snackBar.dismiss()
                }.show()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostPageBinding.inflate(inflater, container, false)

        postPageModel.apply {
            seller.observe(viewLifecycleOwner) { seller ->
                binding.apply {
                    if (seller != null) {
                        ownerTextView.text = seller.name

                        val formatPhone = seller.phone.code + seller.phone.number
                        phoneNumber = formatPhone

                        message.setOnClickListener {
                            //whatsapp message
                            openWhatsapp(phoneNumber)
                        }
                        call.setOnClickListener {
                            //directly call
                            requireActivity().handlePermission(object : PermissionResult {
                                override fun onGranted() {
                                    call(phoneNumber)
                                }

                                override fun onNonGranted() {
                                    permissionLauncher.requestCallPermission()
                                }

                            }, listOf(android.Manifest.permission.CALL_PHONE))

                        }
                    }
                }
            }
            post.observe(viewLifecycleOwner) { post ->

                if (post != null) {
                    Log.d(TAG, "post = $post")
                    //get the owner
                    getUserById(post.ownerId)

                    //bind the data
                    binding.priceTextView.text = post.price.toString()
                    binding.categoryTextView.text = post.category
                    binding.locationTextView.text = getString(
                        R.string.location,
                        post.location.country,
                        post.location.city,
                        post.location.street
                    )
                    binding.typeTextView.text = post.type

                    //and the images
                    imagesAdapter = if (post.media.isNotEmpty())
                        MediaPagerAdapter(post.media)
                    else
                        MediaPagerAdapter(listOf("empty"))


                    binding.mediaVp.apply {
                        adapter = imagesAdapter
                        setPageTransformer(ZoomOutPageTransformer())
                    }
                } else {
                    //go back if error
                    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
                    findNavController().popBackStack()
                }
            }
            postLoading.observe(viewLifecycleOwner) { loading ->
                binding.postProgressBar.isVisible = loading
            }
        }

        return binding.root
    }

    private fun openWhatsapp(phoneNumber: String) {
        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun call(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }
}