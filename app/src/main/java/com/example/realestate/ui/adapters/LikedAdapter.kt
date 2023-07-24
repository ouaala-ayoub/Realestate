package com.example.realestate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.DetailsType
import com.example.realestate.data.models.MediaType
import com.example.realestate.data.models.Post
import com.example.realestate.databinding.SingleLikedBinding
import com.example.realestate.utils.OnLikedClickListener
import com.example.realestate.utils.getMediaType
import com.example.realestate.utils.loadImage

class LikedAdapter(private val likedClickListener: OnLikedClickListener) :
    RecyclerView.Adapter<LikedAdapter.FavouritesHolder>() {
    private var likedList: List<Post> = listOf()

    inner class FavouritesHolder(private val binding: SingleLikedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val detailsShortAdapter = DetailsAdapter(DetailsType.SHORT)

        fun bind(position: Int) {

            val currentPost = likedList[position]
            val context = binding.root.context

            binding.apply {
                val firstImage =
                    currentPost.media.find { image ->
                        getMediaType(
                            image,
                            PostsAdapter.TAG
                        ) == MediaType.IMAGE
                    }

                if (firstImage != null) {
                    postImage.loadImage(firstImage)
                } else {
                    if (currentPost.media.isNotEmpty()) {
                        postImage.loadImage(currentPost.media[0])
                    }
                }

                postWhole.setOnClickListener {
                    likedClickListener.onClicked(currentPost.id!!)
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
                            area,
                        )
                }

                unlike.setOnClickListener {
                    likedClickListener.onDeleteClickedListener(currentPost.id!!)
                }

                val details = currentPost.details
                if (details != null) {
                    detailsShortRv.apply {
                        adapter = detailsShortAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        detailsShortAdapter.setDetails(details)
                    }
                }

            }
        }
    }

    fun setList(list: List<Post>) {
        likedList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesHolder {
        return FavouritesHolder(
            SingleLikedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = likedList.size

    override fun onBindViewHolder(holder: FavouritesHolder, position: Int) {
        holder.bind(position)
    }

}
