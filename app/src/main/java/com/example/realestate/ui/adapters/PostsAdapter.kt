package com.example.realestate.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.Post
import com.example.realestate.databinding.SinglePostBinding
import com.example.realestate.utils.OnPostClickListener

class PostsAdapter(

    private val postClickListener: OnPostClickListener,

    ) : RecyclerView.Adapter<PostsAdapter.PostHolder>() {
    companion object {
        const val TAG = "PostAdapter"
    }

    private var postsList: MutableList<Post> = mutableListOf()
    fun setPostsList(list: List<Post>) {
        postsList = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class PostHolder(private val binding: SinglePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val currentPost = postsList[position]
            val context = binding.root.context
            binding.apply {
                postWhole.setOnClickListener {
                    Log.d(TAG, "clicked post at $position with id: ${currentPost.id} ")
                    postClickListener.onClick(currentPost.id!!)
                }
                postPrice.text =
                    context.getString(R.string.price, currentPost.price.toString())
                currentPost.location.apply {
                    postLocation.text =
                        context.getString(
                            R.string.location,
                            country,
                            city,
                            street,
                        )
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return PostHolder(
            SinglePostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = postsList.size

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(position)
    }
}