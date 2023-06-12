package com.example.realestate.ui.fragments.post_add_steps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.realestate.R
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.Post
import com.example.realestate.databinding.FragmentImagesSelectBinding
import com.example.realestate.ui.adapters.ImagesAdapter
import com.example.realestate.ui.viewmodels.ImagesSelectModel
import com.example.realestate.utils.*
import com.google.android.material.snackbar.Snackbar

class ImagesSelectFragment : FragmentStep() {

    companion object {
        private const val TAG = "ImagesSelectFragment"
        private const val MAX_INPUT_SIZE = 8
        private const val NUM_OF_COLUMNS = 3
    }

    private lateinit var binding: FragmentImagesSelectBinding
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<String>
    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: ImagesSelectModel
    private lateinit var imagesAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ImagesSelectModel()
        imagesAdapter = ImagesAdapter(MAX_INPUT_SIZE, viewModel)
        imageResultLauncher = startActivityResult(
            object : SelectionResult {
                override fun onResultOk(data: Intent) {
                    imagesAdapter.addImages(data.getContentAsList())
                }

                override fun onResultFailed() {
//                    TODO("Not yet implemented")
                    Log.i(TAG, "imageResultLauncher onResultFailed")
                }
            }
        )
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
            //handle permissions and open the gallery
            requireActivity().handlePermission(object : PermissionResult {
                override fun onGranted() {
                    imageResultLauncher.openGallery()
                }

                override fun onNonGranted() {
                    permissionRequestLauncher.requestStoragePermission()
                }
            })
        }

        viewModel.isFull.observe(viewLifecycleOwner) { isFull ->
            binding.select.isEnabled = !isFull
        }

        return binding.root
    }

    override fun onNextClicked(viewPager: ViewPager2, post: Post) {
        viewPager.currentItem++

//        add logic
//        post.media = mediaList.value
    }

    override fun onBackClicked(viewPager: ViewPager2) {
        showLeaveDialog()
    }

    private fun showLeaveDialog() {
        val dialog = makeDialog(
            requireContext(),
            object : OnDialogClicked {
                override fun onPositiveButtonClicked() {
                    requireActivity().finish()
                }

                override fun onNegativeButtonClicked() {}
            },
            getString(R.string.quit_post_title),
            getString(R.string.quit_post_message)
        )
        dialog.apply {
            show()
            separateButtonsBy(10)
        }
    }

}