package com.example.realestate.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.databinding.SinglePostBinding
import com.example.realestate.utils.*

class PostsAdapter(

    private val postClickListener: OnPostClickListener,
    private val addToFavClicked: OnAddToFavClicked,
    private var postsList: MutableList<PostWithOwnerId> = mutableListOf()

) : RecyclerView.Adapter<PostsAdapter.PostHolder>() {
    companion object {
        const val TAG = "PostAdapter"
    }

    //    private var postsList: MutableList<Post> = mutableListOf()
    private var favourites: MutableList<String> = mutableListOf()
    private var countriesData: CountriesData? = null
    fun setPostsList(list: List<PostWithOwnerId>) {
        postsList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun isListEmpty() = postsList.isEmpty()

    fun setLiked(list: List<String>) {
        favourites = list.toMutableList()
        notifyDataSetChanged()
    }

    fun setCountriesData(data: CountriesData?) {
        countriesData = data
        notifyDataSetChanged()
    }

    inner class PostHolder(private val binding: SinglePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val detailsShortAdapter = DetailsAdapter(DetailsType.SHORT)
        fun bind(position: Int) {
            val currentPost = postsList[position]
            val context = binding.root.context
            val isChecked = currentPost.id in favourites
            val countryData =
                countriesData?.find { country -> country.name == currentPost.location.country }

            binding.apply {

                //load the first image if nothing found load first media
                val firstImage =
                    currentPost.media.find { image ->
                        getMediaType(
                            image,
                            TAG
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
                    postClickListener.onClick(currentPost.id!!)
                }

                postInfo.apply {
                    defineField(
                        context.getString(
                            R.string.category_type,
                            currentPost.category.upperFirstLetter(),
                            currentPost.type.upperFirstLetter()
                        ), context
                    )
                }
                postPrice.text =
                    context.getString(
                        R.string.price,
                        formatNumberWithCommas(currentPost.price)
                    )
                currentPost.location.apply {
                    countryFlag.loadImage(countryData?.image)
                    postLocation.text =
                        context.getString(
                            R.string.location,
                            country,
                            city,
                            area,
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
                if (details != null) {
                    detailsShortRv.apply {
                        adapter = detailsShortAdapter
                        layoutManager =
                            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        detailsShortAdapter.setDetails(details)
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