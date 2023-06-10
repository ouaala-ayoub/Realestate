package com.example.realestate.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.realestate.R
import com.example.realestate.data.models.SearchParams
import com.example.realestate.data.models.Type
import com.example.realestate.databinding.ActivityMainBinding
import com.example.realestate.utils.ActivityResultListener
import com.example.realestate.utils.SelectionResult
import com.example.realestate.utils.startActivityResult

class MainActivity : AppCompatActivity(), ActivityResultListener {

    private lateinit var resultListener: ActivityResultListener
    var params: SearchParams = SearchParams(type = Type.RENT.value)

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
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
        drawerLayout = binding.drawerLayout

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
            R.id.app_bar_search -> {
                openSearchFragment()
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

    private fun openSearchFragment() {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("search_params", params)
        searchLauncher.launch(intent)
    }

    private fun setTheBottomNav() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Update the selected item in the bottom navigation bar based on the current destination
            val destinationId = destination.id
            binding.bottomNav.menu.findItem(destinationId)?.isChecked = true
        }
    }

    private fun initialiseDrawerLayout(drawerLayout: DrawerLayout) {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.nav_open, R.string.nav_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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

}