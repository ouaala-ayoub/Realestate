package com.example.realestate.ui.fragments

import android.animation.TypeEvaluator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.DetailsLayoutBinding
import com.example.realestate.databinding.FragmentSinglePostEditBinding
import com.example.realestate.ui.fragments.post_add_steps.StepThreeFragment
import com.example.realestate.ui.viewmodels.SinglePostEditViewModel
import com.example.realestate.utils.*
import com.google.android.material.textfield.TextInputEditText

class SinglePostEditFragment : Fragment() {

    companion object {
        private const val TAG = "SinglePostEditFragment"
    }

    private var isConverting: Boolean = false
    private lateinit var binding: FragmentSinglePostEditBinding
    private lateinit var viewModel: SinglePostEditViewModel
    private val retrofit = Retrofit.getInstance()
    private val args: SinglePostEditFragmentArgs by navArgs()
    private lateinit var post: PostWithOwnerId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = args.post
        viewModel =
            SinglePostEditViewModel(
                PostsRepository(retrofit),
                StaticDataRepository(retrofit),
                post
            ).also {
                it.getAllCities()
            }
        Log.d(TAG, "post: $post")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSinglePostEditBinding.inflate(inflater, container, false)

        setUpViews()
        initialiseViews(post)

        binding.update.setOnClickListener {
            update()
        }

