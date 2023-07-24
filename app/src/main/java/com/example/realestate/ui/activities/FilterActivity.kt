package com.example.realestate.ui.activities

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import com.example.realestate.R
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.Type
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.ActivityFilterBinding
import com.example.realestate.ui.fragments.HomeFragment
import com.example.realestate.ui.viewmodels.FilterModel
import com.example.realestate.utils.setUpAndHandleSearch
import com.example.realestate.utils.setWithList
import com.google.android.material.chip.Chip

class FilterActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "FilterActivity"
    }

    private var selectedChipId = -1
    private lateinit var binding: ActivityFilterBinding
    private lateinit var filterModel: FilterModel
    private var searchParams: SearchParams? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilterBinding.inflate(layoutInflater)
        filterModel = FilterModel(StaticDataRepository(Retrofit.getInstance()))


        searchParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("search_params", SearchParams::class.java)
        } else {
            intent.getParcelableExtra("search_params")
        }
        Log.d(TAG, "params: $searchParams")

        searchParams?.apply {
            initialiseViews(this)
        }

        filterModel.categoriesList.observe(this) { categories ->
            if (!categories.isNullOrEmpty()) {
                binding.categoryEditText.apply {
                    val adapter = setUpAndHandleSearch(categories)
                    searchParams?.category?.apply {
                        setText(this)
                        adapter.filter.filter(null)
                    }
                    doOnTextChanged { text, _, _, _ ->
                        searchParams?.category = text.toString()
                    }
                    setOnItemClickListener { _, _, _, _ ->
                        adapter.filter.filter(null)
                    }
                }
            } else {
                binding.categoryEditText.apply {
                    setText(getString(R.string.error_getting_type))
                    isEnabled = false
                }
            }
        }


        binding.search.setOnClickListener {
            Log.d(TAG, "searchParams: $searchParams")
            intent.putExtra("search_params", searchParams)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        setContentView(binding.root)
    }

    private fun initialiseViews(searchParams: SearchParams) {
        binding.apply {
            handleChips()
            when (searchParams.type) {
                Type.RENT.value -> {
                    binding.rent.performClick()
                }
                Type.BUY.value -> {
                    binding.buy.performClick()
                }
                null -> {
                    binding.all.performClick()
                }
            }
        }
    }

    private fun onChipClicked(view: View) {
        val chipId = view.id

        // Unselect previously selected chip if any
        if (selectedChipId != -1) {
            val previousChip = findViewById<Chip>(selectedChipId)
            previousChip.isChecked = false
            previousChip.isEnabled = true
        }

        // Update selected chip
        val chip = view as Chip
        chip.isChecked = true
        chip.isEnabled = false
        selectedChipId = chipId

        // Perform actions based on the selected chip
        when (chipId) {
            binding.all.id -> {
                searchParams?.type = null
            }
            binding.rent.id -> {
                searchParams?.type = Type.RENT.value
            }
            binding.buy.id -> {
                searchParams?.type = Type.BUY.value
            }
        }
    }

    private fun handleChips() {

        for (chip in binding.chips.children) {
            chip.setOnClickListener {
                if (selectedChipId == chip.id) {
                    // Chip is already selected, do nothing
                    chip.isEnabled = false
                    return@setOnClickListener
                }
                onChipClicked(it)
            }
        }
    }
}