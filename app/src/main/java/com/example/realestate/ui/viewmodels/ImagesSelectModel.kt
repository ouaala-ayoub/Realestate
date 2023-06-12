package com.example.realestate.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.data.models.Images
import com.example.realestate.ui.adapters.ImagesAdapter
import com.example.realestate.utils.swap

class ImagesSelectModel : ViewModel() {

    private val _isFull = MutableLiveData<Boolean>(false)
    val isFull: LiveData<Boolean>
        get() = _isFull

    private fun updateIsFull(images: Images) {
        _isFull.postValue(!images.imageUris.contains(null))
    }

    fun checkImageAt(
        position: Int,
        images: Images,
        rv: RecyclerView.Adapter<ImagesAdapter.ImagesHolder>
    ) {
        val oldSelected = images.selectedPosition
        images.selectedPosition = position
        rv.apply {
            notifyItemChanged(position)
            notifyItemChanged(oldSelected)
        }
    }

    fun deleteImageAt(
        position: Int,
        images: Images,
        rv: RecyclerView.Adapter<ImagesAdapter.ImagesHolder>
    ) {
        images.imageUris.apply {
            removeAt(position)
            add(null)
        }
//            notifyItemChanged(position)
        rv.notifyDataSetChanged()
        updateIsFull(images)
    }

    fun addImages(
        listToAdd: List<Uri>,
        images: Images,
        rv: RecyclerView.Adapter<ImagesAdapter.ImagesHolder>
    ) {
        val startIndex = images.imageUris.indexOf(null)
        for (i in listToAdd.indices) {
            if (startIndex + i <= images.imageUris.lastIndex && startIndex + i >= 0) {
                images.imageUris[startIndex + i] = listToAdd[i]
                rv.notifyItemChanged(startIndex + i)
            } else {
                break
            }
        }
        updateIsFull(images)
    }

    fun getResult(images: Images): List<Uri> {
        images.imageUris.swap(images.selectedPosition, 0)
        return images.imageUris.filterNotNull()
    }

}