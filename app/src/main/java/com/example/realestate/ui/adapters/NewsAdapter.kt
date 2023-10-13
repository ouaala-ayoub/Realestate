package com.example.realestate.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.data.models.NewsElement
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.databinding.SingleNewsLayoutBinding
import com.example.realestate.utils.loadImage

//TODO add onClickListener
class NewsAdapter() : RecyclerView.Adapter<NewsAdapter.NewsHolder>() {

    companion object {
        private const val TAG = "NewsAdapter"
    }

    private var newsList = listOf<NewsElement>()
    fun setNewsList(list: List<NewsElement>) {
        newsList = list
        notifyDataSetChanged()
    }

    inner class NewsHolder(private val binding: SingleNewsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val currentNews = newsList[position]
            val contentAdapter = ContentAdapter(currentNews.contents)
            val context = binding.root.context

            binding.apply {
                val link = "${Retrofit.WEBSITE_BASE_URL}${currentNews.image.removePrefix("/")}"
                Log.d(TAG, "link: $link")
                newsImage.loadImage(link)
                newsTitle.text = currentNews.title
                contentRv.apply {
                    adapter = contentAdapter
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        return NewsHolder(
            SingleNewsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}
