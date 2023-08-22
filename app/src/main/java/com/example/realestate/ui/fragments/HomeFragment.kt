package com.example.realestate.ui.fragments

import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.realestate.R
import com.example.realestate.data.models.CountriesDataItem
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.Type
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.ChipVeilledBinding
import com.example.realestate.databinding.FragmentHomeModifiedBinding
import com.example.realestate.databinding.SingleCategoryChipBinding
import com.example.realestate.ui.activities.MainActivity
import com.example.realestate.ui.adapters.PostsAdapter
import com.example.realestate.ui.viewmodels.HomeViewModel
import com.example.realestate.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class HomeFragment : Fragment(), ActivityResultListener {

    companion object {
        private const val TAG = "HomeFragment"
        var count = 0
    }

    private var isUserClick = false
    private var firstTime = true
    private var recyclerViewState: Parcelable? = null
    private lateinit var binding: FragmentHomeModifiedBinding
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var searchParams: SearchParams
    private var selectedChipId: Int = -1
    val viewModel: HomeViewModel by lazy {
        val retrofit = Retrofit.getInstance()
        HomeViewModel(
            PostsRepository(retrofit),
            StaticDataRepository(retrofit),
            UsersRepository(retrofit)
        ).also {
            it.apply {
                if (CurrentUser.isUserIdStored() && !CurrentUser.isConnected())
                    getUserById(CurrentUser.prefs.get()!!)

                getCountries()
                getCategories()
                getPosts(source = "lazy")
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
                override fun onClick(postId: String) {
                    goToPostFragment(postId)
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
                        separateButtonsBy(10)
                    }

                }

            })

        binding = FragmentHomeModifiedBinding.inflate(inflater, container, false)
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
                viewModel.getPosts(searchParams, source = "swipeRefreshLayout.setOnRefreshListener")
                if (viewModel.categoriesList.value.isNullOrEmpty()) {
                    viewModel.getCategories()
                }
            }

            val layoutManager = postRv.getRecyclerView().layoutManager
            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) &&
                        newState == RecyclerView.SCROLL_STATE_IDLE &&
                        !postsAdapter.isListEmpty()
                    ) {
//                        viewModel.getPosts(searchParams, "onScrollStateChanged", false)
                        Log.d(TAG, "got to the bottom")
                    }
                }
            }
//            postRv.getRecyclerView().addOnScrollListener(scrollListener)
//            scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//
//            }

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
                    // Perform your action here
                    if (viewModel.isProgressBarTurning.value != true) {
                        viewModel.getPosts(searchParams, "onScrollStateChanged", false)
                    }

                }

            }
            //get the current country and send the request to get the posts of this country
