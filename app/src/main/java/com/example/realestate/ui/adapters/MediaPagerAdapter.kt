package com.example.realestate.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.data.models.MediaType
import com.example.realestate.databinding.SinglePostImageBinding
import com.example.realestate.databinding.SinglePostVideoBinding
import com.example.realestate.utils.loadImage
import com.example.realestate.utils.loadVideo

class MediaPagerAdapter(private val mediaList: List<String>) :
    RecyclerView.Adapter<MediaPagerAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == MediaType.IMAGE.ordinal) {
            val binding = SinglePostImageBinding.inflate(inflater, parent, false)
            MediaViewHolder.ImageViewHolder(binding)
        } else {
            val binding = SinglePostVideoBinding.inflate(inflater, parent, false)
            MediaViewHolder.VideoViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        holder.bind(media)
    }

    override fun getItemViewType(position: Int): Int {
        //to modify
//        val isImage = mediaList[position].split(".")[1] == "jpg"
        val isImage = true
        return if (isImage) {
            MediaType.IMAGE.ordinal
        } else {
            MediaType.VIDEO.ordinal
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    sealed class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class ImageViewHolder(private val binding: SinglePostImageBinding) :
            MediaViewHolder(binding.root) {

            override fun bind(media: String) {
                binding.postImageVp.loadImage(media)
            }
        }

        class VideoViewHolder(private val binding: SinglePostVideoBinding) :
            MediaViewHolder(binding.root) {

            override fun bind(media: String) {
                binding.videoView.loadVideo(media)
            }
        }

        abstract fun bind(media: String)
    }
}