package com.yq.mediaSession.media;

import android.content.ComponentName;
import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * @author: jyq
 * @desc: 音乐通知监听服务
 * @date: 2025/4/21
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicNotifyService extends NotificationListenerService {
    private static final String TAG = "MusicNotifyService";
    private MediaControllerTracker tracker;

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "onListenerConnected: ");

        tracker = new MediaControllerTracker(this, new ComponentName(this, MusicNotifyService.class));
        tracker.setMediaSessionListener(new MediaControllerTracker.MediaSessionListener() {
            @Override
            public void onPlaybackStateChanged(String pkg, PlaybackState state) {
                MusicInfoLiveData.updatePlaybackState(state);
            }

            @Override
            public void onMetadataChanged(String pkg, MediaMetadata metadata) {
                MusicInfoLiveData.updateMetadata(metadata);
            }
        });

        tracker.refreshControllers(); // 初次连接时扫描
        MusicPlayManager.getInstance(this).refreshControllers();
        MusicInfoLiveData.updateNotifyServiceStatus(true);

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "Notification posted from: " + sbn.getPackageName());
        refreshAllControllers(sbn.getPackageName());
    }

    /**
     * 刷新 所有的 音乐类 App 控制器
     *
     * @param packageName 音乐播放器报名
     */
    private void refreshAllControllers(String packageName) {
        if (isMusicApp(packageName)) {
            if (tracker != null) {
                tracker.refreshControllers();
            }
            MusicPlayManager.getInstance(this).refreshControllers();
        }
    }

    private boolean isMusicApp(String packageName) {
        // 根据常见音乐 App 包名来判断
        return packageName.equals("com.netease.cloudmusic") ||       // 网易云音乐
                packageName.equals("com.tencent.qqmusic") ||          // QQ 音乐
                packageName.equals("com.kugou.android") ||            // 酷狗
                packageName.equals("com.kugou.android.watch.lite")||// 酷狗手表
                packageName.equals("com.jxw.yxxt");// 九学王悦享熏听
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.d(TAG, "onListenerDisconnected: ");
        MusicInfoLiveData.updateNotifyServiceStatus(false);
    }

    @Override
    public void onListenerHintsChanged(int hints) {
        super.onListenerHintsChanged(hints);
        Log.d(TAG, "onListenerHintsChanged: ");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory: ");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d(TAG, "onNotificationRemoved Notification posted from: " + sbn.getPackageName());
        refreshAllControllers(sbn.getPackageName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tracker != null) {
            tracker.release();
            MusicPlayManager.getInstance(this).release();
        }
        Log.e(TAG, "onDestroy: ");
    }
}
