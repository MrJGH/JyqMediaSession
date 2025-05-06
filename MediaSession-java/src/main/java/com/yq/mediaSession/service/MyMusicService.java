package com.yq.mediaSession.service;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.media.MediaBrowserServiceCompat;

import java.util.List;

public class MyMusicService extends MediaBrowserServiceCompat {
    private static final String TAG = "MyMusicService";
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSessionCompat(this, "MusicServiceSession");
        mediaSession.setActive(true);

        setSessionToken(mediaSession.getSessionToken()); // 关键步骤
        Log.e(TAG, "onCreate: mediaSession" + mediaSession);
    }

    // 用于媒体浏览结构的节点定义（可忽略实现，仅需连接）
    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        Log.e(TAG, "onGetRoot: clientPackageName=" + clientPackageName + " clientUid=" + clientUid);
        return new BrowserRoot("media_root", null);
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null); // 示例不加载内容
    }
}
