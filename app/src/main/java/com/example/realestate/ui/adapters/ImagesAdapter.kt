package com.example.realestate.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.data.models.Images
import com.example.realestate.databinding.SingleImageBinding
import com.example.realestate.ui.viewmodels.postaddmodels.ImagesSelectModel
import com.example.realestate.utils.loadImageUri
import com.example.realestate.utils.swap

class ImagesAdapter(
    imagesNumber: Int,
    private val viewModel: ImagesSelectModel
) : RecyclerView.Adapter<ImagesAdapter.ImagesHolder>() {


    companion object {
        private const val TAG = "ImagesAdapter"
        private const val MARGIN_SIZE = 5
    }

    private var imagesList: Images = Images(
        MutableList(imagesNumber) {
            null
        }
    )

    inner class ImagesHolder(private val binding: SingleImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val currentImage = imagesList.imageUris[position]
            val isNull = imagesList.imageUris[position] == null
            val isSelected = position == imagesList.selectedPosition

            binding.apply {
                checkBox.apply {
                    isChecked = isSelected && !isNull
                    isEnabled = !isNull
                    isClickable = !isSelected
                }

                delete.setOnClickListener {
                    viewModel.deleteImageAt(position, imagesList, this@ImagesAdapter)
                }
                currentImage?.apply {
                    //if not null means image place holder contains a selected images
//                    selectedImage.setImageURI(this)
                    selectedImage.loadImageUri(this)
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

            }
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

    override fun getItemCount() = imagesList.imageUris.size

    override fun onBindViewHolder(holder: ImagesHolder, position: Int) {
        holder.bind(position)
    }

    fun addImages(listToAdd: List<Uri>) {
        viewModel.addImages(listToAdd, imagesList, this)
    }

    fun getResult(): List<Uri> {
        imagesList.imageUris.swap(imagesList.selectedPosition, 0)
        return imagesList.imageUris.filterNotNull()
    }

}