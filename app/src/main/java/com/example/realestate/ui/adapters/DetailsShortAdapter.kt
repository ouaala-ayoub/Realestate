package com.example.realestate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.databinding.SingleShortDetailBinding
import com.example.realestate.utils.getDetailIcon

class DetailsShortAdapter : RecyclerView.Adapter<DetailsShortAdapter.DetailsShortHolder>() {

    private var featuresList: List<String> = listOf()
    fun setFeatures(list: List<String>) {
        featuresList = list
        notifyDataSetChanged()
    }

    inner class DetailsShortHolder(val binding: SingleShortDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context
        fun bind(position: Int) {
            val currentList = featuresList[position]
            binding.apply {
                detailBody.setCompoundDrawablesWithIntrinsicBounds(
                    getDetailIcon(currentList, context),
                    null,
                    null,
                    null
                )
            }
        }
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

    override fun getItemCount() = featuresList.size

    override fun onBindViewHolder(holder: DetailsShortHolder, position: Int) {
        holder.bind(position)
    }
}