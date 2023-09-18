package com.example.realestate.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.CountriesDataItem
import com.example.realestate.data.models.PostWithOwnerId
import com.example.realestate.databinding.CustomCountryItemBinding
import com.example.realestate.utils.loadImage
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import java.util.*

class CustomCountryArrayAdapter(
    context: Context,
    private val res: Int,
    private val list: List<CountriesDataItem>
) :
    ArrayAdapter<CountriesDataItem>(context, res, list), Filterable {

    companion object {
        private const val TAG = "CustomCountryArrayAdapter"
    }

    private var filteredList: List<CountriesDataItem> = list
    private var onItemClickListener: ((CountriesDataItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (CountriesDataItem) -> Unit) {
        onItemClickListener = listener
    }

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: inflater.inflate(res, parent, false)
        val currentCountry = filteredList[position]

        view.setOnClickListener {
            onItemClickListener?.invoke(filteredList[position])
        }

        val countryImageIv = view.findViewById<ImageView>(R.id.custom_country_flag)
        val countryNameTv = view.findViewById<TextView>(R.id.custom_country_name)

        // Set custom data to your views
        countryImageIv.loadImage(currentCountry.image)
        countryNameTv.text = currentCountry.name

        return view
    }

    override fun getCount() = filteredList.size

    override fun getItem(position: Int): CountriesDataItem? {
        return filteredList[position]
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter = object : Filter() {
        override fun performFiltering(query: CharSequence?): FilterResults {
            Log.d(TAG, "query: $query")

            filteredList = if (query.isNullOrEmpty()) {
                list
            } else {
                list.filter { country ->
                    //filtering by name
                    country.name!!.contains(query.toString(), ignoreCase = true)
                }

            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(query: CharSequence?, result: FilterResults?) {
            notifyDataSetChanged()
        }

    }

}