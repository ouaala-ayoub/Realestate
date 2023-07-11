package com.example.realestate.ui.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.DetailsType
import com.example.realestate.databinding.ErrorBinding
import com.example.realestate.databinding.SingleLongDetailBinding
import com.example.realestate.databinding.SingleShortDetailBinding
import com.example.realestate.utils.getMediaType
import com.google.android.exoplayer2.ExoPlayer

class DetailsAdapter(private val detailsType: DetailsType) :
    RecyclerView.Adapter<DetailsAdapter.DetailsHolder>() {

    companion object {
        private const val TAG = "DetailsShortAdapter"
    }

    sealed class DetailsHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(currentDetail: Map.Entry<String, String>)

        class DetailShortHolder(private val binding: SingleShortDetailBinding) :
            DetailsHolder(binding.root) {
            override fun bind(currentDetail: Map.Entry<String, String>) {

                Log.d(TAG, "currentDetail: $currentDetail")

                binding.apply {
                    detailBody.text = currentDetail.value
                    detailBody.setCompoundDrawablesWithIntrinsicBounds(
                        getDetailIcon(currentDetail.key),
                        null,
                        null,
                        null
                    )
                }
            }
        }

        class DetailLongHolder(private val binding: SingleLongDetailBinding) :
            DetailsHolder(binding.root) {
            override fun bind(currentDetail: Map.Entry<String, String>) {

                Log.d(TAG, "currentDetail: $currentDetail")

                binding.apply {
                    detailTitle.text = currentDetail.key
                    detailBody.text = currentDetail.value
                    detailTitle.setCompoundDrawablesWithIntrinsicBounds(
                        getDetailIcon(currentDetail.key),
                        null,
                        null,
                        null
                    )
                }
            }
        }

        class ErrorHolder(binding: ErrorBinding) :
            DetailsHolder(binding.root) {

            override fun bind(currentDetail: Map.Entry<String, String>) {}
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

    private var detailsMap: Map<String, String> = mapOf()

    fun setDetailsMap(map: Map<String, String>) {
        detailsMap = map
        notifyDataSetChanged()
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

    override fun getItemCount() = detailsMap.size

    override fun onBindViewHolder(holder: DetailsHolder, position: Int) {
        val currentDetail = detailsMap.entries.elementAt(position)
        holder.bind(currentDetail)
    }
}