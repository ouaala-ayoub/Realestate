package com.example.realestate.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.databinding.SimpleTextLayoutBinding

class ContentAdapter(private val data: List<String>) : RecyclerView.Adapter<ContentAdapter.TextViewHolder>() {
    inner class TextViewHolder(private var binding: SimpleTextLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.newsContent.text = data[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(
            SimpleTextLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
