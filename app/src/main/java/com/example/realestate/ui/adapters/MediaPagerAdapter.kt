package com.example.realestate.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.data.models.MediaType
import com.example.realestate.databinding.ErrorBinding
import com.example.realestate.databinding.SinglePostImageBinding
import com.example.realestate.databinding.SinglePostVideoBinding
import com.example.realestate.utils.loadImage
import com.example.realestate.utils.loadVideo
import com.google.android.exoplayer2.ExoPlayer


class MediaPagerAdapter(private val mediaList: List<String>, private val exoPlayer: ExoPlayer? = null) :
    RecyclerView.Adapter<MediaPagerAdapter.MediaViewHolder>() {

    companion object {
        private const val TAG = "MediaPagerAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MediaType.IMAGE.ordinal -> {
                val binding = SinglePostImageBinding.inflate(inflater, parent, false)
                MediaViewHolder.ImageViewHolder(binding)
            }
            MediaType.VIDEO.ordinal -> {
                val binding = SinglePostVideoBinding.inflate(inflater, parent, false)
                MediaViewHolder.VideoViewHolder(binding)
            }
            else -> {
                val binding = ErrorBinding.inflate(inflater, parent, false)
                MediaViewHolder.ErrorHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        val isVideo = holder.itemViewType == MediaType.VIDEO.ordinal

        if (isVideo) {
            holder.bind(media, exoPlayer)
        } else {
            holder.bind(media)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getMediaType(mediaList[position]).ordinal
    }

    private fun getMediaType(url: String): MediaType {
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        Log.d(TAG, "mimeType: $mimeType")

        return when {
            mimeType?.startsWith("image/") == true -> MediaType.IMAGE
            mimeType?.startsWith("video/") == true -> MediaType.VIDEO
            else -> MediaType.UNKNOWN
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    sealed class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class ImageViewHolder(private val binding: SinglePostImageBinding) :
            MediaViewHolder(binding.root) {
            override fun bind(media: String, exoPlayer: ExoPlayer?) {
                binding.postImageVp.loadImage(media)
            }
        }

        class VideoViewHolder(private val binding: SinglePostVideoBinding) :
            MediaViewHolder(binding.root) {

            override fun bind(media: String, exoPlayer: ExoPlayer?) {
                binding.playerView.loadVideo(media, exoPlayer!!)
            }
        }

        class ErrorHolder(binding: ErrorBinding) :
            MediaViewHolder(binding.root) {

            override fun bind(media: String, exoPlayer: ExoPlayer?) {}
        }

        abstract fun bind(media: String, exoPlayer: ExoPlayer? = null)
    }
}