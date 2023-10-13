package com.example.realestate.ui.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.ContextCompat
import com.example.realestate.R
import com.example.realestate.data.models.FragmentStep
import com.example.realestate.data.models.PostWithoutId
import com.example.realestate.databinding.ActivityAddPostBinding
import com.example.realestate.databinding.TabElementBinding
import com.example.realestate.ui.adapters.FragmentsAdapter
import com.example.realestate.ui.fragments.post_add_steps.DetailsStepFragment
import com.example.realestate.ui.fragments.post_add_steps.ImagesSelectFragment
import com.example.realestate.ui.fragments.post_add_steps.StepThreeFragment
import com.example.realestate.ui.fragments.post_add_steps.StepTwoFragment
import com.example.realestate.ui.viewmodels.postaddmodels.AddPostModel
import com.example.realestate.utils.showLeaveDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AddPostActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AddPostActivity"
    }

    val addPostModel: AddPostModel by lazy {
        AddPostModel()
    }
    private lateinit var binding: ActivityAddPostBinding
    private val fragmentsList: List<FragmentStep> =
        listOf(
            ImagesSelectFragment(),
            StepTwoFragment(),
            DetailsStepFragment(),
            StepThreeFragment()
        )

    private val fragmentsAdapter: FragmentsAdapter by lazy {
        FragmentsAdapter(this, fragmentsList)
    }
    val post: PostWithoutId by lazy {
        PostWithoutId.emptyPost
    }
    var selectedMedia: List<Uri> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dummyView = layoutInflater.inflate(R.layout.loading_screen, null, false)
        val asyncInflater = AsyncLayoutInflater(this)

        asyncInflater.inflate(R.layout.activity_add_post, null) { inflatedView, _, _ ->
            binding = ActivityAddPostBinding.bind(inflatedView)
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

            (dummyView as ViewGroup).apply {
                removeAllViews()
                addView(binding.root)
            }

            addPostModel.isValidData.observe(this) { isValidData ->
                binding.next.isEnabled = isValidData
            }
            addPostModel.isBackEnabled.observe(this) { isBackEnabled ->
                binding.back.isEnabled = isBackEnabled
            }

        }


        setContentView(dummyView)
    }


    private fun setViewPager() {
        //change color of selected tab
        val customTabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Change the text color of the selected tab
                val tabNumber = tab?.view?.findViewById<TextView>(R.id.tabNumber)
                tabNumber?.setTextColor(
                    ContextCompat.getColor(
                        this@AddPostActivity,
                        R.color.yellow
                    )
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Change the text color of the unselected tab
                val tabNumber = tab?.view?.findViewById<TextView>(R.id.tabNumber)
                tabNumber?.setTextColor(
                    ContextCompat.getColor(
                        this@AddPostActivity,
                        R.color.colorText
                    )
                )
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselection if needed
            }
        }
        binding.progressTabBar.addOnTabSelectedListener(customTabSelectedListener)
        binding.fragmentsViewPager.apply {
            offscreenPageLimit = 1
            adapter = fragmentsAdapter
            val tabMediator =
                TabLayoutMediator(binding.progressTabBar, this, true) { tab, position ->
                    val tabBinding = TabElementBinding.inflate(layoutInflater)
                    tabBinding.tabNumber.text =
                        (position + 1).toString() // Set the tab number dynamically
                    Log.d(
                        TAG,
                        "tabBinding is selected $position: ${tabBinding.tabNumber.isSelected}"
                    )
                    tab.apply {
                        customView = tabBinding.root
                        view.isClickable = false
                    }
                }
            tabMediator.attach()
            isUserInputEnabled = false
        }
    }
}