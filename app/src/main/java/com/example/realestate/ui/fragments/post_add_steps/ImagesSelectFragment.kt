package com.example.realestate.ui.fragments.post_add_steps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.R
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.databinding.FragmentImagesSelectBinding
import com.example.realestate.ui.activities.AddPostActivity
import com.example.realestate.ui.adapters.ImagesAdapter
import com.example.realestate.ui.viewmodels.postaddmodels.ImagesSelectModel
import com.example.realestate.utils.*
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit

class ImagesSelectFragment : FragmentStep() {
    companion object {
        private const val TAG = "ImagesSelectFragment"
        private const val MAX_INPUT_SIZE = 8
        private const val NUM_OF_COLUMNS = 3
    }

    private lateinit var binding: FragmentImagesSelectBinding
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<String>
    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private val viewModel: ImagesSelectModel by lazy {
        ImagesSelectModel(MAX_INPUT_SIZE)
    }
    private val imagesAdapter: ImagesAdapter by lazy {
        ImagesAdapter(MAX_INPUT_SIZE, viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionRequestLauncher = requestPermissionLauncher(
            object : PermissionResult {
                override fun onGranted() {
                    imageResultLauncher.openGallery()
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
            }
        )
        imageResultLauncher = startActivityResult(
            object : SelectionResult {
                override fun onResultOk(data: Intent) {
                    val uris = data.getContentAsList()
                    val urisToUpload = uris.filter { uri -> isValidMedia(uri) }

                    //in case not all uri got accepted
                    if (urisToUpload.size != uris.size) {
                        val snackBar = makeSnackBar(
                            binding.root,
                            getString(R.string.failed_media_selection),
                            Snackbar.LENGTH_INDEFINITE
                        )
                        snackBar.setAction(getString(R.string.OK)) {
                            snackBar.dismiss()
                        }.show()
                    }


                    imagesAdapter.addImages(urisToUpload, requireContext())
                }

                override fun onResultFailed() {
                    Log.e(TAG, "imageResultLauncher onResultFailed")
                }
            }
        )
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

//            handle permissions and open the gallery
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            requireActivity().handlePermission(object : PermissionResult {
                override fun onGranted() {
                    imageResultLauncher.openGallery()
                }

                override fun onNonGranted() {
                    permissionRequestLauncher.requestStoragePermission()
                }
            }, listOf(permission))
        }

        viewModel.isFull.observe(viewLifecycleOwner) { isFull ->
            binding.select.isEnabled = !isFull
        }

        viewModel.isValid.observe(viewLifecycleOwner) { isValid ->
            (requireActivity() as AddPostActivity).addPostModel.updateIsValidData(isValid)
        }


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

    // Function to validate selected media
    @SuppressLint("Range")
    private fun isValidMedia(uri: Uri): Boolean {
        val projection = arrayOf(
            MediaStore.MediaColumns.SIZE,
            MediaStore.Video.VideoColumns.DURATION
        )

        requireActivity().contentResolver.query(uri, projection, null, null, null)?.use {
            if (it.moveToFirst()) {
                val size = it.getLong(it.getColumnIndex(MediaStore.MediaColumns.SIZE))
                val duration = it.getLong(it.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
                val maxFileSize = 100 * 1024 * 1024
                val maxVideoDuration = TimeUnit.SECONDS.toMillis(59)

                if (size >= 0 && duration >= 0) {
                    // Check size and duration criteria
                    if (size <= maxFileSize && duration <= maxVideoDuration) {
                        return true // Media is valid
                    }
                }
            }
        }

        return false // Media is invalid
    }
}

