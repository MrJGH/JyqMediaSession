package com.yq.mediaSession.mediacompat;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

/**
 * @author: jyq
 * @desc: 媒体控制器跟踪器 (第三方音乐应用 操作上一首 ，下一首 ，暂停，播放等行为)
 * @date: 2025/4/21
 */
public class MediaControllerTracker2 {

    private MediaControllerCompat mediaController;
    private Context context;

    public static volatile MediaControllerTracker2 instance = null;

    public static MediaControllerTracker2 getInstance() {
        if (instance == null) {
            synchronized (MediaControllerTracker2.class) {
                if (instance == null) {
                    instance = new MediaControllerTracker2();
                }
            }
        }
        return instance;
    }

    public void init(Context context, MediaControllerCompat mediaController) {
        this.context = context.getApplicationContext();
        this.mediaController = mediaController;
        mediaController.registerCallback(controllerCallback);
    }

    private final MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            // 在此处理播放/暂停/停止等状态变化
            Log.d(TAG, "onPlaybackStateChanged packageName= " + mediaController.getPackageName() + "  state = " + state);
            if (state != null) {
                MusicInfoLiveData2.updatePlaybackState(state);
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            Log.d(TAG, "onMetadataChanged packageName= " + mediaController.getPackageName() + "  metadata= " + metadata);
            if (metadata != null) {
                MusicInfoLiveData2.updateMetadata(metadata);
            }

        }
    };

    public void release() {
        if (mediaController != null) {
            mediaController.unregisterCallback(controllerCallback);
        }
    }

    private static final String TAG = "MediaControllerTracker2";
}
