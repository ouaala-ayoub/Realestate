package com.example.realestate.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.data.models.Post
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

class HomeFragment : Fragment(), ActivityResultListener {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var searchParams: SearchParams
    private var selectedChipId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activity = (requireActivity() as MainActivity)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        searchParams = activity.params

        val retrofit = Retrofit.getInstance()
        viewModel = HomeViewModel(PostsRepository(retrofit), StaticDataRepository(retrofit)).also {
            it.getPosts(searchParams)
        }
        postsAdapter = PostsAdapter(
            object : OnPostClickListener {
                override fun onClick(postId: String) {
                    goToPostFragment(postId)
                }
            }
        )

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

        val activity = (requireActivity() as MainActivity)
        activity.setActivityResultListener(this)

        binding.apply {

            val items = listOf("Morocco", "UAE", "Armenia")

//            viewModel.countries.observe(viewLifecycleOwner) { countries ->
//                if (countries == null) return@observe
//                countryEditText.apply {
//                    val adapter = setUpAndHandleSearch(countries, object : OnSelected {
//                        override fun onSelected(selectedItem: String) {
//                            searchParams.location?.country = selectedItem
//                            viewModel.getPosts(searchParams)
//                        }
//                    })
//                    setOnItemClickListener { _, _, _, _ ->
//                      //remove the filters to get all choices next time
//                      adapter.filter.filter(null)
//
//                    //clear focus and hide keyboard after item selected
//                    hideKeyboard()
//                    clearFocus() }
//                }
//            }
            countryEditText.apply {
                val adapter = setUpAndHandleSearch(items, object : OnSelected {
                    override fun onSelected(selectedItem: String) {
                        searchParams.location?.country = selectedItem
                        viewModel.getPosts(searchParams)
                    }
                })
                setOnItemClickListener { _, _, _, _ ->
                    //remove the filters to get all choices next time
                    adapter.filter.filter(null)

                    //clear focus and hide keyboard after item selected
                    hideKeyboard()
                    clearFocus()
                }
            }


//            viewModel.categoriesList.observe(viewLifecycleOwner) { categories ->
//                if (categories == null) return@observe
//                categoriesChipGroup.apply {
//                    fillWith(categories)
//                    setOnCheckedStateChangeListener { group, checkedId ->
//                        val selectedChip: Chip? = group.findViewById(checkedId[0])
//                        val selectedCategory: String? = selectedChip?.text?.toString()
//
//                        Log.d(TAG, "selectedCategory: $selectedCategory")
//
//                        // Do something with the selected category
//
//                        searchParams.category = selectedCategory
//                        viewModel.getPosts(searchParams)
//                    }
//                }
//            }

            val categoriesList: List<String> = listOf(
                "dar",
                "villa",
                "ard",
                "blabla",
                "sheesh",
                "blabla",
                "blabla",
                "blabla",
                "test"
            )
            categoriesChipGroup.fillWith(categoriesList)

            categoriesChipGroup.setOnCheckedStateChangeListener { group, checkedId ->
                val selectedChip: Chip? = group.findViewById(checkedId[0])
                val selectedCategory: String? = selectedChip?.text?.toString()

                Log.d(TAG, "selectedCategory: $selectedCategory")

                // Do something with the selected category
            }

            selectedChipId = binding.rent.id
            binding.rent.isEnabled = false

            handleChips()

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.getPosts(SearchParams())
            }

            postRv.apply {
                adapter = postsAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            //disable swipe refresh if not on top
            vAppBar.addOnOffsetChangedListener { _, verticalOffset ->
                val isAppBarExpanded = verticalOffset == 0
                swipeRefreshLayout.isEnabled = isAppBarExpanded && !postRv.canScrollVertically(-1)
            }


            viewModel.postsList.observe(viewLifecycleOwner) { posts ->
                Log.d(TAG, "postsList: $posts")
                handleHomeButton()
                val test = listOf(
                    Post.emptyPost,
                    Post.emptyPost,
                    Post.emptyPost,
                    Post.emptyPost,
                    Post.emptyPost,
                    Post.emptyPost,
                    Post.emptyPost,
                )
                posts?.apply {
                    postsAdapter.setPostsList(test)
                    swipeRefreshLayout.isRefreshing = false
                }
            }
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
                            if ((layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() != 0) {
                                //scroll to the top if home re clicked
                                scrollToPosition(0)
                                binding.vAppBar.setExpanded(true, true)
                            }
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
            binding.rent.id -> {
                searchParams.type = Type.RENT.value
            }
            binding.sell.id -> {
                // Handle selection of Chip 2
                searchParams.type = Type.SELL.value
            }
            binding.buy.id -> {
                // Handle selection of Chip 3
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

    override fun onResultOk(searchParams: SearchParams) {
        requestData(searchParams)
    }

    private fun requestData(params: SearchParams) {
        Log.d(TAG, "requesting Data with params: $params")
        viewModel.getPosts(searchParams)
    }

    override fun onResultCancelled() {
        Log.d(TAG, "onResultCancelled")
    }

    private fun goToPostFragment(postId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToPostPageFragment2(postId)
        findNavController().navigate(action)
    }

}