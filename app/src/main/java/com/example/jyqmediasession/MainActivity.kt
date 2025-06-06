package com.example.jyqmediasession

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.back).setOnClickListener { finish() }
        findViewById<Button>(R.id.button_1).setOnClickListener {
            startActivity(Intent(this, MusicPlayActivity::class.java))
        }
        findViewById<Button>(R.id.button_2).setOnClickListener {
            startActivity(Intent(this, MusicPlayActivity2::class.java))
        }
    }
}