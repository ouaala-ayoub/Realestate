package com.example.realestate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.realestate.databinding.ActivityWantedBinding

class ActivityWanted : AppCompatActivity() {

    private lateinit var binding: ActivityWantedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWantedBinding.inflate(layoutInflater)



        setContentView(binding.root)
    }
}