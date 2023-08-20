package com.example.realestate.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentSavedBinding
import com.example.realestate.ui.adapters.LikedAdapter
import com.example.realestate.ui.viewmodels.LikedViewModel
import com.example.realestate.utils.OnLikedClickListener
import com.example.realestate.utils.toast

class LikedFragment : Fragment() {

    companion object {
        private const val TAG = "LikedFragment"
    }

    private lateinit var binding: FragmentSavedBinding
    private val viewModel: LikedViewModel by lazy {
        LikedViewModel(UsersRepository(Retrofit.getInstance()))
    }
    private val likedAdapter: LikedAdapter by lazy {
        LikedAdapter(
            object : OnLikedClickListener {
                override fun onClicked(postId: String) {
                    openPostFragment(postId)
                }

                override fun onDeleteClickedListener(postId: String) {
                    val userId = CurrentUser.prefs.get()
                    userId?.apply {
                        viewModel.apply {
                            deleteFromFavourites(postId)

                        }
                    }
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "created liked fragment")
        super.onCreate(savedInstanceState)
        //get user saved posts
        val userId = CurrentUser.prefs.get()
        userId?.apply {
            viewModel.getLikedPosts(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(inflater, container, false)

        binding.savedRv.apply {
            adapter = likedAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            unliked.observe(viewLifecycleOwner) { message ->
                if (message == null)
                    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
                else {
                    val userId = CurrentUser.prefs.get()
                    userId?.apply {
                        viewModel.getLikedPosts(this)
                    }
                }
            }
            postsMessage.observe(viewLifecycleOwner) { postsMessage ->
                if (postsMessage.isEmpty()) {
                    binding.postsStateMessage.visibility = View.GONE
                } else {
                    binding.postsStateMessage.visibility = View.VISIBLE
                    binding.postsStateMessage.text = postsMessage
                }
            }
            savedList.observe(viewLifecycleOwner) { savedList ->
                savedList?.apply {
                    likedAdapter.setList(this)
                }
            }
            loading.observe(viewLifecycleOwner) { loading ->
                binding.savedLoading.isVisible = loading
            }
        }
    }

    private fun openPostFragment(postId: String) {
        val action = LikedFragmentDirections.actionSavedFragmentToPostNav(postId)
        findNavController().navigate(action)
    }

}