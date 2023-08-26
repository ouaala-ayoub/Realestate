package com.example.realestate.ui.activities

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.example.realestate.data.models.PriceFilter
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.Type
import com.example.realestate.data.models.extras
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

        binding = ActivityFilterBinding.inflate(layoutInflater)
        filterModel = FilterModel(StaticDataRepository(Retrofit.getInstance())).apply {
//            getAllCities()
        }

        searchParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("search_params", SearchParams::class.java)
        } else {
            intent.getParcelableExtra("search_params")
        }
        Log.i(TAG, "params: $searchParams")

        binding.extrasChipGrp.forEach { view ->
            val checkBox = view as CheckBox

            checkBox.setOnClickListener {
                val text = checkBox.text
                Log.d(TAG, "chip text: $text")
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

        filterModel.apply {
            categoriesList.observe(this@FilterActivity) { categories ->
                Log.i(TAG, "categories: $categories")
                categories?.apply {
                    val categoriesToShow = this.capitalizeFirstLetter().sorted()
                    binding.categoryEditText.apply {
                        binding.categoryTextField.isEnabled = true
                        val adapter = setUpAndHandleSearch(categoriesToShow, object : OnSelected {
                            override fun onSelected(selectedItem: Editable?) {
                                val item = selectedItem.toString().lowerFirstLetter()
                                if (categories.contains(item))
                                    searchParams?.category = item
                                
                                binding.apply {
                                    val show = extras.contains(item)
                                    proprietyCdTv.isVisible = show
                                    proprietyConditionRg.isVisible = show
                                    extrasTv.isVisible = show
                                    extrasChipGrp.isVisible = show
                                }

                            }

                        })
                        searchParams?.category?.apply {
                            setText(this.upperFirstLetter())
                            adapter.filter.filter(null)
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
                    Log.d(TAG, "countries: $countries")
                    //Initialise country data
                    searchParams?.location?.country?.name?.apply {
                        Log.d(TAG, "country data: $this")
                        binding.countryEditText.setText(this)
                        getCities(this)
//                        val code = this?.code
//
//                        code?.apply {
//                            val country = countries.find { data -> data.code == code }
//                            Log.d(TAG, "country with code $code: $country")
//                            country?.name?.apply {
//                                binding.countryEditText.setText(this)
//                                getCities(this)
//                            }
//                        }

                    }

                    binding.countryEditText.apply {
                        val names = countries.map { data ->
                            data.name ?: "____"
                        }
                        val adapter = setUpAndHandleSearch(names, object : OnSelected {
                            override fun onSelected(selectedItem: Editable?) {
                                if (!selectedItem.isNullOrEmpty()) {
                                    searchParams?.setCountry(selectedItem.toString())
                                } else {
                                    searchParams?.setCountry(null)
                                }
                            }
                        })

                        binding.countryTextField.isEnabled = true

                        setOnItemClickListener { _, view, i, _ ->
                            adapter.filter.filter(null)
                            searchParams?.location?.country?.code = countries[i].code
                            val text = (view as TextView).text
                            getCities(text.toString())

                            binding.cityEditText.text.clear()
                            binding.cityTextField.isEnabled = false
                        }
                    }
                }

            }

            citiesToShow.observe(this@FilterActivity) { cities ->
                Log.d(TAG, "citiesToShow: $cities")
                binding.cityEditText.apply {
                    binding.cityTextField.isVisible = !cities.isNullOrEmpty()
                    binding.cityTextField.isEnabled = !cities.isNullOrEmpty()
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
                        binding.areaTextField.isEnabled = false
                    }
                }
            }
            streets.observe(this@FilterActivity) { streets ->
                binding.areaEditText.apply {
                    binding.areaTextField.isVisible = !streets.isNullOrEmpty()
                    binding.cityTextField.isEnabled = !streets.isNullOrEmpty()

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


        binding.search.setOnClickListener {
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

            //TODO is it really necessary ?
            searchParams.location?.city.apply {
                this?.apply {
                    cityTextField.isVisible = true
                    cityEditText.setText(this)
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