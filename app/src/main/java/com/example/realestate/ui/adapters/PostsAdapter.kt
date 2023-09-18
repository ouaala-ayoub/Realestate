package com.example.realestate.ui.adapters

import android.content.pm.ApplicationInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.databinding.SinglePostBinding
import com.example.realestate.utils.*

class PostsAdapter(


    private val postClickListener: OnPostClickListener,
    private val addToFavClicked: OnAddToFavClicked? = null,

    private val isEdit: Boolean = false,

    ) : RecyclerView.Adapter<PostsAdapter.PostHolder>(), Filterable {
    companion object {
        const val TAG = "PostAdapter"
    }

    private var postsList: MutableList<PostWithOwnerId> = mutableListOf()
    private var filteredList: List<PostWithOwnerId> = postsList

    //    private var postsList: MutableList<PostWithOwnerId> = mutableListOf()
//    private var postsListFull: MutableList<PostWithOwnerId> = postsList
    private var favourites: MutableList<String> = mutableListOf()
    private var countriesData: CountriesData? = null
    fun setPostsList(list: List<PostWithOwnerId>) {
        postsList = list.toMutableList()
        filteredList = postsList
        Log.d(TAG, "postsList: $postsList")
        Log.d(TAG, "filteredList: $filteredList")
        notifyDataSetChanged()
    }

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

        private val detailsShortAdapter = DetailsShortAdapter()
        fun bind(position: Int) {
            val currentPost = filteredList[position]
            val context = binding.root.context
            val isChecked = currentPost.id in favourites
            val countryData =
                countriesData?.find { country -> country.name == currentPost.location.country }

            binding.apply {


                postWhole.setOnClickListener {
                    if (isEdit) {
                        postClickListener.onClicked(currentPost)
                    } else {
                        postClickListener.onClick(currentPost.id!!)
                    }
                }

                if (isEdit) {
                    approveStatusLl.apply {
                        isVisible = true
                        approveStatusTv.text = currentPost.status
                        // Handle long press to show the menu
                        postWhole.setOnLongClickListener {
                            showPopUpMenu(moreIcon, position)
                            true
                        }
                        moreIcon.setOnClickListener {
                            showPopUpMenu(it, position)
                        }
                    }
                }


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

                outOfOrder.isVisible = currentPost.status == PostStatus.OUT_OF_ORDER.value

                postInfo.apply {
                    defineField(
                        context.getString(
                            R.string.category_type,
                            currentPost.category.upperFirstLetter(),
                            currentPost.type.upperFirstLetter()
                        )
                    )
                }
                when (currentPost.type) {
                    Type.RENT.value -> {
                        val toShow = context.getString(
                            R.string.price_rent,
                            formatNumberWithCommas(currentPost.price.toDouble()),
                            currentPost.period
                        )
                        postPrice.defineField(
                            toShow
                        )
                    }
                    else -> {
                        val toShow = context.getString(
                            R.string.price,
                            formatNumberWithCommas(currentPost.price.toDouble())
                        )
                        postPrice.defineField(
                            toShow
                        )
                    }
                }
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

                if (!isEdit) {
                    addToFav.isChecked = isChecked
                    addToFav.setOnClickListener {

                        val userConnected = CurrentUser.isConnected()
//                    val userId = CurrentUser.prefs.get()
                        val postId = currentPost.id!!

                        if (userConnected) {
                            if (isChecked) {
                                addToFavClicked?.onChecked(postId)
                            } else {
                                addToFavClicked?.onUnChecked(postId)
                            }

                        } else {
                            addToFav.isEnabled = false
                        }
                    }
                } else {
                    addToFav.visibility = View.INVISIBLE
                }


                val features = currentPost.features
                if (features != null) {
                    detailsShortRv.apply {
                        adapter = detailsShortAdapter
                        layoutManager =
                            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        detailsShortAdapter.setFeatures(features)
                    }
                }
            }
        }

        private fun showPopUpMenu(anchor: View, position: Int) {
            val currentPost = filteredList[position]
            val popupMenu = PopupMenu(anchor.context, anchor)
            popupMenu.menuInflater.inflate(R.menu.post_long_click_menu, popupMenu.menu)

            // Set click listeners for menu items
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_set_out_of_order -> {
                        // Handle edit action
                        // You can implement your edit logic here
                        Log.d(TAG, "action_set_out_of_order")
                        if (currentPost.status == PostStatus.OUT_OF_ORDER.value || currentPost.status == PostStatus.APPROVED.value) {
                            Log.d(TAG, "status valid : yes")
                            postClickListener.setOutOfOrder(
                                currentPost.id!!,
                                position,
                                currentPost.status == PostStatus.OUT_OF_ORDER.value
                            )
                        }


                        true
                    }
                    R.id.action_delete -> {
                        // Handle delete action
                        // You can implement your delete logic here
                        Log.d(TAG, "action_delete")
                        postClickListener.onDeleteClicked(currentPost.id!!, position)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
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

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(position)
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter = object : Filter() {
        override fun performFiltering(query: CharSequence?): FilterResults {

            filteredList = if (query.isNullOrEmpty()) {
                postsList
            } else {
                postsList.filter { post ->
                    //filtering by description
                    post.description.contains(query, ignoreCase = true)
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(query: CharSequence?, result: FilterResults?) {
            notifyDataSetChanged()
        }

    }
}