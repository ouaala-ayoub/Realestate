package com.example.realestate.ui.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.realestate.data.models.SearchParams
import com.example.realestate.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SearchActivity"
    }

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)


        val params = intent.getParcelableExtra<SearchParams>("search_params")
        Log.d(TAG, "params: $params")

        params?.apply {
            Log.d(TAG, "params extra: $params")
            initialiseViews(this)
        }

        binding.search.setOnClickListener {
            //get the search params
            val searchParams = SearchParams(
                title = (0..10).random().toString()
            )
            intent.putExtra("search_params", searchParams)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        setContentView(binding.root)
    }

    private fun initialiseViews(searchParams: SearchParams) {
        //initialisation logic
    }
}