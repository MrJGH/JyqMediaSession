package com.example.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat

class MyMusicService : MediaBrowserServiceCompat() {
    private var mediaSession: MediaSessionCompat? = null
    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MusicServiceSession")
        mediaSession?.isActive = true
        mediaSession?.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                Log.d(TAG, "onPlay: ")
            }

            override fun onPause() {
                super.onPause()
                Log.d(TAG, "onPause: ")
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                Log.e(TAG, "onSkipToNext: ")
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                Log.d(TAG, "onSkipToPrevious: ")
            }
        })
        sessionToken = mediaSession?.sessionToken // 关键步骤
        Log.e(TAG, "onCreate: mediaSession$mediaSession")
    }

    // 用于媒体浏览结构的节点定义（可忽略实现，仅需连接）
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        Log.e(TAG, "onGetRoot: clientPackageName=$clientPackageName clientUid=$clientUid")
        return BrowserRoot("media_root", null)
    }

    override fun onLoadChildren(parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null) // 示例不加载内容
    }

    companion object {
        private const val TAG = "MyMusicService"
    }
}