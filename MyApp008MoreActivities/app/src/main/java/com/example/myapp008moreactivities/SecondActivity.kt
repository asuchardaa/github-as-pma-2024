package com.example.myapp008moreactivities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp008moreactivities.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Second Activity"

        val nickname = intent.getStringExtra("NICK_NAME")
        binding.twInfo.text = "Data from first activity. Name: $nickname"

        binding.btnThird.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("NICK_NAME", nickname)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
