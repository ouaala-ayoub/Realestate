package com.example.realestate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.data.models.Post
import com.example.realestate.databinding.SingleFavouriteBinding
import com.example.realestate.utils.OnFavouriteClickListener

class FavouritesAdapter(private val favouriteClickListener: OnFavouriteClickListener) :
    RecyclerView.Adapter<FavouritesAdapter.FavouritesHolder>() {
    private var favouritesList: List<Post> = listOf()

    inner class FavouritesHolder(private val binding: SingleFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                
            }
        }
    }

    fun setList(list: List<Post>) {
        favouritesList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesHolder {
        return FavouritesHolder(
            SingleFavouriteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = favouritesList.size

    override fun onBindViewHolder(holder: FavouritesHolder, position: Int) {
        holder.bind(position)
    }

}
