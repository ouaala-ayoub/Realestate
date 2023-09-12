package com.example.realestate.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.OnChanged
import com.example.realestate.data.models.PostWithOwnerId
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.databinding.FragmentUserPostsBinding
import com.example.realestate.ui.adapters.PostsAdapter
import com.example.realestate.ui.viewmodels.UserPostsViewModel
import com.example.realestate.utils.Countries
import com.example.realestate.utils.OnPostClickListener
import com.example.realestate.utils.doOnFail

class UserPostsFragment : Fragment() {

    companion object {
        private const val TAG = "UserPostsFragment"
    }

    private lateinit var binding: FragmentUserPostsBinding
    private val postsAdapter = PostsAdapter(object : OnPostClickListener {
        override fun onClicked(post: PostWithOwnerId): Nothing? {
            navigateToPostEdit(post)
            return super.onClicked(post)
        }

        override fun onDeleteClicked(postId: String, position: Int): Nothing? {
            viewModel.deletePost(postId, position)
            return super.onDeleteClicked(postId, position)
        }

        override fun setOutOfOrder(postId: String, position: Int, outOfOrder: Boolean): Nothing? {
            viewModel.setOutOfOrder(postId, position, outOfOrder)
            return super.setOutOfOrder(postId, position, outOfOrder)
        }

    }, isEdit = true)
    private var viewModel: UserPostsViewModel =
        UserPostsViewModel(PostsRepository(Retrofit.getInstance())).also {
            val userId = CurrentUser.get()?.id!!
            it.getUserPosts(userId)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserPostsBinding.inflate(inflater, container, false)
        binding.userPostsRv.apply {
            adapter = postsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        return binding.root
    }

    private fun <T> List<LiveData<T>>.observeLiveDataList(function: OnChanged<Int>) {
        forEachIndexed { index, liveData ->
            liveData.observe(viewLifecycleOwner) { message ->
                message?.apply {
                    function.onChange(index)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            deleted.observe(viewLifecycleOwner) {
                it.observeLiveDataList(object : OnChanged<Int> {
                    override fun onChange(data: Int?) {
                        Log.i(TAG, "deleted element at position : $data")
                        postsAdapter.deleteElementAt(data!!)
                    }
                })
            }
            outOfOrderSet.observe(viewLifecycleOwner) {
                it.observeLiveDataList(object : OnChanged<Int> {
                    override fun onChange(data: Int?) {
                        val position = data!!
                        Log.i(TAG, "outOfOrderSet element at position : $position")
                        postsAdapter.setOutOfOrder(position)
                    }
                })
            }
            Countries.observe(viewLifecycleOwner, object : OnChanged<CountriesData> {
                override fun onChange(data: CountriesData?) {
                    postsAdapter.setCountriesData(data)
                }
            })
            posts.observe(viewLifecycleOwner) { posts ->
                if (posts != null) {
                    setDeletedList(posts.size)
                    setOutOfOrderSet(posts.size)
                    setIsEmpty(posts.isEmpty())
                    postsAdapter.setPostsList(posts)
                } else {
                    requireActivity().doOnFail()
                }
            }
            loading.observe(viewLifecycleOwner) { loading ->
                binding.loadingProgressBar.isVisible = loading
            }
            isEmpty.observe(viewLifecycleOwner) { isEmpty ->
                binding.isEmpty.isVisible = isEmpty
            }
        }
    }

    fun navigateToPostEdit(post: PostWithOwnerId) {
        val action =
            UserPostsFragmentDirections.actionUserPostsFragmentToSinglePostEditFragment(post)
        findNavController().navigate(action)
    }

}