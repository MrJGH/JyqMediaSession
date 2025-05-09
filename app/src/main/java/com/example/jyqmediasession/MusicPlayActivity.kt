package com.example.jyqmediasession

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @author: jyq
 * @desc: android5.0以上 音乐播放器
 * @date: 2025/5/7
 */
class MusicPlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play)
        findViewById<Button>(R.id.back).setOnClickListener { finish() }
    }
}