        return binding.root
    }

    private fun update() {
        post.apply {
            viewModel.apply {
                type = typeLd.value.toString()
//                category = categoryLd.value.toString()
                price = priceLd.value.toString()

                if (type == Type.RENT.value) {
                    period = periodLd.value
                }

                // TODO whatsapp
                val contactInfo = Contact()
                val whatsappNumber = whatsappLd.value
                val callNumber = callLd.value

                if (!whatsappNumber.isNullOrEmpty()) {
                    val code =
                        binding.whatsappPhoneInput.countryCode.selectedCountryCodeWithPlus
                    contactInfo.whatsapp = code + whatsappNumber
                }

                if (!callNumber.isNullOrEmpty()) {
                    val code =
                        binding.callPhoneInput.countryCode.selectedCountryCodeWithPlus
                    contactInfo.call = code + callNumber
                }

                contact = contactInfo

                location.apply {
                    country = countryLd.value
                    city = cityLd.value
                    area = areaLd.value
                }
                description = descriptionLd.value!!

                //TODO if category is house or ....
                if (!extras.contains(post.category)) {
                    viewModel.clearAllDetails()
                }

                features = featuresLd.value
                condition = conditionLd.value
                rooms = roomsLd.value?.toDoubleOrNull()
                bathrooms = bathroomsLd.value?.toDoubleOrNull()
                floors = floorsLd.value?.toDoubleOrNull()
                floorNumber = floorNumberLd.value?.toDoubleOrNull()
                space = spaceLd.value?.toDoubleOrNull()
            }
        }
        viewModel.updatePost(post.id!!, post)
    }

    private fun initialiseViews(post: PostWithOwnerId) {
        binding.apply {
            //type chips
            when (post.type) {
                Type.RENT.value -> {
                    rent.performClick()
                }
                Type.BUY.value -> {
                    forSell.performClick()
                }
            }

//            //category dropdown
//            categoryEditText.setText(post.category)

            //price
            priceEditText.setText(post.price)

            //period
            val shouldShow = post.type == Type.RENT.value
            periodRg.forEach { it.isEnabled = shouldShow }
            if (shouldShow) {
                periodRg.forEach { view ->
                    val radioButton = view as RadioButton
                    val text = radioButton.text.toString()
                    if (post.period == text) {
                        radioButton.performClick()
                    }
                }
            } else {
                periodRg.clearCheck()
                viewModel.clearPeriod()
            }


            //contact
//            whatsappPhoneInput.phoneEditText.setText(post.contact.whatsapp)
//            callPhoneInput.phoneEditText.setText(post.contact.call)

            //country
            countryEditText.setText(post.location.country)
            //city
            cityEditText.setText(post.location.city)
            //area
            post.location.area?.apply {
                streetEditText.setText(this)
            }

            descriptionEditText.setText(post.description)

            if (extras.contains(post.category)) {
                detailsLayout.apply {

                    //propriety
                    proprietyConditionRg.forEach { view ->
                        val radioButton = view as RadioButton
                        val text = radioButton.text.toString()
                        if (post.condition == text) {
                            radioButton.performClick()
                        }
                    }

                    //number Of rooms
                    numberOfRoomsEditText.setText(post.rooms.toString())

                    //number of bathrooms
                    numberOfBathroomsEditText.setText(post.bathrooms.toString())

                    //floor number
                    floorNumberEditText.setText(post.floorNumber.toString())

                    //floors
                    numberOfFloorsEditText.setText(post.floors.toString())

                    //space
                    converter.spaceMeterEditText.setText(post.space.toString())

                    //features
                    proprietyDetailsCg.forEach { view ->
                        val checkBox = view as CheckBox
                        val feature = checkBox.text.toString()
                        if (post.features?.contains(feature) == true) {
                            checkBox.isChecked = true
                        }
                    }

                }
            }

        }
    }

    private fun setUpViews() {
        binding.apply {
            viewModel.apply {

                //type
                mutableType.apply {
                    rent.setOnClickListener {
                        value = Type.RENT.value
                    }
                    forSell.setOnClickListener {
                        value = Type.BUY.value
                    }
                }

                //category
//                Categories.observe(viewLifecycleOwner, object : OnChanged<List<String>?> {
//                    override fun onChange(data: List<String>?) {
//                        categoryEditText.apply {
//                            data?.apply {
//                                val adapter = setUpAndHandleSearch(data)
//                                updateLiveData(mutableCategory)
//                                setOnItemClickListener { _, _, _, _ ->
//                                    adapter.filter.filter(null)
//                                }
//                            }
//
//                        }
//                    }
//                })


                //price
                priceEditText.addTextChangedListener(
                    NumberTextWatcher(
                        priceEditText,
                        mutablePrice
                    )
                )

                //period
                periodRg.setOnCheckedChangeListener { radioGroup, id ->
                    val button = radioGroup.findViewById<RadioButton>(id)
                    button?.apply {
                        val period = text.toString()
                        mutablePeriod.postValue(period)
                    }
                }

                //contact
                whatsappPhoneInput.phoneEditText.updateLiveData(mutableWhatsappNumber)
                callPhoneInput.phoneEditText.updateLiveData(mutableCallNumber)

                //country
                Countries.observe(viewLifecycleOwner, object : OnChanged<CountriesData> {
                    override fun onChange(data: CountriesData?) {
                        data?.apply {
                            val countries = map { data -> data.name }
                            countryEditText.apply {
                                val adapter = setUpAndHandleSearch(countries)
                                updateLiveData(mutableCountry)
                                setOnItemClickListener { _, view, _, _ ->
                                    val text = (view as TextView).text
                                    adapter.filter.filter(null)
                                    getCities(text.toString())
                                    cityEditText.text.clear()
                                }
                            }
                        }
                    }

                })

                cities.observe(viewLifecycleOwner) { cities ->
                    Log.i(TAG, "cities: $cities")
                    cityEditText.apply {
//                        cityTextField.isEnabled = !cities.isNullOrEmpty()
                        val adapter = setUpAndHandleSearch(cities)

                        setOnItemClickListener { _, _, _, _ ->
                            adapter.filter.filter(null)
//                            getStreets(selectedCity.toString())
                            streetEditText.apply {
                                this.text.clear()
//                                isEnabled = false
                            }
                        }
                    }
                }

                //city
                cityEditText.updateLiveData(mutableCity)

                //area
                streetEditText.updateLiveData(mutableArea)

                //description
                descriptionEditText.updateLiveData(mutableDescription)

                if (extras.contains(post.category)) {
                    detailsLayout.apply {

                        //condition
                        proprietyConditionRg.setOnCheckedChangeListener { radioGroup, i ->
                            val radioButton =
                                radioGroup.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
                            val state = radioButton.text.toString()

                            mutableCondition.postValue(state)
                        }

                        //number of rooms
                        numberOfRoomsEditText.updateLiveData(mutableRooms)

                        //bathrooms
                        numberOfBathroomsEditText.updateLiveData(mutableBathrooms)

                        //floor number
                        floorNumberEditText.updateLiveData(mutableFloorNumber)

                        //number of floors
                        numberOfFloorsEditText.updateLiveData(mutableFloors)

                        //space
                        converter.apply {
                            spaceMeterEditText.doOnTextChanged { text, _, _, _ ->
                                viewModel.apply {
                                    updateValue(
                                        text,
                                        spaceFootEditText,
                                        ::squareMeterToSquareFoot,
                                        ::setSpace
                                    )
                                    setSpace(text.toString())
                                }

                            }
                            spaceFootEditText.doOnTextChanged { text, _, _, _ ->
                                updateValue(text, spaceMeterEditText, ::squareFeetToSquareMeters)
                            }
                        }

                        //features
                        proprietyDetailsCg.forEach { view ->
                            val checkBox = view as CheckBox

                            checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
                                val feature = compoundButton.text.toString()
                                if (isChecked) {
                                    viewModel.addFeature(feature)
                                } else {
                                    viewModel.deleteFeature(feature)
                                }
                            }
                        }
                    }
                }

            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {

            //response handling
            response.observe(viewLifecycleOwner) { response ->
                Log.d(TAG, "response: $response")
                if (response != null)
                    requireContext().toast(response.message, Toast.LENGTH_SHORT)
                else
                    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
            }

            //handle details visibility
//            categoryLd.observe(viewLifecycleOwner) { category ->
//                val contains = extras.contains(category)
//                Log.d(TAG, "contains: $contains")
//                binding.detailsLayout.root.isVisible = contains
//            }

            //handle period visibility
            typeLd.observe(viewLifecycleOwner) { type ->
                val shouldShow = type == Type.RENT.value
                binding.periodRg.forEach { it.isEnabled = shouldShow }
                if (!shouldShow) {
                    binding.periodRg.clearCheck()
                    clearPeriod()
                }
            }

            loading.observe(viewLifecycleOwner) { loading ->
                binding.postEditProgressBar.isVisible = loading
            }
            isDataValid.observe(viewLifecycleOwner) { isValid ->
                binding.update.isEnabled = isValid
            }
        }
    }

    private fun updateValue(
        sourceText: CharSequence?,
        targetInput: TextInputEditText,
        converter: (Double) -> Double,
        setLiveData: ((String?) -> Unit)? = null
    ) {
        if (!isConverting) {
            isConverting = true

            val inputValue = sourceText.toString().toDoubleOrNull()
            setLiveData?.invoke(sourceText.toString())

            if (inputValue != null) {
                val convertedValue = converter(inputValue)
                targetInput.setText(formatDecimal(convertedValue))
            } else {
                targetInput.setText("")
            }

            isConverting = false
        }
    }

}