package com.example.realestate.ui.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realestate.R
import com.example.realestate.data.models.*
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.ChipVeilledBinding
import com.example.realestate.databinding.FragmentHomeModifiedBinding
import com.example.realestate.ui.activities.ActivityWanted
import com.example.realestate.ui.activities.MainActivity
import com.example.realestate.ui.adapters.PostsAdapter
import com.example.realestate.ui.viewmodels.HomeViewModel
import com.example.realestate.utils.*
import com.google.android.material.chip.Chip


class HomeFragment : Fragment(), ActivityResultListener {

    companion object {
        private const val TAG = "HomeFragment"
        var count = 0
    }

    private var isUserClick = false
    private var firstTime = true
    private var recyclerViewState: Parcelable? = null
    private var countryAdapter: ArrayAdapter<String?>? = null
    private lateinit var binding: FragmentHomeModifiedBinding
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var searchParams: SearchParams
    private var selectedOption: RadioButton? = null
    private var selectedChipId: Int = -1
    val viewModel: HomeViewModel by lazy {
        val retrofit = Retrofit.getInstance()
        HomeViewModel(
            PostsRepository(retrofit),
            StaticDataRepository(retrofit),
            UsersRepository(retrofit)
        ).also {
            it.apply {
                getCategories()
                getPosts(source = "lazy")

                if (!CurrentUser.isConnected())
                    getAuth()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "count: $count")
        count++
        val activity = (requireActivity() as MainActivity)
        //initialise filter params
        searchParams = activity.params


        //set the listener
        activity.setActivityResultListener(this)


        postsAdapter = PostsAdapter(
            object : OnPostClickListener {
                override fun onClick(postId: String): Nothing? {
                    goToPostFragment(postId)
                    return super.onClick(postId)
                }
            },
            object : OnAddToFavClicked {
                override fun onChecked(postId: String) {
                    viewModel.unlike(postId)
                }

                override fun onUnChecked(postId: String) {
                    viewModel.like(postId)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeModifiedBinding.inflate(inflater, container, false)

        //back button handling
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //to add a confirmation dialog
                    val dialog = makeDialog(
                        requireContext(),
                        object : OnDialogClicked {
                            override fun onPositiveButtonClicked() {
                                requireActivity().finish()
                            }

                            override fun onNegativeButtonClicked() {
                                // nothing
                            }
                        },
                        title = getString(R.string.app_name),
                        message = getString(R.string.Leave),
                        negativeText = getString(R.string.No),
                        positiveText = getString(R.string.Yes)
                    )
                    dialog.apply {
                        show()
                        separateButtonsBy(15)
                    }

                }

            })


//        val countryPicker = binding.countryPicker
        searchParams.location?.country =
            CountriesDataItem(
//                name = countryPicker.selectedCountryName,
//                code = countryPicker.selectedCountryNameCode
            )

        binding.apply {

            selectedChipId = binding.all.id
            handleChips()

            //default selected chip
//            binding.all.performClick()

            handleSearch()

            //handle swipe gesture
            swipeRefreshLayout.setOnRefreshListener {
                searchParams = SearchParams()
                refreshEveryView()
                viewModel.getPosts(
                    searchParams,
                    source = "swipeRefreshLayout.setOnRefreshListener",
                )
                if (viewModel.categoriesList.value.isNullOrEmpty()) {
                    viewModel.getCategories()
                }
            }

            //disable swipe refresh if not on top
            scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                val isAtTop = scrollY == 0
                val contentHeight = scrollView.getChildAt(0).height
                val scrollViewHeight = scrollView.height
                val isAtBottom = scrollY >= contentHeight - scrollViewHeight

                swipeRefreshLayout.isEnabled =
                    isAtTop && !postRv.canScrollVertically(-1)

                if (isAtBottom) {
                    // Reached the bottom of the ScrollView
                    if (viewModel.isProgressBarTurning.value != true && viewModel.shouldVeil.value != true) {
                        viewModel.getPosts(
                            searchParams,
                            "onScrollStateChanged",
                            shouldVeil = false,
                            override = false
                        )
                    }

                }

            }
        }

        return binding.root
    }

