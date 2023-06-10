package com.example.realestate.ui.fragments.post_add_steps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import com.example.realestate.R
import com.example.realestate.databinding.FragmentImagesSelectBinding
import com.example.realestate.utils.*
import com.google.android.material.snackbar.Snackbar

class ImagesSelectFragment : Fragment() {

    companion object {
        private const val TAG = "ImagesSelectFragment"
    }

    private lateinit var binding: FragmentImagesSelectBinding
    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionRequestLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageResultLauncher = requireActivity().startActivityResult(
            object : SelectionResult {
                override fun onResultOk(data: Intent) {
//                    TODO("Not yet implemented")
                    Log.i(TAG, "imageResultLauncher onResultOk")
                }

                override fun onResultFailed() {
//                    TODO("Not yet implemented")
                    Log.i(TAG, "imageResultLauncher onResultFailed")
                }
            }
        )
        permissionRequestLauncher = requireActivity().requestPermissionLauncher(
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

        return binding.root
    }

}