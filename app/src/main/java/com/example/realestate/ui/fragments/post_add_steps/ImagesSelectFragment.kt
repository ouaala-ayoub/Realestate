package com.example.realestate.ui.fragments.post_add_steps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.R
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.databinding.FragmentImagesSelectBinding
import com.example.realestate.databinding.LoadingLayoutBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.adapters.ImagesAdapter
import com.example.realestate.ui.viewmodels.postaddmodels.ImagesSelectModel
import com.example.realestate.utils.*
import com.google.android.material.snackbar.Snackbar
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType

class ImagesSelectFragment : FragmentStep() {
    companion object {
        private const val TAG = "ImagesSelectFragment"
        private const val MAX_INPUT_SIZE = 8
        private const val NUM_OF_COLUMNS = 3
    }

    private lateinit var binding: FragmentImagesSelectBinding

    //    private lateinit var permissionRequestLauncher: ActivityResultLauncher<String>
//    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private val viewModel: ImagesSelectModel by lazy {
        ImagesSelectModel(MAX_INPUT_SIZE)
    }
    private val imagesAdapter: ImagesAdapter by lazy {
        ImagesAdapter(MAX_INPUT_SIZE, viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        permissionRequestLauncher = requestPermissionLauncher(
//            object : PermissionResult {
//                override fun onGranted() {
//                    imageResultLauncher.openGallery()
//                }
//
//                override fun onNonGranted() {
//                    val snackBar = makeSnackBar(
//                        requireView(),
//                        getString(R.string.permission),
//                        Snackbar.LENGTH_INDEFINITE
//                    )
//                    snackBar.setAction(R.string.OK) {
//                        snackBar.dismiss()
//                    }.show()
//                }
//            }
//        )
//        imageResultLauncher = startActivityResult(
//            object : SelectionResult {
//                override fun onResultOk(data: Intent) {
//                    val uris = data.getContentAsList()
//                    val mediaTypes = uris.map { uri -> requireContext().getType(uri) }
//
//                    imagesAdapter.addImages(uris, mediaTypes)
//                }
//
//                override fun onResultFailed() {
//                    Log.e(TAG, "imageResultLauncher onResultFailed")
//                }
//            }
//        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImagesSelectBinding.inflate(layoutInflater, container, false)

        binding.imagesRv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), NUM_OF_COLUMNS)
            adapter = imagesAdapter
        }

        binding.select.setOnClickListener {

            //TODO more readable code
            TedImagePicker
                .with(requireContext())
                .mediaType(MediaType.IMAGE_AND_VIDEO)
                .max(10, getString(R.string.max_string))
                .buttonBackground(R.drawable.yellow_drawable)
                .buttonTextColor(R.color.colorBackground)
                .startMultiImage { uriList ->
                    val mediaTypes = uriList.map { uri -> requireContext().getType(uri) }
                    imagesAdapter.addImages(uriList, mediaTypes)
                }

            //handle permissions and open the gallery
//            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                Manifest.permission.READ_MEDIA_IMAGES
//            } else {
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            }
//
//            requireActivity().handlePermission(object : PermissionResult {
//                override fun onGranted() {
//                    imageResultLauncher.openGallery()
//                }
//
//                override fun onNonGranted() {
//                    permissionRequestLauncher.requestStoragePermission()
//                }
//            }, listOf(permission))
        }

        viewModel.isFull.observe(viewLifecycleOwner) { isFull ->
            binding.select.isEnabled = !isFull
        }

        viewModel.isValid.observe(viewLifecycleOwner) { isValid ->
            (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isValid)
        }

//        viewModel.uploading.observe(viewLifecycleOwner) { uploading ->
//            Log.d(TAG, "uploading: $uploading")
//            if (uploading) {
//                showLoadingDialog()
//                updateProgress(0)
//            } else {
//                hideLoadingDialog()
//            }
//        }


        viewModel.progress.forEachIndexed { index, progressLiveData ->
            progressLiveData.observe(viewLifecycleOwner) { progress ->
                progress?.apply { imagesAdapter.updateProgress(this, index) }
            }
        }

        return binding.root
    }

    override fun onNextClicked(viewPager: ViewPager2) {
        viewPager.currentItem++
        (requireActivity() as AddPostActivity).post.media = imagesAdapter.getUploadedMedia()
    }

    override fun onBackClicked(viewPager: ViewPager2) {
        showLeaveDialog(requireActivity())
    }

    private fun showLeaveDialog(activity: Activity) {
        val dialog = makeDialog(
            activity,
            object : OnDialogClicked {
                override fun onPositiveButtonClicked() {
                    activity.finish()
                }

                override fun onNegativeButtonClicked() {
                    //TODO
                    //add the delete request
                    viewModel.cancelAllUploads()
                }
            },
            activity.getString(R.string.quit_post_title),
            activity.getString(R.string.quit_post_message)
        )
        dialog.apply {
            show()
            separateButtonsBy(10)
        }
    }

    override fun onResume() {
        super.onResume()
        val lastState = viewModel.isValid.value!!
        (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(lastState)
    }
}

