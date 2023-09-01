package com.example.realestate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.databinding.SingleLongDetailBinding
import com.example.realestate.utils.getDetailIcon

class DetailsLongAdapter : RecyclerView.Adapter<DetailsLongAdapter.DetailsLongHolder>() {

    private var featuresMap: Map<String, String> = mapOf()
    fun setFeatures(map: Map<String, String>) {
        featuresMap = map
        notifyDataSetChanged()
    }

    inner class DetailsLongHolder(val binding: SingleLongDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context
        fun bind(position: Int) {
            val currentDetail = featuresMap.entries.elementAt(position)
            binding.apply {

                val title = if (currentDetail.key == currentDetail.value) {
                    currentDetail.key
                } else {
                    context.getString(
                        R.string.long_detail,
                        currentDetail.key,
                        currentDetail.value
                    )
                }

                detailTitle.text = title

                detailTitle.setCompoundDrawablesWithIntrinsicBounds(
                    getDetailIcon(currentDetail.key, context),
                    null,
                    null,
                    null
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsLongHolder {
        return DetailsLongHolder(
            SingleLongDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = featuresMap.size

    override fun onBindViewHolder(holder: DetailsLongHolder, position: Int) {
        holder.bind(position)
    }
}
