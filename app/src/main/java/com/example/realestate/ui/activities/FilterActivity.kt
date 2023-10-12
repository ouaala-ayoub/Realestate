package com.example.realestate.ui.activities

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.ActivityFilterBinding
import com.example.realestate.ui.viewmodels.FilterModel
import com.example.realestate.utils.*
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

        val dummyView = layoutInflater.inflate(R.layout.loading_screen, null, false)
        val asyncInflater = AsyncLayoutInflater(this)
        filterModel = FilterModel(StaticDataRepository(Retrofit.getInstance()))
        searchParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("search_params", SearchParams::class.java)
        } else {
            intent.getParcelableExtra("search_params")
        }

        asyncInflater.inflate(R.layout.activity_filter, null){ inflatedView, _, _ ->

            binding = ActivityFilterBinding.bind(inflatedView)

            binding.features.proprietyDetailsCg.forEach { view ->
                val checkBox = view as CheckBox

                checkBox.setOnClickListener {
                    val feature = checkBox.text.toString()
                    if (searchParams?.features == null) {
                        searchParams?.apply {
                            initialiseFeatures()
                            addFeature(feature)
                        }
                    } else if (searchParams?.features?.contains(feature) == true) {
                        searchParams?.deletedFeature(feature)
                    } else {
                        searchParams?.addFeature(feature)
                    }
                    Log.d(TAG, "searchParams?.features: ${searchParams?.features}")
                }
            }

            binding.priceFilterRg.setOnCheckedChangeListener { _, checkedId ->
                searchParams?.price = when (checkedId) {
                    binding.up.id -> {
                        PriceFilter.UP
                    }
                    binding.down.id -> {
                        PriceFilter.DOWN
                    }
                    else -> PriceFilter.NONE
                }
            }

            binding.search.setOnClickListener {
                //set the condition
                val button =
                    findViewById<RadioButton>(binding.proprietyConditionRg.checkedRadioButtonId)

                button?.apply {
                    searchParams?.condition = text.toString()
                }

                intent.putExtra("search_params", searchParams)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            (dummyView as ViewGroup).apply {
                removeAllViews()
                addView(binding.root)
            }

            filterModel.apply {
                categoriesList.observe(this@FilterActivity) { categories ->
                    Log.i(TAG, "categories: $categories")
                    categories?.apply {
                        val categoriesToShow = this.sorted()
                        binding.categoryEditText.apply {
                            binding.categoryTextField.isEnabled = true
                            val adapter = setUpAndHandleSearch(categoriesToShow, object : OnSelected {
                                override fun onSelected(selectedItem: Editable?) {
                                    val item = selectedItem.toString()
                                    if (categories.contains(item))
                                        searchParams?.category = item

                                    binding.apply {
                                        val show = extras.contains(item)
                                        proprietyCdTv.isVisible = show
                                        proprietyConditionRg.isVisible = show
                                        extrasTv.isVisible = show
                                        features.root.isVisible = show
                                    }

                                }

                            })
                            searchParams?.category?.apply {
                                setText(this)
                                adapter.filter.filter(null)
                                binding.categoryEditText.setSelection(this.length)
                            }
                            setOnItemClickListener { _, _, _, _ ->
                                adapter.filter.filter(null)
                                hideKeyboard()
                            }
                        }
                    }
                }

                countries.observe(this@FilterActivity) { countries ->

                    countries?.apply {
                        //Initialise country data
                        searchParams?.location?.country?.name?.apply {
                            binding.countryEditText.setText(this)
                            binding.countryEditText.setSelection(this.length)
                            getCities(this)
                        }

                        binding.countryEditText.apply {

                            val adapter = setUpCountriesAndHandleSearch(countries)

                            binding.countryTextField.isEnabled = true
                            binding.cityTextField.isEnabled = true
                            binding.areaTextField.isEnabled = true

                            adapter.setOnItemClickListener { selectedItem ->
                                val name = selectedItem.name

                                if (!name.isNullOrEmpty()) {
                                    searchParams?.setCountry(name)
                                    setText(name.toString(), false)
                                    setSelection(name.length)
                                    adapter.filter.filter(null)
                                    dismissDropDown()
                                    getCities(name)
                                } else {
                                    searchParams?.setCountry(null)
                                }


                                binding.cityEditText.text.clear()
                            }

//                        setOnItemClickListener { _, view, i, _ ->
//
//                            val selectedItem = adapter.getItem(i)
//
//
//                        }
                        }
                    }

                }

                citiesToShow.observe(this@FilterActivity) { cities ->
                    Log.d(TAG, "citiesToShow: $cities")
                    binding.cityEditText.apply {
                        val adapter = setUpAndHandleSearch(cities, object : OnSelected {
                            override fun onSelected(selectedItem: Editable?) {
                                if (!selectedItem.isNullOrEmpty()) {
                                    searchParams?.setCity(selectedItem.toString())
                                } else {
                                    searchParams?.setCity(null)
                                }
                            }
                        })

                        setOnItemClickListener { _, view, _, _ ->
                            val selectedCity = (view as TextView).text
                            Log.i(TAG, "onItemSelected: $selectedCity")
                            adapter.filter.filter(null)
//                            getStreets(selectedCity.toString())
                            binding.areaEditText.text.clear()
                        }
                    }
                }
                streets.observe(this@FilterActivity) { streets ->
                    binding.areaEditText.apply {

                        val adapter = setUpAndHandleSearch(streets, object : OnSelected {
                            override fun onSelected(selectedItem: Editable?) {
                                if (!selectedItem.isNullOrEmpty()) {
                                    searchParams?.setArea(selectedItem.toString())
                                } else {
                                    searchParams?.setArea(null)
                                }
                            }
                        })
                        setOnItemClickListener { _, _, _, _ ->
                            adapter.filter.filter(null)
                        }
                    }
                }
                searchParams?.apply {
                    initialiseViews(this)
                }
            }

        }

        setContentView(dummyView)
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

            //TODO is it really necessary ?
            searchParams.location?.city.apply {
                this?.apply {
                    cityTextField.isVisible = true
                    cityEditText.setText(this)
                    cityEditText.setSelection(this.length)
//                    filterModel.getAreas(this)
                }
            }

            searchParams.location?.area.apply {
                this?.apply {
                    areaTextField.isVisible = true
                    areaEditText.setText(this)
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