    private fun refreshEveryView() {
        binding.apply {
            //default button
            if (selectedChipId != -1) {
                //unselect last selected chip
                val previousChip = requireActivity().findViewById<Chip>(selectedChipId)
                previousChip.apply {
                    isChecked = false
                    isEnabled = true
                }
                //select all chip
                selectedChipId = binding.all.id
                binding.all.apply {
                    isChecked = true
                    isEnabled = false
                }
            }

            //back to all countries
            //TODO maybe to change to the country auto detected
            if (countryEditText.text.toString().isNotEmpty()) {
                countryEditText.setText(countryAdapter?.getItem(0), false)
                countryAdapter?.filter?.filter(null)
            }

            //clear selection
            categoriesChipGroup.forEach { view ->
                val radioButton = view as RadioButton
                radioButton.isChecked = false
            }

        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.postRv.apply {
            setAdapter(postsAdapter)
            setLayoutManager(LinearLayoutManager(requireContext()))
            addVeiledItems(10)
        }
        binding.categoriesChipGroup.addVeilElements(10)
        binding.shimmerFrameLayout.startShimmer()

        viewModel.currentPage.observe(viewLifecycleOwner) { currentPage ->
            Log.d(TAG, "currentPage: $currentPage")
            searchParams.page = currentPage
        }

        binding.apply {
            (requireActivity() as MainActivity).countriesModel.countries.observe(viewLifecycleOwner) { data ->

                if (data != null) {
                    Countries.set(data)
                    postsAdapter.setCountriesData(data)
                    val countries = data.toMutableList().also {
                        it.add(0, CountriesDataItem(name = getString(R.string.all)))
                    }
                    binding.countryEditText.apply {

                        val adapter = setUpCountriesAndHandleSearch(countries)


                        isEnabled = data.isNotEmpty()
                        val elementToShow = countries[0]
                        setText(elementToShow.name, false)
                        setSelection(elementToShow.name!!.length)
                        adapter.filter.filter(null)

                        adapter.setOnItemClickListener { selectedItem ->
                            val name = selectedItem.name
                            binding.countryTextField.isEnabled = true

                            if (!name.isNullOrEmpty()) {
                                searchParams.setCountry(name)
                                setText(name.toString(), false)
                                setSelection(name.length)
                                adapter.filter.filter(null)
                                dismissDropDown()
                            } else {
                                searchParams.setCountry(null)
                            }
                            viewModel.getPosts(searchParams, "countryEditText changed")
                        }
                    }
                }

            }
            viewModel.liked.observe(viewLifecycleOwner) { message ->
                Log.d(TAG, "liked: $message")
                message?.apply { requestTheUser() }
            }

            viewModel.unliked.observe(viewLifecycleOwner) { message ->
                Log.d(TAG, "unliked: $message")
                message?.apply { requestTheUser() }
            }

            viewModel.user.observe(viewLifecycleOwner) { user ->
                Log.d(TAG, "user: $user")
                CurrentUser.set(user)
                if (user != null)
                    postsAdapter.setLiked(user.likes)
            }

            //handle error message
            viewModel.postsMessage.observe(viewLifecycleOwner) { postsMessage ->
                if (postsMessage.isEmpty()) {
                    this.postsMessage.visibility = View.GONE
                } else {
                    this.postsMessage.visibility = View.VISIBLE
                    this.postsMessage.text = postsMessage
                }

            }
            viewModel.categoriesMessage.observe(viewLifecycleOwner) { categoriesMessage ->
                if (categoriesMessage.isEmpty()) {
                    this.categoriesMessage.visibility = View.GONE
                } else {
                    binding.categoriesChipGroup.removeAllViews()
                    this.categoriesMessage.visibility = View.VISIBLE
                    this.categoriesMessage.text = categoriesMessage
                }
            }

            viewModel.shouldVeil.observe(viewLifecycleOwner) { shouldVeil ->
                Log.d(TAG, "shouldVeil: $shouldVeil")
                if (shouldVeil)
                    binding.postRv.veil()
                else
                    binding.postRv.unVeil()

            }

            //handle loading
            viewModel.isProgressBarTurning.observe(viewLifecycleOwner) { loading ->
                binding.progressBar.isVisible = loading
//                if (!loading)
//                    binding.postRv.unVeil()
//                if (loading && !binding.postRv.isVeiled) {
//                    binding.postRv.veil()
//                }
            }



            viewModel.categoriesList.observe(viewLifecycleOwner) { categories ->
                if (categories == null) return@observe
                binding.shimmerFrameLayout.hideShimmer()
                categoriesChipGroup.apply {
                    val list =
                        categories.sortedByDescending { it.length }

                    val categoriesToShow = list.sortToAdd()

                    removeAllViews()
                    fillWith(categoriesToShow)

                    forEach { view ->
                        val radioButton = view as RadioButton

                        radioButton.setOnClickListener {
                            val newSelectedOption = radioButton.text.toString()
                            Log.d(TAG, "text: $newSelectedOption")

                            if (radioButton == selectedOption) {
                                radioButton.isChecked = false
                                selectedOption = null

                                searchParams.category = null

                            } else {
                                selectedOption?.isChecked = false
                                selectedOption = radioButton

                                Log.d(TAG, "selectedCategory: $newSelectedOption")

                                searchParams.category = newSelectedOption
                            }
                            viewModel.getPosts(
                                searchParams,
                                source = "radioButton.setOnClickListener"
                            )
                        }
                    }
                }
            }

            viewModel.postsList.observe(viewLifecycleOwner) { posts ->

                handleHomeButton()

                when (searchParams.price) {
                    PriceFilter.DOWN -> {
                        posts?.sortBy { it.price }
                        searchParams.price = PriceFilter.NONE
                    }
                    PriceFilter.UP -> {
                        posts?.sortByDescending { it.price }
                        searchParams.price = PriceFilter.NONE
                    }
                    else -> {
                        //do nothing
                    }
                }


                posts?.apply {
                    Log.d(TAG, "posts: $posts")

//                    recyclerViewState =
//                        binding.postRv.getRecyclerView().layoutManager?.onSaveInstanceState()
                    postsAdapter.setPostsList(posts)
                    binding.postRv.getRecyclerView().layoutManager?.onRestoreInstanceState(
                        recyclerViewState
                    )

                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

    }

    private fun requestTheUser() {
        viewModel.getAuth()
    }

    private fun handleSearch() {
        binding.searchView.apply {

            //search by user input
            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchByQuery(query, "onQueryTextSubmit")
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (firstTime) {
                        firstTime = false
                    } else {
                        if (newText.isNullOrBlank()) {
                            searchParams.title = null
                            viewModel.getPosts(searchParams, source = "onQueryTextChange")
                        }
                    }



                    return false
                }

                fun searchByQuery(query: String?, source: String) {
                    if (query.isNullOrBlank())
                        searchParams.title = null
                    else
                        searchParams.title = query

                    viewModel.getPosts(searchParams, source = source)
                }
            })
        }

    }


    private fun handleHomeButton() {
        (requireActivity() as MainActivity).bottomNavView.setOnItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    binding.scrollView.scrollTo(0, 0)
                }
            }
        }
    }


    private fun ViewGroup.fillWith(categories: List<String>) {

        categories.forEach { category ->
            val radioButton = RadioButton(context)
            radioButton.apply {
                text = category
                id = ViewCompat.generateViewId()
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(5, 5, 5, 5)
            }
            addView(radioButton)
        }
    }

    private fun ViewGroup.addVeilElements(number: Int) {
        for (i in 0..number) {
            val veiledChip = ChipVeilledBinding.inflate(layoutInflater)
            val randomText = RandomGenerator.generateRandomEmptyString(20, 30)
            veiledChip.chipVeiled.text = randomText
            addView(veiledChip.root)
        }
    }

    private fun onChipClicked(view: View, isUserClick: Boolean) {
        val chipId = view.id

        // Unselect previously selected chip if any
        if (selectedChipId != -1) {
            val previousChip = requireActivity().findViewById<Chip>(selectedChipId)
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
                searchParams.type = null
            }
            binding.rent.id -> {
                searchParams.type = Type.RENT.value
            }
            binding.buy.id -> {
                searchParams.type = Type.BUY.value
            }
        }

        if (isUserClick && chipId != binding.wanted.id)
            viewModel.getPosts(searchParams, source = "onChipClicked")
    }

    override fun onResume() {
        super.onResume()
    }

    private fun handleChips() {

        for (chip in binding.chips.children) {
            if (chip.id == R.id.wanted) {
                chip.setOnClickListener {
                    (chip as Chip).isChecked = false
                    goToActivity<ActivityWanted>(requireContext())
                }
                continue
            }
            chip.setOnClickListener {
                isUserClick = true
                if (selectedChipId == chip.id) {
                    // Chip is already selected, do nothing
                    Log.d(TAG, "onChipClicked already selected")
                    chip.isEnabled = false
                    return@setOnClickListener
                }
                onChipClicked(it, isUserClick)
                isUserClick = false
            }
        }
    }

    override fun onResultOk(searchParams: SearchParams) {
        Log.d(TAG, "onResultOk params: $searchParams")
        this.searchParams = searchParams
        initialiseTypeChips(searchParams.type)

        val selected = binding.categoriesChipGroup.initialiseCategoryButtons(
            searchParams.category,
            selectedOption,
            TAG
        )
        selected?.apply {
            if (selectedOption != this) {
                selectedOption?.isChecked = false
                selectedOption = this
            }
        }

        initialiseCountryPicker(searchParams.location?.country)

        requestData(this.searchParams)
    }

    private fun initialiseCountryPicker(countryData: CountriesDataItem?) {
//        val code = countryData?.code
        val country = countryData?.name
        if (country == null) {
            binding.countryEditText.setText(getString(R.string.all))
        } else {
            binding.countryEditText.setText(country)
        }
        countryAdapter?.filter?.filter(null)
    }

    private fun requestData(params: SearchParams) {
        Log.d(TAG, "requesting Data with params: $params")
        //initialise chips
        viewModel.getPosts(params, source = "onResultOk requestData")
    }

    override fun onResultCancelled() {
        Log.d(TAG, "onResultCancelled")
    }

    private fun goToPostFragment(postId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToPostNav(postId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerViewState =
            binding.postRv.getRecyclerView().layoutManager?.onSaveInstanceState()
    }

    private fun initialiseTypeChips(type: String?) {
        when (type) {
            Type.RENT.value -> {
                if (selectedChipId != binding.rent.id) {
                    binding.rent.performClick()
                }
            }
            Type.BUY.value -> {
                if (selectedChipId != binding.buy.id) {
                    binding.buy.performClick()
                }
            }
            null -> {
                if (selectedChipId != binding.all.id) {
                    binding.all.performClick()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        firstTime = true
    }
}