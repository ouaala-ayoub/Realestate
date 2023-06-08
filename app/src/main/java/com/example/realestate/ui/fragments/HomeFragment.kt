package com.example.realestate.ui.fragments

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.data.models.Location
import com.example.realestate.data.models.Post
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.databinding.FragmentHomeBinding
import com.example.realestate.ui.adapters.PostsAdapter
import com.example.realestate.ui.viewmodels.HomeViewModel
import com.example.realestate.utils.OnPostClickListener
import com.google.android.material.chip.Chip

class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var postsAdapter: PostsAdapter
    private var selectedChipId: Int = 0
    private var searchParams = SearchParams()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        postsAdapter = PostsAdapter(
            object : OnPostClickListener {
                override fun onClick(postId: String) {
                    Log.d(TAG, "onClick: $postId")
                }
            }
        )
        viewModel = HomeViewModel(PostsRepository(Retrofit.getInstance())).also {
            it.getPosts(searchParams)
        }

        binding.apply {

            selectedChipId = binding.rent.id
            binding.rent.isEnabled = false
            handleChips()

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.getPosts(searchParams)
            }

            postRv.apply {
                val test = Post(
                    title = "Dar kbiiira",
                    category = "category",
                    currency = "USD",
                    location = Location(
                        "Maroc", city = "Salé", street = "Hay karima",
                    ),
                    media = listOf(),
                    ownerId = "ownerId",
                    price = 120000,
                    type = "Type"
                )
                val list = listOf<Post>(
                    test,
                    test,
                    test,
                    test,
                    test,
                    test,
                )
                adapter = postsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                postsAdapter.setPostsList(list)
            }

            viewModel.postsList.observe(viewLifecycleOwner) { posts ->
                posts?.apply {
                    postsAdapter.setPostsList(posts)
                }
            }
        }

        return binding.root
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
                // Handle selection of Chip 1
                // TODO: Add your code here
            }
            binding.sell.id -> {
                // Handle selection of Chip 2
                // TODO: Add your code here
            }
            binding.buy.id -> {
                // Handle selection of Chip 3
                // TODO: Add your code here
            }
        }
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

}