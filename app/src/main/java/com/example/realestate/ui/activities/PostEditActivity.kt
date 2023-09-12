package com.example.realestate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.realestate.databinding.ActivityAddPostBinding
import com.example.realestate.databinding.ActivityPostEditBinding

class PostEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}