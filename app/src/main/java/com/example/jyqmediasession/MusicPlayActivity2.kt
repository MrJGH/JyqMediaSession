package com.example.jyqmediasession

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yq.mediaSession.mediacompat.MusicPlayManager2

/**
 *
 * @author: jyq
 * @desc: android4.4以上 音乐播放器
 * @date: 2025/5/7
 */
class MusicPlayActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_play2)
        MusicPlayManager2.getInstance().init(this)

        findViewById<Button>(R.id.back).setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayManager2.getInstance().release()
    }
}