//            countryPicker.setOnCountryChangeListener {
//                // your code to handle selected country
//                countryPicker.selectedCountryName.apply {
//                    searchParams.location?.country?.name = this
//                    viewModel.getPosts(searchParams, source = "countryPicker")
//                }
//                val code = countryPicker.selectedCountryNameCode
//                searchParams.location?.country?.code = code
//            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.postRv.apply {
            setAdapter(postsAdapter)
            setLayoutManager(LinearLayoutManager(requireContext()))
            addVeiledItems(10)
        }
        binding.categoriesChipGroup.addVeilElements(5)
        binding.shimmerFrameLayout.startShimmer()

        viewModel.currentPage.observe(viewLifecycleOwner) { currentPage ->
            Log.d(TAG, "currentPage: $currentPage")
            searchParams.page = currentPage
        }

        binding.apply {
            viewModel.countries.observe(viewLifecycleOwner) { data ->
                if (data != null) {
                    val countries = data.map { element -> element.name }.toMutableList().also {
                        it.add(0, "All")
                    }
                    binding.countryEditText.apply {
                        setText(countries[0])
                        val adapter = setUpAndHandleSearch(countries)
                        setOnItemClickListener { _, view, i, _ ->
                            val tv = view as TextView
                            val query = tv.text.toString()
                            adapter.filter.filter(null)

                            if (query.isNotEmpty()) {
                                if (query == "All") {
                                    searchParams.setCountry(null)
                                } else {
                                    searchParams.setCountry(query)
                                    searchParams.location?.country?.code = data[i - 1].code
                                }
                            } else
                                searchParams.setCountry(null)

                            viewModel.getPosts(searchParams, "countryEditText changed")
                        }
                    }
                }

            }
            viewModel.liked.observe(viewLifecycleOwner) { message ->
                if (message == null)
                    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
                else {
                    requestTheUser()
                }
            }

            viewModel.unliked.observe(viewLifecycleOwner) { message ->
                if (message == null)
                    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
                else {
                    requestTheUser()
                }
            }

            viewModel.user.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    if (!CurrentUser.isConnected()) {
                        CurrentUser.set(user)
                    }
//                    user.likes = listOf()
                    postsAdapter.setLiked(user.likes)
                } else {
                    requireContext().toast(getString(R.string.error), Toast.LENGTH_SHORT)
                }
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
                    this.categoriesMessage.visibility = View.VISIBLE
                    this.categoriesMessage.text = categoriesMessage
                }
            }

            //handle loading
            viewModel.isProgressBarTurning.observe(viewLifecycleOwner) { loading ->
                binding.progressBar.isVisible = loading
//                if (loading && !binding.postRv.isVeiled) {
//                    binding.postRv.veil()
//                }
            }



            viewModel.categoriesList.observe(viewLifecycleOwner) { categories ->
                if (categories == null) return@observe
                categoriesChipGroup.apply {
                    removeAllViews()
                    binding.shimmerFrameLayout.hideShimmer()
                    fillWith(categories)
                    setOnCheckedStateChangeListener { group, checkedId ->
                        if (checkedId.isEmpty()) {
                            searchParams.category = null
                            viewModel.getPosts(
                                searchParams,
                                source = "setOnCheckedStateChangeListener isEmpty = true"
                            )
                        } else {
                            val selectedChip: Chip? = group.findViewById(checkedId[0])
                            val selectedCategory: String? = selectedChip?.text?.toString()

                            searchParams.category = selectedCategory
                            viewModel.getPosts(
                                searchParams,
                                source = "setOnCheckedStateChangeListener isEmpty = false"
                            )

                        }
                    }
                }
            }

            viewModel.postsList.observe(viewLifecycleOwner) { posts ->

                handleHomeButton()

                posts?.apply {

//                    recyclerViewState =
//                        binding.postRv.getRecyclerView().layoutManager?.onSaveInstanceState()
                    postsAdapter.setPostsList(posts)
                    binding.postRv.getRecyclerView().layoutManager?.onRestoreInstanceState(
                        recyclerViewState
                    )
                    binding.postRv.unVeil()

                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

    }

    private fun requestTheUser() {
        val connected = CurrentUser.isUserIdStored()
        if (connected)
            viewModel.getUserById(CurrentUser.prefs.get()!!)
    }

    private fun handleSearch() {
        binding.searchView.apply {

            //search by user input
            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.apply {
                        searchByQuery(query, "onQueryTextSubmit")
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (firstTime) {
                        firstTime = false
                    } else {
                        viewModel.apply {
                            searchByQuery(newText, "onQueryTextChange")
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


    private fun ChipGroup.fillWith(categories: List<String>) {

        categories.forEach { category ->
            val chipLayout = SingleCategoryChipBinding.inflate(layoutInflater, this, false)
            chipLayout.chip.apply {
                text = category
                id = ViewCompat.generateViewId()
            }
            addView(chipLayout.root)
        }
    }

    private fun ChipGroup.addVeilElements(number: Int) {
        for (i in 0..number) {
            val veiledChip = ChipVeilledBinding.inflate(layoutInflater)
            val randomText = RandomGenerator.generateRandomEmptyString(10, 20)
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

        if (isUserClick)
            viewModel.getPosts(searchParams, source = "onChipClicked")
    }


    private fun handleChips() {

        for (chip in binding.chips.children) {
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

        binding.categoriesChipGroup.initialiseCategoryChip(searchParams.category, TAG)

        initialiseCountryPicker(searchParams.location?.country)

        requestData(this.searchParams)
    }

    private fun initialiseCountryPicker(countryData: CountriesDataItem?) {
        //TODO fix this shit
        val code = countryData?.code
        Log.i(TAG, "code: $code")
        val country = countryData?.name
        if (country == null) {
            binding.countryEditText.setText("All")
        } else {
            binding.countryEditText.setText(country)
        }
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