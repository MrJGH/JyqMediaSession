package com.example.jyqmediasession

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yq.mediaSession.mediacompat.MusicPlayManager2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     /*   val startService = startService(Intent().apply {
            setClassName("com.example.jyqmediasession", "com.yq.mediaSession.service.MyMusicService")
        })*/
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.back).setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
//        MusicPlayManager2.getInstance(this).release()
    }
}