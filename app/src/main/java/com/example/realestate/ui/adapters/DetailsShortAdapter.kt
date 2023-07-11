package com.example.realestate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.databinding.SingleShortDetailBinding

class DetailsShortAdapter() :
    RecyclerView.Adapter<DetailsShortAdapter.DetailsShortHolder>() {
    inner class DetailsShortHolder(private val binding: SingleShortDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {

            }
        }
    }

    private var detailsMap: Map<String, String> = mapOf()

    fun setDetailsMap(map: Map<String, String>) {
        detailsMap = map
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsShortHolder {
        return DetailsShortHolder(
            SingleShortDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = detailsMap.size

    override fun onBindViewHolder(holder: DetailsShortHolder, position: Int) {
        holder.bind(position)
    }
}