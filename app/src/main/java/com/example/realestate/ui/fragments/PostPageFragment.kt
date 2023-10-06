package com.example.realestate.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.eudycontreras.boneslibrary.extensions.disableSkeletonLoading
import com.eudycontreras.boneslibrary.extensions.enableSkeletonLoading
import com.example.realestate.PostNavArgs
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentPostPageBinding
import com.example.realestate.ui.activities.MainActivity
import com.example.realestate.ui.adapters.DetailsLongAdapter
import com.example.realestate.ui.adapters.MediaPagerAdapter
import com.example.realestate.ui.viewmodels.PostPageModel
import com.example.realestate.utils.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.snackbar.Snackbar


class PostPageFragment : Fragment() {

    companion object {
        private const val TAG = "PostPageFragment"
    }

    private var _binding: FragmentPostPageBinding?=null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var phoneNumber: String
    private lateinit var loginLauncher: ActivityResultLauncher<Intent>
    private lateinit var activity: MainActivity
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
    private val detailsAdapter: DetailsLongAdapter by lazy {
        DetailsLongAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "postId: $postId")

        activity = requireActivity() as MainActivity
        loginLauncher = startActivityResult(object : SelectionResult {
            override fun onResultOk(data: Intent) {

                val registerSuccess = data.getBooleanExtra("register_success", false)

                Log.i(TAG, "registerSuccess: $registerSuccess")

                if (registerSuccess) {
                    navigateToReportFragment(postId)
                } else {
                    requireContext().toast(getString(R.string.register_first), Toast.LENGTH_SHORT)
                }
            }

            override fun onResultFailed() {
                requireContext().toast(getString(R.string.register_first), Toast.LENGTH_SHORT)
            }

        })

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
        _binding = FragmentPostPageBinding.inflate(inflater, container, false)

        binding.reportButton.setOnClickListener {

            //TODO get activity for result instead of goToActivity
            if (CurrentUser.isConnected()) {
                navigateToReportFragment(postId)
            } else {
                activity.launchRegisterProcess(loginLauncher)
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


            post.observe(viewLifecycleOwner) { post ->
                Log.d(TAG, "post: $post")

                if (post != null) {

                    likes.observe(viewLifecycleOwner) { likes ->
                        binding.numberOfLikes.text = formatNumberWithSpaces(likes)
                        CurrentUser.getAuth()
                    }

                    val contact = post.contact
                    val owner = post.owner
                    phoneNumber = "${contact.code}${contact.phoneNumber}"
                    val deepLink = "https://realestatefy.vercel.app/posts/${post.id}"
                    val country = getString(R.string.country_res, post.location.country)
                    val city = getString(R.string.city_res, post.location.city)
                    val area = if (!post.location.area.isNullOrEmpty()) {
                        getString(R.string.area_res, post.location.area)
                    } else {
                        getString(R.string.area_res, "-")
                    }

                    //bind the data
                    binding.apply {
//                        imagePlaceholder.isVisible = false

                        categoryTypeRv.defineField(
                            getString(
                                R.string.category_type,
                                post.category,
                                post.type
                            )
                        )

                        setLikes(post.likes)
                        //Likes
                        CurrentUser.observe(viewLifecycleOwner, object : OnChanged<User> {
                            override fun onChange(data: User?) {
                                val isChecked = data?.likes?.contains(post.id) == true
                                addToFav.isChecked = isChecked
                                addToFav.isEnabled = CurrentUser.isConnected()


                            }

                        })
                        addToFav.setOnClickListener {
                            val isChecked = CurrentUser.get()?.likes?.contains(post.id) == true
                            if (CurrentUser.isConnected()) {
                                if (isChecked) {
                                    unlike(postId)
                                } else {
                                    like(postId)
                                }
                            }
                        }

                        //handle price
                        when (post.type) {
                            Type.RENT.value -> {
                                priceTextView.defineField(
                                    getString(
                                        R.string.price_rent,
                                        formatNumberWithCommas(post.price),
                                        post.period
                                    ),
                                )
                            }
                            else -> {
                                priceTextView.defineField(
                                    getString(R.string.price, formatNumberWithCommas(post.price)),
                                )
                            }
                        }

                        share.setOnClickListener {
                            shareDeepLink(deepLink)
                        }

                        countryTv.text = loadHtml(country)
                        cityTv.text = loadHtml(city)
                        areaTv.text = loadHtml(area)

                        descriptionRv.defineField(post.description)
                        val features = post.features
                        val map = mutableMapOf<String, String>()

                        if (features != null) {
                            for (element in features) {
                                map[element] = element
                            }
                        }
                        post.apply {
                            condition?.apply {
                                map["Property Condition"] = this
                            }
                            rooms?.apply { map["Number Of rooms"] = this.toString() }
                            bathrooms?.apply { map["Number of bathrooms"] = this.toString() }
                            floors?.apply {
                                floorNumber?.apply {
                                    map["Floor Info"] = "Floor n° $floorNumber out f $floors"
                                }
                            }
                            space?.apply {
                                val footValue =
                                    formatDecimal(squareMeterToSquareFoot(this.toDouble()), 2)
                                map["Space"] =
                                    "$this m² = $footValue foot²"
                            }
                        }

                        detailsAdapter.setFeatures(map)
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

                        message.setOnClickListener {
                            when (contact.type) {
                                ContactType.WHATSAPP.value -> {
                                    openWhatsapp(phoneNumber)
                                }
                                ContactType.Both.value -> {
                                    openWhatsapp(phoneNumber)
                                }
                                else -> {
                                    handleUnsupportedAction()
                                }
                            }
                        }

                        call.setOnClickListener {
                            when (contact.type) {
                                ContactType.CALL.value -> {
                                    handleCallClick(phoneNumber)
                                }
                                ContactType.Both.value -> {
                                    handleCallClick(phoneNumber)
                                }
                                else -> {
                                    handleUnsupportedAction()
                                }
                            }
                        }

                        owner?.apply {
                            ownerImage.loadImage(image, R.drawable.baseline_person_24)
                            ownerTv.defineField(name)
                            ownerJoinDateTv.defineField(
                                getString(
                                    R.string.join_date,
                                    extractDate(owner.createdAt)
                                )
                            )
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
//                binding.imagePlaceholder.isVisible = loading
            }
        }
    }



    private fun handleUnsupportedAction() {
        requireContext().toast(
            getString(R.string.no_call_msg),
            Toast.LENGTH_SHORT
        )
    }

    private fun handleCallClick(phoneNumber: String) {
        requireActivity().handlePermission(object : PermissionResult {
            override fun onGranted() {
                call(phoneNumber)
            }

            override fun onNonGranted() {
                permissionLauncher.requestCallPermission()
            }

        }, listOf(android.Manifest.permission.CALL_PHONE))
    }

    private fun navigateToReportFragment(postsId: String) {
        val action =
            PostPageFragmentDirections.actionPostPageFragmentToReportFragment(postsId)
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

    private fun loadHtml(value: String): Spanned {
        return HtmlCompat.fromHtml(value, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.release()
    }

    private fun shareDeepLink(deepLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Shared Post")
            putExtra(Intent.EXTRA_TEXT, deepLink)
        }
        startActivity(Intent.createChooser(shareIntent, "Share using"))
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}