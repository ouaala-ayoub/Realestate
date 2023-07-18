package com.example.realestate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.realestate.R
import com.example.realestate.data.models.CurrentUser
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.remote.network.Retrofit
import com.example.realestate.data.repositories.PostsRepository
import com.example.realestate.data.repositories.StaticDataRepository
import com.example.realestate.data.repositories.UsersRepository
import com.example.realestate.databinding.ActivityMainBinding
import com.example.realestate.ui.viewmodels.HomeViewModel
import com.example.realestate.utils.ActivityResultListener
import com.example.realestate.utils.SelectionResult
import com.example.realestate.utils.SessionCookie
import com.example.realestate.utils.startActivityResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), ActivityResultListener {

    init {

    }

    companion object {
        private const val TAG = "MainActivity"
    }

    var params: SearchParams = SearchParams()
    val viewModel: HomeViewModel by lazy {
        val retrofit = Retrofit.getInstance()
        HomeViewModel(
            PostsRepository(retrofit),
            StaticDataRepository(retrofit),
            UsersRepository(retrofit)
        ).also {
            val connected = CurrentUser.isConnected()
            if (connected)
                it.getUserById(CurrentUser.prefs.get()!!)

            it.getCategories()
            it.getPosts(source = "onCreate activity")
        }
    }
    private lateinit var resultListener: ActivityResultListener
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        drawerLayout = binding.drawerLayout
        bottomNavView = binding.bottomNav

        supportActionBar

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
                //TODO
//                openSearchActivity()
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
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("search_params", params)
        searchLauncher.launch(intent)
    }

    private fun setTheBottomNav() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            val userConnected = CurrentUser.isConnected()
            Log.d(TAG, "CurrentUser.prefs.get() : ${CurrentUser.prefs.get()}")
            when (menuItem.itemId) {
                R.id.addPostActivity -> {
                    //to change
                    if (userConnected) {
                        // User is connected, open PostAddActivity
                        navController.navigate(R.id.addPostActivity)
                    } else {
                        // User is not connected, open UserRegisterActivity
                        navController.navigate(R.id.userRegisterActivity)
                    }
                }
                R.id.likedFragment -> {
                    if (userConnected) {
                        // User is connected, open savedFragment
                        navController.navigate(R.id.likedFragment)
                    } else {
                        // User is not connected, open UserRegisterActivity
                        navController.navigate(R.id.userRegisterActivity)
                    }
                }
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                }
                // Handle other menu items if needed
            }
            true
        }
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

        val view = binding.navView.getHeaderView(0)
        val phoneTv = view.findViewById<TextView>(R.id.user_phone)

        handleUser(FirebaseAuth.getInstance().currentUser, phoneTv)
        FirebaseAuth.getInstance().currentUser

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            handleUser(auth.currentUser, phoneTv)
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    logout()
                }
            }
            true
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun handleUser(user: FirebaseUser?, phoneTv: TextView) {
        Log.d(TAG, "handleUser user ${user?.phoneNumber}")
        if (user != null) {
            phoneTv.text = user.phoneNumber
        } else {
            phoneTv.text = getString(R.string.no_user)
        }
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
        CurrentUser.prefs.delete()
    }

}