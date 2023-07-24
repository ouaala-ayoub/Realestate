package com.example.realestate.ui.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.Details
import com.example.realestate.data.models.DetailsType
import com.example.realestate.databinding.ErrorBinding
import com.example.realestate.databinding.SingleLongDetailBinding
import com.example.realestate.databinding.SingleShortDetailBinding

class DetailsAdapter(private val detailsType: DetailsType) :
    RecyclerView.Adapter<DetailsAdapter.DetailsHolder>() {

    companion object {
        private const val TAG = "DetailsShortAdapter"
    }

    private var availableDetailsList = listOf<Pair<String, Any>>()

    fun setDetails(details: Details) {
        availableDetailsList = details.getAvailableDetails()
        notifyDataSetChanged()
    }

    sealed class DetailsHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(currentDetail: Pair<String, Any>)

        class DetailShortHolder(private val binding: SingleShortDetailBinding) :
            DetailsHolder(binding.root) {
            override fun bind(currentDetail: Pair<String, Any>) {

                Log.d(TAG, "currentDetail: $currentDetail")

                binding.apply {
                    detailBody.text = currentDetail.second.toString()
                    detailBody.setCompoundDrawablesWithIntrinsicBounds(
                        getDetailIcon(currentDetail.first),
                        null,
                        null,
                        null
                    )
                }
            }
        }

        class DetailLongHolder(private val binding: SingleLongDetailBinding) :
            DetailsHolder(binding.root) {
            override fun bind(currentDetail: Pair<String, Any>) {

                Log.d(TAG, "currentDetail: $currentDetail")

                binding.apply {
                    detailTitle.text = currentDetail.first
                    detailBody.text = currentDetail.second.toString()
                    detailTitle.setCompoundDrawablesWithIntrinsicBounds(
                        getDetailIcon(currentDetail.first),
                        null,
                        null,
                        null
                    )
                }
            }
        }

        class ErrorHolder(binding: ErrorBinding) :
            DetailsHolder(binding.root) {

            override fun bind(currentDetail: Pair<String, Any>) {}
        }


        fun getDetailIcon(key: String): Drawable? {
            val drawableRes = when (key) {
                "Number Of Beds" -> {
                    R.drawable.baseline_bed_24
                }
                else -> {
                    R.drawable.baseline_broken_image_24
                }
            }
            return ContextCompat.getDrawable(itemView.context, drawableRes)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsHolder {
        return when (viewType) {
            DetailsType.SHORT.ordinal -> {
                DetailsHolder.DetailShortHolder(
                    SingleShortDetailBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            DetailsType.LONG.ordinal -> {
                DetailsHolder.DetailLongHolder(
                    SingleLongDetailBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                DetailsHolder.ErrorHolder(
                    ErrorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return detailsType.ordinal
    }

    override fun getItemCount() = availableDetailsList.size

    override fun onBindViewHolder(holder: DetailsHolder, position: Int) {
        val currentDetail = availableDetailsList[position]
        holder.bind(currentDetail)
    }
}