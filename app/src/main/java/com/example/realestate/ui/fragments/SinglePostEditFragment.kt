package com.example.realestate.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.DetailsLayoutBinding
import com.example.realestate.databinding.FragmentSinglePostEditBinding
import com.example.realestate.ui.viewmodels.SinglePostEditViewModel
import com.example.realestate.utils.*
import com.google.android.material.textfield.TextInputEditText


class SinglePostEditFragment : Fragment() {

    companion object {
        private const val TAG = "SinglePostEditFragment"
    }

    private var isConverting: Boolean = false
    private var _binding: FragmentSinglePostEditBinding? = null
    private val binding get() = _binding!!
    private var _detailsBinding: DetailsLayoutBinding? = null
    private val detailsBinding get() = _detailsBinding!!
    private lateinit var viewModel: SinglePostEditViewModel
    private val retrofit = Retrofit.getInstance()
    private val args: SinglePostEditFragmentArgs by navArgs()
    private lateinit var post: PostWithOwnerId
    private val onChanged = object : OnChanged<CountriesData> {
        override fun onChange(data: CountriesData?) {
            data?.apply {
                val countries = map { data -> data.name }
                binding.countryEditText.apply {
                    val adapter = setUpAndHandleSearch(countries)
                    updateLiveData(viewModel.mutableCountry)
                    setOnItemClickListener { _, view, _, _ ->
                        val text = (view as TextView).text
                        adapter.filter.filter(null)
                        viewModel.getCities(text.toString())
                        binding.cityEditText.text.clear()
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = System.nanoTime()
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
        val endTime = System.nanoTime()
        val elapsedTime = (endTime - startTime) / 1000000
        Log.d(TAG, "onCreate function took $elapsedTime ms to execute")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dummyView = inflater.inflate(R.layout.loading_screen, container, false)
        val asyncInflater = AsyncLayoutInflater(requireContext())

        asyncInflater.inflate(
            R.layout.fragment_single_post_edit, null
        ) { inflatedView, _, _ ->
            _binding = FragmentSinglePostEditBinding.bind(inflatedView)

            if (view != null) {
                setUpViews()
                initialiseViews(post)

                binding.update.setOnClickListener {
                    update()
                }
                (dummyView as ViewGroup).apply {
                    removeAllViews()
                    addView(binding.root)
                }
                handleViewModelLogic()
            }
        }

        return dummyView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _detailsBinding = null
        _binding = null
        Countries.remove(onChanged)
    }

    private fun update() {
        val startTime = System.nanoTime()
        post.apply {
            viewModel.apply {
                type = typeLd.value.toString()
//                category = categoryLd.value.toString()
                price = priceLd.value.toString()

                if (type == Type.RENT.value) {
                    period = periodLd.value
                }

                // TODO whatsapp
                val code =
                    binding.phoneNumber.countryCode.selectedCountryCodeWithPlus
                val phoneNumberValue = viewModel.phoneNumber.value.toString()
                val contactType = viewModel.contactType.value.toString()

                contact = Contact(code, phoneNumberValue, contactType)

                location.apply {
                    country = countryLd.value
                    city = cityLd.value
                    area = areaLd.value
                }
                description = descriptionLd.value!!

                //TODO if category is house or ....
                if (extras.contains(post.category)) {
                    features = featuresLd.value
                    condition = conditionLd.value
                    rooms = roomsLd.value?.toDoubleOrNull()
                    bathrooms = bathroomsLd.value?.toDoubleOrNull()
                    floors = floorsLd.value?.toDoubleOrNull()
                    floorNumber = floorNumberLd.value?.toDoubleOrNull()
                    space = spaceLd.value?.toDoubleOrNull()
                }


            }
        }
        Log.d(TAG, "update post $post")
        viewModel.updatePost(post.id!!, post)
        val endTime = System.nanoTime()
        val elapsedTime = (endTime - startTime) / 1000000
        Log.d(TAG, "update function took $elapsedTime ms to execute")
    }

    private fun initialiseViews(post: PostWithOwnerId) {
        val startTime = System.nanoTime()
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
            phoneNumber.apply {
                val phoneCode = post.contact.code.replace("+", "").toInt()
                phoneEditText.setText(post.contact.phoneNumber)
                countryCode.setCountryForPhoneCode(phoneCode)
            }

            whatsapp.isChecked =
                post.contact.type == ContactType.WHATSAPP.value || post.contact.type == ContactType.Both.value

            call.isChecked =
                post.contact.type == ContactType.CALL.value || post.contact.type == ContactType.Both.value

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
                detailsBinding.apply {

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
                    floorInfo.floorNumberEditText.setText(post.floorNumber.toString())

                    //floors
                    floorInfo.numberOfFloorsEditText.setText(post.floors.toString())

                    //space
                    converter.spaceMeterEditText.setText(post.space.toString())

                    //features
                    features.proprietyDetailsCg.forEach { view ->
                        val checkBox = view as CheckBox
                        val feature = checkBox.text.toString()
                        if (post.features?.contains(feature) == true) {
                            checkBox.isChecked = true
                        }
                    }

                }
            }

        }
        val endTime = System.nanoTime()
        val elapsedTime = (endTime - startTime) / 1000000
        Log.d(TAG, "initialiseViews function took $elapsedTime ms to execute")
    }

    private fun setUpViews() {
        val startTime = System.nanoTime()
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
                binding.phoneNumber.phoneEditText.updateLiveData(mutablePhoneNumber)

                binding.apply {
                    whatsapp.setOnCheckedChangeListener { _, isChecked ->
                        viewModel.updateSelectedOptions(isChecked, call.isChecked)
                    }

                    call.setOnCheckedChangeListener { _, isChecked ->
                        viewModel.updateSelectedOptions(whatsapp.isChecked, isChecked)
                    }
                }

                //country
                Countries.observe(viewLifecycleOwner, onChanged)


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
                    _detailsBinding =
                        DetailsLayoutBinding.inflate(layoutInflater, binding.linearLayout, true)
                    detailsBinding.apply {

                        //condition
                        proprietyConditionRg.setOnCheckedChangeListener { radioGroup, _ ->
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
                        floorInfo.floorNumberEditText.updateLiveData(mutableFloorNumber)

                        //number of floors
                        floorInfo.numberOfFloorsEditText.updateLiveData(mutableFloors)

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
                        features.proprietyDetailsCg.forEach { view ->
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
        val endTime = System.nanoTime()
        val elapsedTime = (endTime - startTime) / 1000000
        Log.d(TAG, "setUpViews function took $elapsedTime ms to execute")
    }

    private fun handleViewModelLogic() {
        viewModel.apply {

            //response handling
            response.observe(viewLifecycleOwner) { response ->
                Log.d(TAG, "response: $response")
                if (response != null)
                    response.message?.apply {
                        requireContext().toast(response.message, Toast.LENGTH_SHORT)
                    }
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