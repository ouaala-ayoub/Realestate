package com.example.realestate.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.Type
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.databinding.FragmentHomeBinding
import com.example.realestate.ui.activities.MainActivity
import com.example.realestate.ui.adapters.PostsAdapter
import com.example.realestate.ui.viewmodels.HomeViewModel
import com.example.realestate.utils.*
import com.google.android.material.chip.Chip

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


        Log.d(TAG, "searchParams from home fragment: $searchParams")

        viewModel = HomeViewModel(PostsRepository(Retrofit.getInstance())).also {
            it.getPosts(searchParams)
        }
        postsAdapter = PostsAdapter(
            object : OnPostClickListener {
                override fun onClick(postId: String) {
                    Log.d(TAG, "onClick: $postId")
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

            viewModel.postsList.observe(viewLifecycleOwner) { posts ->
                Log.d(TAG, "postsList: $posts")
                posts?.apply {
                    postsAdapter.setPostsList(posts)
                    swipeRefreshLayout.isRefreshing = false
                }
            }
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

}