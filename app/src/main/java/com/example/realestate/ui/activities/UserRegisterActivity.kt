package com.example.realestate.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.realestate.R
import com.example.realestate.databinding.ActivityUserRegisterBinding
import com.google.firebase.FirebaseApp

class UserRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityUserRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}