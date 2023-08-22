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
        private const val TAG = "DetailsAdapter"
    }

    private var availableDetailsList = listOf<Pair<String, Any>>()

    fun setDetails(details: Details) {
        availableDetailsList = if (detailsType == DetailsType.LONG) {
            details.getAvailableDetails()
        } else {
            details.getShortDetails()
        }

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

                    if (currentDetail.second != true)
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

                    if (currentDetail.second != true) {
                        val title = binding.root.context.getString(
                            R.string.long_detail,
                            currentDetail.first,
                            currentDetail.second
                        )
                        detailTitle.text = title
                    } else
                        detailTitle.text = currentDetail.first

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
                "Property Condition" -> {
                    R.drawable.baseline_content_paste_search_24
                }
                "Number Of rooms" -> {
                    R.drawable.baseline_bed_24
                }
                "Number of bathrooms" -> {
                    R.drawable.baseline_bathroom_24
                }
                "Floor Info" -> {
                    R.drawable.skyscraper_svgrepo_com
                }
                "Space" -> {
                    R.drawable.measure_area_svgrepo_com
                }
                "Balcony" -> {
                    R.drawable.antique_balcony_svgrepo_com
                }
                "Furnished" -> {
                    R.drawable.furniture_svgrepo_com
                }
                "New" -> {
                    R.drawable.baseline_fiber_new_24
                }
                "Elevator" -> {
                    R.drawable.elevator_svgrepo_com
                }
                "Security" -> {
                    R.drawable.secure_shield_password_protect_safe_svgrepo_com
                }
                "Gym" -> {
                    R.drawable.gym_svgrepo_com
                }
                "Swimming Pool" -> {
                    R.drawable.baseline_pool_24
                }
                "Parking" -> {
                    R.drawable.baseline_local_parking_24
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