package com.example.realestate.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.OnChanged
import com.example.realestate.data.models.PostWithOwnerId
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.databinding.FragmentUserPostsBinding
import com.example.realestate.ui.adapters.PostsAdapter
import com.example.realestate.ui.viewmodels.UserPostsViewModel
import com.example.realestate.utils.*

class UserPostsFragment : Fragment() {

    companion object {
        private const val TAG = "UserPostsFragment"
    }

    private var _binding: FragmentUserPostsBinding? = null
    private val binding get() = _binding!!
    private val postsAdapter = PostsAdapter(object : OnPostClickListener {
        override fun onClicked(post: PostWithOwnerId): Nothing? {
            navigateToPostEdit(post)
            return super.onClicked(post)
        }

        override fun onDeleteClicked(postId: String, position: Int): Nothing? {
            val dialog = makeDialog(
                requireContext(),
                object : OnDialogClicked {
                    override fun onPositiveButtonClicked() {
                        viewModel.deletePost(postId, position)
                    }

                    override fun onNegativeButtonClicked() {}

                },
                getString(R.string.delete_title),
                getString(R.string.delete_message),
                negativeText = getString(R.string.No),
                positiveText = getString(R.string.Yes)
            )

            dialog.apply {
                show()
                separateButtonsBy(15)
            }


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
        _binding = FragmentUserPostsBinding.inflate(inflater, container, false)
        binding.apply {
            userPostsRv.apply {
                adapter = postsAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            //TODO add search logic
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Handle query submission (if needed)
                    filter(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filter(newText)
                    return true
                }

                fun filter(query: String?) {
                    postsAdapter.filter.filter(query.orEmpty())
                }
            })

            swipeRefresh.setOnRefreshListener {
                val userId = CurrentUser.get()?.id!!
                viewModel.getUserPosts(userId)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            deleted.observe(viewLifecycleOwner) {
                it.observeLiveDataList(object : OnChanged<Int> {
                    override fun onChange(data: Int?) {
                        Log.i(TAG, "deleted element at position : $data")
//                        setIsEmpty(posts.value.isNullOrEmpty())
                        deleteElementAt(data!!)
                    }
                })
            }
            outOfOrderSet.observe(viewLifecycleOwner) {
                it.observeLiveDataList(object : OnChanged<Int> {
                    override fun onChange(data: Int?) {
                        val position = data!!
                        Log.i(TAG, "outOfOrderSet element at position : $position")
                        setOutOfOrder(position)
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
                    binding.swipeRefresh.isRefreshing = false
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun navigateToPostEdit(post: PostWithOwnerId) {
        val action =
            UserPostsFragmentDirections.actionUserPostsFragmentToSinglePostEditFragment(post)
        findNavController().navigate(action)
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

}