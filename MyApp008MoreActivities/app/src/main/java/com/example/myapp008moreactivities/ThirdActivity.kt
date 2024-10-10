package com.example.myapp008moreactivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp008moreactivities.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Third Activity"

        val nickname = intent.getStringExtra("NICK_NAME")
        binding.twInfo.text = "Data from previous activities. Name: $nickname"

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
