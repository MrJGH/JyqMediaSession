package com.yq.mediaSession.mediacompat;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yq.mediaSession.media.HeadsetReceiver;

import java.util.List;

/**
 * @author: jyq
 * @desc: 客户端控制器
 * @date: 2025/4/21
 */
public class MusicPlayManager2 {
    private static final String TAG = "MusicPlayManager2";
    private static volatile MusicPlayManager2 instance;
    private Context context;
    private HeadsetReceiver headsetReceiver;
    private MediaControllerCompat mTargetController = null;
    private static final String ID = "jxw_widget_media_root_id";
    private MediaBrowserCompat mMediaBrowser;
    //    private final String SERVER_PACKAGE = "com.example.music_server"; // 替换为服务端包名
//    private final String SERVER_SERVICE = "com.example.service.MyMusicService"; // 替换服务端全类名
    private final String SERVER_PACKAGE = "com.kugou.android.watch.lite"; // 替换为服务端包名
    private final String SERVER_SERVICE = "com.kugou.android.watch.lite.service.JXWWidgetMediaService"; // 替换服务端全类名
    private boolean isInitialized = false;

    public static MusicPlayManager2 getInstance() {
        if (instance == null) {
            synchronized (MusicPlayManager2.class) {
                if (instance == null) {
                    instance = new MusicPlayManager2();
                }
            }
        }
        return instance;
    }

    /**
     * 对外暴漏的初始化的函数 ，在适当的地方调用，一般在 开启服务之后
     * @param context
     */
    public void init(Context context) {
        this.context = context.getApplicationContext();
        connect();
        if (isInitialized) return;
        isInitialized = true;
        registerHeadsetReceiver();
    }

    /**
     * 注册耳机插拔广播
     */
    private void registerHeadsetReceiver() {
        headsetReceiver = new HeadsetReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(headsetReceiver, filter);
    }

    private void connect() {
        Log.e(TAG, "connect:  开始服务连接 mediaBrowser=" + mMediaBrowser);
        if (mMediaBrowser == null) {
            Bundle bundle = new Bundle();
            bundle.putString(ID, "jy123");
            mMediaBrowser = new MediaBrowserCompat(context, new ComponentName(SERVER_PACKAGE, SERVER_SERVICE), connectionCallback, bundle);
        }
        Log.e(TAG, "connect:  开始服务连接 mediaBrowser.isConnected()=" + mMediaBrowser.isConnected() + " threadName=" + Thread.currentThread().getName());
        if (!mMediaBrowser.isConnected()) {
            mMediaBrowser.connect();
        }
    }

    private void disconnect() {
        Log.e(TAG, "disconnect:  断开服务连接 mediaBrowser=" + mMediaBrowser);
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.disconnect();
        }
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            try {
                Log.e(TAG, "服务连接成功 SessionToken:" + mMediaBrowser.getSessionToken());
                mTargetController = new MediaControllerCompat(context, mMediaBrowser.getSessionToken());
                MediaControllerTracker2.getInstance().init(context, mTargetController);
                mMediaBrowser.subscribe(ID, subscriptionCallback);
            } catch (Exception e) {
                Log.e(TAG, "服务连接失败", e);
            }
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.e(TAG, "onConnectionFailed: 服务连接失败");
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.e(TAG, "onConnectionSuspended: 服务连接失败");
        }
    };
    private final MediaBrowserCompat.SubscriptionCallback subscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            Log.d(TAG, "onChildrenLoaded for parentId: " + parentId + ", count: " + children.size());
        }

        @Override
        public void onError(@NonNull String parentId) {
            Log.e(TAG, "onError for parentId: " + parentId);
        }
    };

    public void play() {
        Log.e(TAG, "play 点击了播放 targetController=" + mTargetController);
        if (mTargetController != null) {
            mTargetController.getTransportControls().play();
        }
    }

    public void pause() {
        Log.e(TAG, "pause 点击了暂停 targetController=" + mTargetController);
        if (mTargetController != null) {
            mTargetController.getTransportControls().pause();
        }
    }

    public void next() {
        Log.e(TAG, "next 点击了下一首 targetController=" + mTargetController);
        if (mTargetController != null) {
            mTargetController.getTransportControls().skipToNext();
        }
    }

    public void previous() {
        Log.e(TAG, "previous 点击了上一首 targetController=" + mTargetController);
        if (mTargetController != null) {
            mTargetController.getTransportControls().skipToPrevious();
        }
    }

    public boolean isPlaying() {
        if (mTargetController != null && mTargetController.getPlaybackState() != null) {
            Log.e(TAG, "isPlaying 当前播放状态 playStatus=" + (mTargetController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING));
            return mTargetController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING;
        }
        return false;
    }


    public void getCurrentMusicInfo() {
        if (mTargetController != null) {
            MusicInfoLiveData2.updateMetadata(mTargetController.getMetadata());
            MusicInfoLiveData2.updatePlaybackState(mTargetController.getPlaybackState());
        }
    }

    public boolean isConnected() {
        return mMediaBrowser != null && mMediaBrowser.isConnected();
    }

    public void release() {
        if (headsetReceiver != null) {
            context.unregisterReceiver(headsetReceiver);
        }
        if (mTargetController != null) {
            mTargetController = null;
        }
        instance = null;
        MediaControllerTracker2.getInstance().release();
        disconnect();
    }
}
