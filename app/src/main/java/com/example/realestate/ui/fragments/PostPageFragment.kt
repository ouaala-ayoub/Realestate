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
import androidx.navigation.fragment.navArgs
import com.example.realestate.R
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.FragmentPostPageBinding
import com.example.realestate.ui.viewmodels.PostPageModel
import com.example.realestate.utils.toast

class PostPageFragment : Fragment() {

    companion object {
        private const val TAG = "PostPageFragment"
    }

    private lateinit var binding: FragmentPostPageBinding
    private val args: PostPageFragmentArgs by navArgs()
    private val postId: String by lazy {
        args.postId
    }
    private val postPageModel: PostPageModel by lazy {
        val retrofit = Retrofit.getInstance()
        PostPageModel(PostsRepository(retrofit), UsersRepository(retrofit)).apply {
            getPost(postId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostPageBinding.inflate(inflater, container, false)
        Log.d(TAG, "postId: $postId")

        postPageModel.apply {
            seller.observe(viewLifecycleOwner) { seller ->
                binding.apply {
                    owner.text = seller?.name
                }
            }
            post.observe(viewLifecycleOwner) { post ->

                if (post != null) {
                    Log.d(TAG, "post = $post")
                    getUserById(post.ownerId)
                    binding.postName.text = post.title
                    binding.price.text = post.price.toString()
                } else {
                    //go back if error
                    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
                    findNavController().popBackStack()
                }
            }
            postLoading.observe(viewLifecycleOwner) { loading ->
                binding.postProgressBar.isVisible = loading
            }
        }

        return binding.root
    }
}