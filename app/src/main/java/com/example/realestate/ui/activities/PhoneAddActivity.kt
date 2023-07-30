package com.example.realestate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.realestate.databinding.ActivityPhoneAddBinding

class PhoneAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAddBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}