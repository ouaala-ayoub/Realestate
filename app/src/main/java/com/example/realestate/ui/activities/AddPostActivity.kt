package com.example.realestate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.PostWithoutId
import com.example.realestate.databinding.ActivityAddPostBinding
import com.example.realestate.ui.adapters.FragmentsAdapter
import com.example.realestate.ui.fragments.post_add_steps.ImagesSelectFragment
import com.example.realestate.ui.fragments.post_add_steps.StepThreeFragment
import com.example.realestate.ui.fragments.post_add_steps.StepTwoFragment
import com.example.realestate.ui.viewmodels.postaddmodels.AddPostModel
import com.example.realestate.utils.showLeaveDialog
import com.google.android.material.tabs.TabLayoutMediator

class AddPostActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AddPostActivity"
    }

    val addPostModel: AddPostModel by lazy {
        AddPostModel()
    }
    private lateinit var binding: ActivityAddPostBinding
    private val fragmentsList: List<FragmentStep> by lazy {
        listOf(
            ImagesSelectFragment(),
            StepTwoFragment(),
            StepThreeFragment()
        )
    }
    private val fragmentsAdapter: FragmentsAdapter by lazy {
        FragmentsAdapter(this, fragmentsList)
    }
    val post: PostWithoutId by lazy {
        //don't forget ownerId
        PostWithoutId.emptyPost
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPostBinding.inflate(layoutInflater)
        val viewPager = binding.fragmentsViewPager

        //handle back button
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewPager.currentItem == 0) {
                        showLeaveDialog(this@AddPostActivity)
                    } else {
                        fragmentsAdapter.onBackClicked(viewPager)
                    }
                }
            }
        )


        //setting up the view pager with progressTabBar
        setViewPager()

        //handling next and back buttons
        binding.apply {
            back.setOnClickListener {
                fragmentsAdapter.onBackClicked(viewPager)
            }
            next.setOnClickListener {
                fragmentsAdapter.onNextClicked(viewPager)
            }
        }

        addPostModel.isValidData.observe(this) { isValidData ->
            Log.d(TAG, "isValidData: $isValidData")
            binding.next.isEnabled = isValidData
        }

        setContentView(binding.root)
    }


    private fun setViewPager() {
        binding.fragmentsViewPager.apply {
            offscreenPageLimit = 1
            adapter = fragmentsAdapter
            TabLayoutMediator(binding.progressTabBar, this, true) { tab, _ ->
                tab.view.isClickable = false
            }.attach()
            isUserInputEnabled = false
        }
    }
}