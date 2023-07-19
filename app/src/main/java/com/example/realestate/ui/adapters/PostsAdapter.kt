package com.example.realestate.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.DetailsType
import com.example.realestate.data.models.MediaType
import com.example.realestate.data.models.Post
import com.example.realestate.databinding.SinglePostBinding
import com.example.realestate.utils.OnAddToFavClicked
import com.example.realestate.utils.OnPostClickListener
import com.example.realestate.utils.getMediaType
import com.example.realestate.utils.loadImage

class PostsAdapter(

    private val postClickListener: OnPostClickListener,
    private val addToFavClicked: OnAddToFavClicked,
    private var postsList: MutableList<Post> = mutableListOf()

) : RecyclerView.Adapter<PostsAdapter.PostHolder>() {
    companion object {
        const val TAG = "PostAdapter"
    }

//    private var postsList: MutableList<Post> = mutableListOf()
    private var favourites: MutableList<String> = mutableListOf()
    fun setPostsList(list: List<Post>) {
        postsList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun setLiked(list: List<String>) {
        favourites = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class PostHolder(private val binding: SinglePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val detailsShortAdapter = DetailsAdapter(DetailsType.SHORT)

        fun bind(position: Int) {
            val currentPost = postsList[position]
            val context = binding.root.context
            val isChecked = currentPost.id in favourites

            binding.apply {

                //load the first image if nothing found load first media
                val firstImage =
                    currentPost.media.find { image -> getMediaType(image, TAG) == MediaType.IMAGE }

                if (firstImage != null) {
                    postImage.loadImage(firstImage)
                } else {
                    if (currentPost.media.isNotEmpty()) {
                        postImage.loadImage(currentPost.media[0])
                    }
                }

                postWhole.setOnClickListener {
                    Log.d(TAG, "clicked post at $position with id: ${currentPost.id} ")
                    postClickListener.onClick(currentPost.id!!)
                }
                postPrice.text =
                    context.getString(
                        R.string.price,
                        currentPost.price.toString()
                    )
                currentPost.location.apply {
                    postLocation.text =
                        context.getString(
                            R.string.location,
                            country,
                            city,
                            street,
                        )
                }

                //favourites button

                addToFav.isChecked = isChecked
                addToFav.setOnClickListener {
                    val userConnected = CurrentUser.isConnected()
//                    val userId = CurrentUser.prefs.get()
                    val postId = currentPost.id!!

                    if (userConnected) {
                        if (isChecked) {
                            addToFavClicked.onChecked(postId)
                        } else {
                            addToFavClicked.onUnChecked(postId)
                        }

                    } else {
                        addToFav.isEnabled = false
//                        addToFavClicked.onDisconnected()
                    }
                }

                val details = currentPost.details
                if (!details.isNullOrEmpty()) {
                    detailsShortRv.apply {
                        adapter = detailsShortAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        detailsShortAdapter.setDetailsMap(details)
                    }
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