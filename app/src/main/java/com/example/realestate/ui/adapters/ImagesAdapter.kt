package com.example.realestate.ui.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.Media
import com.example.realestate.data.models.UriHolder
import com.example.realestate.databinding.SingleImageBinding
import com.example.realestate.ui.viewmodels.postaddmodels.ImagesSelectModel
import com.example.realestate.utils.loadImageUri

class ImagesAdapter(
    imagesNumber: Int,
    private val viewModel: ImagesSelectModel
) : RecyclerView.Adapter<ImagesAdapter.ImagesHolder>() {


    companion object {
        private const val TAG = "ImagesAdapter"
        private const val PROGRESS_COMPLETE = 100
    }

    private var imagesList: Media = Media(
        MutableList(imagesNumber) {
            UriHolder()
        }
    )

    fun setMedia(media: Media) {
        imagesList = media
        notifyDataSetChanged()
    }

    inner class ImagesHolder(private val binding: SingleImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val currentImage = imagesList.uriHolders[position].uri
            val currentProgress = imagesList.uriHolders[position].uploadProgress
            val isNull = currentImage == null
            val isSelected = position == imagesList.selectedPosition

            binding.apply {
                checkBox.apply {
                    isChecked = isSelected && !isNull
                    isEnabled = !isNull
                    isClickable = !isSelected
                }

                delete.setOnClickListener {
                    if (currentImage != null) {
                        viewModel.deleteImageAt(position, imagesList, this@ImagesAdapter)
                    }
                }

                if (currentImage == null) {
                    selectedImage.setImageResource(R.drawable.image_cadre)
                } else {
                    selectedImage.loadImageUri(currentImage)
                }

                //to enhance
                if (!isSelected && !isNull) {
                    wholeImage.setOnClickListener {
                        viewModel.checkImageAt(position, imagesList, this@ImagesAdapter)
                    }
                    checkBox.setOnClickListener {
                        viewModel.checkImageAt(position, imagesList, this@ImagesAdapter)
                    }
                }

                //handle progress
                if (currentProgress != null) {

                    if (currentProgress == PROGRESS_COMPLETE) {
                        hideLoadingDialog()
                    } else {
                        showLoadingDialog()
                        binding.imageUploadProgress.progress = currentProgress
                    }

                } else {
                    hideLoadingDialog()
                }
            }
        }

        private fun showLoadingDialog() {
            binding.imageUploadProgress.isVisible = true
        }

        private fun hideLoadingDialog() {
            binding.imageUploadProgress.isVisible = false
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesHolder {
        val view = SingleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        //to go back to this piece of code
//        val cardWidth = parent.width / (imagesNumber / 3)
//        val cardHeight = parent.height / (imagesNumber / 3)
//        val cardSideLength = min(cardWidth, cardHeight)
//        val layoutParams = view.wholeImage.layoutParams as ViewGroup.MarginLayoutParams
//        layoutParams.width = cardSideLength
//        layoutParams.height = cardSideLength
////        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)

        return ImagesHolder(
            view
        )
    }

    override fun getItemCount() = imagesList.uriHolders.size

    override fun onBindViewHolder(holder: ImagesHolder, position: Int) {
        holder.bind(position)
    }

    fun addImages(listToAdd: List<Uri>, context: Context) {
        viewModel.addImages(listToAdd, imagesList, this, context)
    }

    fun getUploadedMedia(): List<String> {
        return viewModel.getResult(imagesList.selectedPosition)
    }

    fun updateProgress(progress: Int, position: Int) {
        imagesList.uriHolders[position].uploadProgress = progress
        notifyItemChanged(position)
    }
}