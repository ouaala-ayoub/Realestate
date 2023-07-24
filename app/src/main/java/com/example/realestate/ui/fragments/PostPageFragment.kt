package com.example.realestate.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.eudycontreras.boneslibrary.extensions.disableSkeletonLoading
import com.eudycontreras.boneslibrary.extensions.enableSkeletonLoading
import com.example.realestate.PostNavArgs
import com.example.realestate.R
import com.example.realestate.data.models.CommunicationMethod
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.DetailsType
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentPostPageBinding
import com.example.realestate.ui.activities.UserRegisterActivity
import com.example.realestate.ui.adapters.DetailsAdapter
import com.example.realestate.ui.adapters.MediaPagerAdapter
import com.example.realestate.ui.viewmodels.PostPageModel
import com.example.realestate.utils.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.snackbar.Snackbar


class PostPageFragment : Fragment() {

    companion object {
        private const val TAG = "PostPageFragment"
    }

    private lateinit var binding: FragmentPostPageBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var phoneNumber: String
    private val args: PostNavArgs by navArgs()
    private lateinit var imagesAdapter: MediaPagerAdapter
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build()
    }
    private val postId: String by lazy {
        args.postId
    }
    private val postPageModel: PostPageModel by lazy {
        val retrofit = Retrofit.getInstance()
        PostPageModel(PostsRepository(retrofit), UsersRepository(retrofit)).apply {
            getPost(postId)
        }
    }
    private val detailsAdapter: DetailsAdapter by lazy {
        DetailsAdapter(DetailsType.LONG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "postId: $postId")
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

        binding.reportButton.setOnClickListener {
            val connected = CurrentUser.prefs.get() != null

            if (connected) {
                navigateToReportFragment()
            } else {
                goToActivity<UserRegisterActivity>(requireContext())
            }
        }
        binding.detailsRv.apply {
            adapter = detailsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postPageModel.apply {
            seller.observe(viewLifecycleOwner) { seller ->
                binding.apply {
                    if (seller != null) {
                        val commMethod = seller.communicationMethod
                        val all = CommunicationMethod.ALL.value
                        val whatsapp = CommunicationMethod.WHATSAPP.value
                        val callMe = CommunicationMethod.CALL.value

                        ownerTextView.defineField(seller.name, requireContext())

                        phoneNumber = seller.phone

                        message.apply {
                            isEnabled = commMethod == all || commMethod == whatsapp
                            if (isEnabled) {
                                message.setOnClickListener {
                                    //whatsapp message
                                    openWhatsapp(phoneNumber)
                                }
                            }
                        }

                        call.apply {
                            isEnabled = commMethod == all || commMethod == callMe
                            setOnClickListener {
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
                    } else {
                        //TODO
                    }
                }
            }
            post.observe(viewLifecycleOwner) { post ->

                if (post != null) {
                    Log.d(TAG, "post: $post")
                    //get the owner
                    getUserById(post.ownerId)

                    //bind the data
                    binding.apply {
                        imagePlaceholder.isVisible = false
                        priceTextView.defineField(
                            getString(R.string.price, post.price.toString()),
                            requireContext()
                        )
                        categoryTypeRv.defineField(
                            getString(
                                R.string.category_type,
                                post.category,
                                post.type
                            ), requireContext()
                        )
                        locationTextView.defineField(
                            getString(
                                R.string.location,
                                post.location.country,
                                post.location.city,
                                post.location.area
                            ),
                            requireContext()
                        )
                        descriptionRv.defineField(post.description, requireContext())
                        val details = post.details

                        if (details != null) {
                            detailsAdapter.setDetails(details)
                        }
//                        else {
//                            val placeholderMap = mutableMapOf<String, String>()
//                            placeholderMap[getString(R.string.no_defined)] =
//                                getString(R.string.no_defined)
//                            detailsAdapter.setDetails(placeholderMap)
//                        }
                        //and the images
                        imagesAdapter = if (post.media.isNotEmpty())
                            MediaPagerAdapter(post.media, exoPlayer)
                        else
                            MediaPagerAdapter(listOf("empty"))

                        mediaVp.apply {
                            adapter = imagesAdapter
                            setPageTransformer(ZoomOutPageTransformer())
                        }
                    }

                } else {
                    //go back if error
                    doOnFail()
                }
            }
            postLoading.observe(viewLifecycleOwner) { loading ->
                if (loading) {
                    binding.scrollView.enableSkeletonLoading()
                } else {
                    binding.scrollView.disableSkeletonLoading()
                }
                binding.postProgressBar.isVisible = loading
                binding.imagePlaceholder.isVisible = loading
            }
        }
    }

    private fun navigateToReportFragment() {
        val action =
            PostPageFragmentDirections.actionPostPageFragmentToReportFragment()
        findNavController().navigate(action)
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

    override fun onStop() {
        super.onStop()
        exoPlayer.release()
    }
}