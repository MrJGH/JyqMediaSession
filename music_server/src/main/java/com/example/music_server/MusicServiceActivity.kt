package com.example.music_server

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MusicServiceActivity : AppCompatActivity() {
    private val TAG = "MusicServiceActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_start_service).setOnClickListener {
            try {
                startService(Intent().apply {
                    setClassName("com.example.music_server", "com.example.service.MyMusicService")
                })
                Log.d(TAG, "onCreate: MyMusicService 开启成功")
            } catch (e: Exception) {
                Log.e(TAG, "onCreate: MyMusicService 开启失败", e)

            }

        }
    }
}