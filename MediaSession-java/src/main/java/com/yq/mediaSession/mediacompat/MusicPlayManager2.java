package com.yq.mediaSession.mediacompat;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.yq.mediaSession.media.HeadsetReceiver;

/**
 * @author: jyq
 * @desc: 客户端控制器
 * @date: 2025/4/21
 */
public class MusicPlayManager2 {
    private static final String TAG = "MusicPlayManager2";
    private static volatile MusicPlayManager2 instance;
    private final Context context;
    private HeadsetReceiver headsetReceiver;
    private MediaControllerCompat mTargetController = null;

    private MediaBrowserCompat mMediaBrowser;
//    private final String SERVER_PACKAGE = "com.jxw.yxxt"; // 替换为服务端包名
    private final String SERVER_PACKAGE = "com.example.jyqmediasession"; // 替换为服务端包名
//    private final String SERVER_SERVICE = "com.jxw.player.media.MusicService"; // 替换服务端全类名
    private final String SERVER_SERVICE = "com.yq.mediaSession.service.MyMusicService"; // 替换服务端全类名
//    private final String SERVER_SERVICE = "com.jxw.player.service.PlayerService"; // 替换服务端全类名

    public static MusicPlayManager2 getInstance(Context context) {
        if (instance == null) {
            synchronized (MusicPlayManager2.class) {
                if (instance == null) {
                    instance = new MusicPlayManager2(context);
                }
            }
        }
        return instance;
    }

    private MusicPlayManager2(Context context) {
        this.context = context.getApplicationContext();
        registerHeadsetReceiver();
        connect();
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
            mMediaBrowser = new MediaBrowserCompat(context, new ComponentName(SERVER_PACKAGE, SERVER_SERVICE), connectionCallback, null);
        }
        Log.e(TAG, "connect:  开始服务连接 mediaBrowser.isConnected()=" + mMediaBrowser.isConnected());
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
                mTargetController = new MediaControllerCompat(context, mMediaBrowser.getSessionToken());
                MediaControllerTracker2.getInstance().init(context, mTargetController);
                Log.d(TAG, "Connected to service and controller created.");
            } catch (Exception e) {
                Log.e(TAG, "Failed to connect controller", e);
            }
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.e(TAG, "onConnectionFailed: ");
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.e(TAG, "onConnectionSuspended: ");
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
