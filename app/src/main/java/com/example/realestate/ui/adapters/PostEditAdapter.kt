package com.example.realestate.ui.adapters

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
import com.example.realestate.data.models.CountriesData
import com.example.realestate.data.models.PostStatus
import com.example.realestate.data.models.PostWithOwnerId
import com.example.realestate.data.models.Type
import com.example.realestate.databinding.SinglePostEditBinding
import com.example.realestate.utils.*

class PostEditAdapter(
    private val postClickListener: OnPostClickListener,
) : RecyclerView.Adapter<PostEditAdapter.PostEditHolder>(), Filterable {

    companion object {
        const val TAG = "PostEditAdapter"
    }

    private var postsList: MutableList<PostWithOwnerId> = mutableListOf()
    private var filteredList: List<PostWithOwnerId> = postsList
    private var countriesData: CountriesData? = null

    fun setPostsList(list: List<PostWithOwnerId>) {
        postsList = list.toMutableList()
        filteredList = postsList
        notifyDataSetChanged()
    }

    fun setCountriesData(data: CountriesData?) {
        countriesData = data
        notifyDataSetChanged()
    }

    inner class PostEditHolder(private val binding: SinglePostEditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val detailsShortAdapter = DetailsShortAdapter()
        fun bind(position: Int) {
            val currentPost = filteredList[position]
            val context = binding.root.context
            val countryData =
                countriesData?.find { country -> country.name == currentPost.location.country }
            val price = formatNumberWithCommas(currentPost.price.toDouble())

            binding.apply {
                postWhole.apply {
                    setOnClickListener {
                        postClickListener.onClicked(currentPost)
                    }
                    // Handle long press to show the menu
                    setOnLongClickListener {
                        showPopUpMenu(moreIcon, position)
                        true
                    }
                }
                postInfo.apply {
                    defineField(
                        context.getString(
                            R.string.category_type,
                            currentPost.category.upperFirstLetter(),
                            currentPost.type.upperFirstLetter()
                        )
                    )
                }

                approveStatusTv.text = currentPost.status
                moreIcon.setOnClickListener {
                    showPopUpMenu(it, position)
                }
                if (currentPost.media.isNotEmpty()) {
                    postImage.loadImage(currentPost.media[0])
                }
                outOfOrder.isVisible = currentPost.status == PostStatus.OUT_OF_ORDER.value
                val priceToShow = when (currentPost.type) {
                    Type.RENT.value -> {
                        context.getString(
                            R.string.price_rent,
                            formatNumberWithCommas(currentPost.price.toDouble()),
                            currentPost.period
                        )
                    }
                    else -> {
                        context.getString(
                            R.string.price,
                            formatNumberWithCommas(currentPost.price.toDouble())
                        )
                    }
                }
                postPrice.defineField(
                    priceToShow
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
                        Log.d(PostsAdapter.TAG, "action_set_out_of_order")
                        if (currentPost.status == PostStatus.OUT_OF_ORDER.value || currentPost.status == PostStatus.APPROVED.value) {
                            Log.d(PostsAdapter.TAG, "status valid : yes")
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
                        Log.d(PostsAdapter.TAG, "action_delete")
                        postClickListener.onDeleteClicked(currentPost.id!!, position)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostEditHolder {
        return PostEditHolder(
            SinglePostEditBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = filteredList.size

    override fun onBindViewHolder(holder: PostEditHolder, position: Int) {
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