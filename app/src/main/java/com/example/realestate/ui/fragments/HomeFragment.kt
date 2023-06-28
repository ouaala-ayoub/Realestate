package com.example.realestate.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.Type
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.FragmentHomeBinding
import com.example.realestate.ui.activities.MainActivity
import com.example.realestate.ui.adapters.PostsAdapter
import com.example.realestate.ui.viewmodels.HomeViewModel
import com.example.realestate.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private var firstTime: Boolean = true
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var searchParams: SearchParams
    private val retrofit = Retrofit.getInstance()
    private var selectedChipId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = (requireActivity() as MainActivity)
        viewModel = HomeViewModel(PostsRepository(retrofit), StaticDataRepository(retrofit)).also {
            it.getCategories()
        }
        searchParams = activity.params
        postsAdapter = PostsAdapter(
            object : OnPostClickListener {
                override fun onClick(postId: String) {
                    goToPostFragment(postId)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.postRv.apply {
            adapter = postsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //to add a confirmation dialog
                    val dialog = makeDialog(
                        requireContext(),
                        object : OnDialogClicked {
                            override fun onPositiveButtonClicked() {
                                requireActivity().finish()
                            }

                            override fun onNegativeButtonClicked() {
                                // nothing
                            }
                        },
                        title = getString(R.string.app_name),
                        message = getString(R.string.Leave),
                        negativeText = getString(R.string.No),
                        positiveText = getString(R.string.Yes)
                    )
                    dialog.apply {
                        show()
                        separateButtonsBy(10)
                    }

                }

            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val activity = (requireActivity() as MainActivity)
//        activity.setActivityResultListener(this)

        binding.apply {

            //handle loading
            viewModel.isProgressBarTurning.observe(viewLifecycleOwner) { loading ->
                progressBar.isVisible = loading
            }

            //get the current country and send the request to get the posts of this country
            countryPicker.setOnCountryChangeListener {
                // your code to handle selected country
                countryPicker.selectedCountryName?.apply {
                    val name = this
                    searchParams.location?.country = name
                    viewModel.getPosts(searchParams)
                }
            }

            viewModel.categoriesList.observe(viewLifecycleOwner) { categories ->
                if (categories == null) return@observe
                categoriesChipGroup.apply {
                    fillWith(categories)
                    setOnCheckedStateChangeListener { group, checkedId ->
                        if (checkedId.isEmpty()) {
                            searchParams.category = null
                            viewModel.getPosts(searchParams)
                        } else {
                            val selectedChip: Chip? = group.findViewById(checkedId[0])
                            val selectedCategory: String? = selectedChip?.text?.toString()

                            Log.d(TAG, "selectedCategory: $selectedCategory")

                            searchParams.category = selectedCategory
                            viewModel.getPosts(searchParams)

                        }
                    }
                }
            }

            //default selected chip
            selectedChipId = binding.all.id

            handleChips()
            handleSearch()

            //handle swipe gesture
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.getPosts(searchParams)
                if (viewModel.categoriesList.value.isNullOrEmpty()) {
                    viewModel.getCategories()
                }
            }


            //disable swipe refresh if not on top
            vAppBar.addOnOffsetChangedListener { _, verticalOffset ->
                val isAppBarExpanded = verticalOffset == 0
                swipeRefreshLayout.isEnabled =
                    isAppBarExpanded && !postRv.canScrollVertically(-1)
            }


            viewModel.postsList.observe(viewLifecycleOwner) { posts ->
                Log.d(TAG, "postsList: $posts")


                handleHomeButton()

                //prevent scrolling bugs
                if (posts.isNullOrEmpty()) {
                    collapsingBar.disableScroll()
                } else {
                    collapsingBar.enableScroll()
                }


                posts?.apply {
                    if (firstTime) {
                        postsAdapter.setPostsList(posts)
                        firstTime = false
                    } else {
                        val recyclerViewState = binding.postRv.layoutManager?.onSaveInstanceState()
                        postsAdapter.setPostsList(posts)
                        binding.postRv.layoutManager?.onRestoreInstanceState(recyclerViewState)
                        binding.vAppBar.setExpanded(false, false)
                    }

                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

    }

    private fun handleSearch() {
        binding.searchView.apply {

            //search by user input
            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.apply {
                        searchByQuery(query)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.apply {
                        searchByQuery(newText)
                    }
                    return false
                }

                fun searchByQuery(query: String?) {
                    if (query.isNullOrBlank()) return
                    else {
                        searchParams.title = query
                        viewModel.getPosts(searchParams)
                    }
                }
            })
        }

    }


    private fun AutoCompleteTextView.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun handleHomeButton() {
        (requireActivity() as MainActivity).bottomNavView.setOnItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeNav -> {
                    binding.postRv.apply {
                        if (layoutManager is LinearLayoutManager) {
                            //scroll to the top if home re clicked

                            scrollToPosition(0)
                            binding.vAppBar.setExpanded(true, true)

                        }
                    }
                }
            }
        }
    }


    private fun ChipGroup.fillWith(categories: List<String>) {
        for (category in categories) {
            val chip = Chip(context)
            chip.apply {
                text = category
                isCheckable = true
                isCheckedIconVisible = false
//                setChipDrawable(chipDrawable)
            }
            addView(chip)
        }
    }

    private fun onChipClicked(view: View) {
        val chipId = view.id

        // Unselect previously selected chip if any
        if (selectedChipId != -1) {
            val previousChip = requireActivity().findViewById<Chip>(selectedChipId)
            previousChip.isChecked = false
            previousChip.isEnabled = true
        }

        // Update selected chip
        val chip = view as Chip
        chip.isChecked = true
        chip.isEnabled = false
        selectedChipId = chipId

        // Perform actions based on the selected chip
        when (chipId) {
            binding.all.id -> {
                searchParams.type = null
            }
            binding.rent.id -> {
                searchParams.type = Type.RENT.value
            }
            binding.buy.id -> {
                searchParams.type = Type.BUY.value
            }
        }
        viewModel.getPosts(searchParams)
    }


    private fun handleChips() {
        for (chip in binding.chips.children) {
            chip.setOnClickListener {
                if (selectedChipId == chip.id) {
                    // Chip is already selected, do nothing
                    Log.d(TAG, "onChipClicked already selected")
                    return@setOnClickListener
                }
                onChipClicked(it)
            }
        }
    }

//    override fun onResultOk(searchParams: SearchParams) {
//        requestData(searchParams)
//    }

//    private fun requestData(params: SearchParams) {
//        Log.d(TAG, "requesting Data with params: $params")
//        viewModel.getPosts(searchParams)
//    }

//    override fun onResultCancelled() {
//        Log.d(TAG, "onResultCancelled")
//    }

    private fun goToPostFragment(postId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToPostPageFragment2(postId)
        findNavController().navigate(action)
    }


}