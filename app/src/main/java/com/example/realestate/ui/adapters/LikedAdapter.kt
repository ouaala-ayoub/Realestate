package com.example.realestate.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.databinding.SingleLikedBinding
import com.example.realestate.utils.*

class LikedAdapter(private val likedClickListener: OnLikedClickListener) :
    RecyclerView.Adapter<LikedAdapter.FavouritesHolder>() {
    companion object {
        private const val TAG = "LikedAdapter"
    }

    private var likedList: List<PostWithOwnerId> = listOf()
    private var countriesData: CountriesData? = null

    inner class FavouritesHolder(private val binding: SingleLikedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val detailsShortAdapter = DetailsShortAdapter()

        fun bind(position: Int) {

            val currentPost = likedList[position]
            val context = binding.root.context
            val countryData =
                countriesData?.find { country -> country.name == currentPost.location.country }

            binding.apply {
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
                    likedClickListener.onClicked(currentPost.id!!)
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

                unlike.setOnClickListener {
                    likedClickListener.onDeleteClickedListener(currentPost.id!!)
                }

                val features = currentPost.features
                if (features != null) {
                    detailsShortRv.apply {
                        adapter = detailsShortAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        detailsShortAdapter.setFeatures(features)
                    }
                }

            }
        }
    }

    fun setList(list: List<PostWithOwnerId>) {
        likedList = list
        notifyDataSetChanged()
    }

    fun setCountriesData(data: CountriesData?) {
        countriesData = data
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
