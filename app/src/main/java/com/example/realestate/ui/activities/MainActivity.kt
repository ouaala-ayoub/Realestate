package com.example.realestate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.realestate.R
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.OnChanged
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.User
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.databinding.ActivityMainBinding
import com.example.realestate.ui.fragments.HomeFragmentDirections
import com.example.realestate.ui.viewmodels.CountriesModel
import com.example.realestate.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), ActivityResultListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    var params: SearchParams = SearchParams()
    private lateinit var resultListener: ActivityResultListener
    val countriesModel: CountriesModel = CountriesModel(
        StaticDataRepository(com.example.realestate.data.remote.network.Retrofit.getInstance())
    )
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var profileImage: ImageView
    private lateinit var navController: NavController
    lateinit var bottomNavView: BottomNavigationView
    private val searchLauncher = startActivityResult(
        object : SelectionResult {
            override fun onResultOk(data: Intent) {
                val searchParams = data.getParcelableExtra<SearchParams>("search_params")

                // Use the search parameters as needed
                if (searchParams != null) {
                    // Perform the search using the search parameters
                    // ...
                    params = searchParams
                    backToHomeFragment()
                    resultListener.onResultOk(params)

                    Log.d(TAG, "searchParams result : $searchParams")
                }
            }

            override fun onResultFailed() {
                resultListener.onResultCancelled()
            }

        }
    )
    private val phoneVerificationLauncher = startActivityResult(object : SelectionResult {
        override fun onResultOk(data: Intent) {
            val phoneVerified = data.getBooleanExtra("phone_verified", false)
            Log.i(TAG, "phoneVerified: $phoneVerified")

            if (phoneVerified) {
                findNavController(R.id.fragment_container).navigate(R.id.addPostActivity)
            } else {
                toast("phone required", Toast.LENGTH_SHORT)
            }
        }

        override fun onResultFailed() {
            Log.d(TAG, "onResultFailed()")
            toast("phone required", Toast.LENGTH_SHORT)
        }

    })
    private val registerForLikesLauncher = registerAnd(object : SelectionResult {
        override fun onResultOk(data: Intent) {
            findNavController(R.id.fragment_container).navigate(R.id.likedFragment)
        }

        override fun onResultFailed() {}

    })
    val registerForPostAddLauncher = registerAnd(object : SelectionResult {
        override fun onResultOk(data: Intent) {
            findNavController(R.id.fragment_container).navigate(R.id.addPostActivity)
        }

        override fun onResultFailed() {}

    })

    private val registerForProfileLauncher = registerAnd(object : SelectionResult {
        override fun onResultOk(data: Intent) {
            findNavController(R.id.fragment_container).navigate(R.id.postEditActivity)
        }

        override fun onResultFailed() {}

    })

    private fun registerAnd(selectionResult: SelectionResult) =
        startActivityResult(object : SelectionResult {
            override fun onResultOk(data: Intent) {
                val registerSuccess = data.getBooleanExtra("register_success", false)

                Log.i(TAG, "registerSuccess: $registerSuccess")


                if (registerSuccess) {
                    selectionResult.onResultOk(data)
                } else {
                    toast("please register first", Toast.LENGTH_SHORT)
                }
            }

            override fun onResultFailed() {
                Log.d(TAG, "onResultFailed()")
                toast("please register first", Toast.LENGTH_SHORT)
                selectionResult.onResultFailed()
            }

        })

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val view = binding.navView.getHeaderView(0)
        val data = intent.data


        drawerLayout = binding.drawerLayout
        bottomNavView = binding.bottomNav
        profileImage = view.findViewById(R.id.user_image)
        navController = navHost.navController

        //check for deep link
        //TODO not working to fix later
        if (data != null) {
            val postId = data.lastPathSegment // Extract the post ID from the URL
            // Use postId to navigate to the specific post within your app
            postId?.apply {
                val action = HomeFragmentDirections.actionHomeFragmentToPostNav(this)
                navController.navigate(action)
            }
        }

        initialiseDrawerLayout(drawerLayout)
        setTheBottomNav()

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_bar_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                handleDrawerLayout(drawerLayout)
                return true
            }
            R.id.filter_button -> {
                openSearchActivity()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    private fun backToHomeFragment() {
        val navController = findNavController(R.id.fragment_container)
        navController.apply {
            popBackStack(R.id.homeFragment, false)
        }
    }

    private fun openSearchActivity() {
        val intent = Intent(this, FilterActivity::class.java)
        intent.putExtra("search_params", params)
        searchLauncher.launch(intent)
    }

    private fun handleAddPost(userConnected: Boolean) {
        if (userConnected) {
            navController.navigate(R.id.addPostActivity)
        } else {
            launchRegisterProcess(registerForPostAddLauncher)
        }
    }

    private fun setTheBottomNav() {
        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            val userConnected = CurrentUser.isConnected()
//            val userConnected = true
            val currentDestination = navController.currentDestination

            when (menuItem.itemId) {
                R.id.addPostActivity -> {
                    handleAddPost(userConnected)
                }
                R.id.likedFragment -> {
                    if (userConnected) {
                        // User is connected, open savedFragment
                        if (currentDestination?.id != R.id.likedFragment) {
                            val fragmentInBackStack =
                                navController.popBackStack(R.id.likedFragment, false)
                            if (!fragmentInBackStack) {
                                navController.navigate(R.id.likedFragment)
                            }
                        }
                    } else {
                        // User is not connected, open UserRegisterActivity
                        launchRegisterProcess(registerForLikesLauncher)
                    }
                }
                R.id.homeFragment -> {
                    if (currentDestination?.id != R.id.homeFragment) {
                        val fragmentInBackStack =
                            navController.popBackStack(R.id.homeFragment, false)
                        if (!fragmentInBackStack) {
                            navController.navigate(R.id.homeFragment)
                        }
                    }
//                    navController.navigate(R.id.homeFragment)
                }
                // Handle other menu items if needed
            }
            true
        }
    }

    fun launchRegisterProcess(
        registerLauncher: ActivityResultLauncher<Intent>
    ) {
        val userRegisterIntent = Intent(this, UserRegisterActivity::class.java)
        registerLauncher.launch(userRegisterIntent)
    }

    private fun launchRegisterProcess() {
        val userRegisterIntent = Intent(this, UserRegisterActivity::class.java)
        startActivity(userRegisterIntent)
    }

    override fun onResume() {
        super.onResume()


        // Manually set the selected item in the bottom navigation view based on the current destination
        val navDestination = findNavController(R.id.fragment_container).currentDestination
        when (navDestination?.id) {
            R.id.homeFragment -> binding.bottomNav.selectedItemId = R.id.homeFragment
//            R.id.postPageFragment -> binding.bottomNav.selectedItemId = R.id.homeNav
//            R.id.reportFragment -> binding.bottomNav.selectedItemId = R.id.homeNav
            R.id.likedFragment -> binding.bottomNav.selectedItemId = R.id.likedFragment
            // Add cases for other destinations if needed
        }
    }

    private fun initialiseDrawerLayout(drawerLayout: DrawerLayout) {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.nav_open, R.string.nav_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        val navItem = binding.navView.menu.findItem(R.id.connected_state)
        val view = binding.navView.getHeaderView(0)
        val emailTv = view.findViewById<TextView>(R.id.user_email)
        val imageView = view.findViewById<ImageView>(R.id.user_image)
//        val phoneTv = view.findViewById<TextView>(R.id.user_phone)

        CurrentUser.observe(this, object : OnChanged<User> {
            override fun onChange(data: User?) {
                Log.d(TAG, "user: ${data?.email}")
                val imageUri = data?.image
                if (imageUri != null) {
                    imageView.loadImage(imageUri)
                } else {
                    profileImage.setImageResource(R.drawable.baseline_person_24)
                }
                if (data != null) {
                    emailTv.text = data.email
                    navItem.apply {
                        setTitle(R.string.logout)
                        setIcon(R.drawable.baseline_logout_24)
                    }
                } else {
                    emailTv.text = getString(R.string.no_user)
                    navItem.apply {
                        setTitle(R.string.sign_up) // Set text for logged-in state
                        setIcon(R.drawable.baseline_assignment_ind_24)
                    }
                }
            }

        })

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    if (CurrentUser.isConnected()) {
                        navController.navigate(R.id.postEditActivity)
                    } else {
                        launchRegisterProcess(registerForProfileLauncher)
                    }
                }
                R.id.add_post -> {
                    handleAddPost(CurrentUser.isConnected())
                }
                R.id.website -> {
                    openTheWebsite(getString(R.string.real_estate_website))
                }
                R.id.instagram -> {
                    openInstagramPage(getString(R.string.real_estate_instagram))
                }
                R.id.connected_state -> {
                    if (CurrentUser.isConnected()) {
                        logout()
                    } else {
                        launchRegisterProcess()
                    }
                }
            }
            true
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun handleDrawerLayout(drawerLayout: DrawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onResultOk(searchParams: SearchParams) {
        resultListener.onResultOk(searchParams)
    }

    override fun onResultCancelled() {
        resultListener.onResultCancelled()
    }

    fun setActivityResultListener(listener: ActivityResultListener) {
        resultListener = listener
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        SessionCookie.prefs.delete()
        CurrentUser.logout()
    }

    private fun launchPhoneAddProcess() {
        val addPhoneIntent = Intent(this, PhoneAddActivity::class.java)
        phoneVerificationLauncher.launch(addPhoneIntent)
    }

    override fun onStop() {
        super.onStop()

        Glide.with(this).clear(profileImage)
    }

}