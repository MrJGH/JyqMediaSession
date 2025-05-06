package com.yq.mediaSession.media;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.yq.mediaSession.mediacompat.MediaControllerTracker2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: jyq
 * @desc: 客户端控制器
 * @date: 2025/4/21
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicPlayManager {
    private static final String TAG = "MusicPlayManager";
    private static volatile MusicPlayManager instance;
    private final Context context;
    private final MediaSessionManager mediaSessionManager;
    private final ComponentName listenerComponent;
    private final Map<String, MediaController> controllerMap = new HashMap<>();
    private String currentPackage = null;
    private HeadsetReceiver headsetReceiver;

    public static MusicPlayManager getInstance(Context context) {
        if (instance == null) {
            synchronized (MusicPlayManager.class) {
                if (instance == null) {
                    instance = new MusicPlayManager(context);
                }
            }
        }
        return instance;
    }

    private MusicPlayManager(Context context) {
        this.context = context.getApplicationContext();
        this.mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        this.listenerComponent = new ComponentName(context, MusicNotifyService.class);
        refreshControllers();
        registerHeadsetReceiver();
    }

    // 获取目标播放器 MediaController
    public void refreshControllers() {
        controllerMap.clear();
        currentPackage = null;

        try {
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(listenerComponent);
            for (MediaController controller : controllers) {
                String pkg = controller.getPackageName();
                PlaybackState state = controller.getPlaybackState();

                controllerMap.put(pkg, controller);
                if (state != null && state.getState() == PlaybackState.STATE_PLAYING) {
                    currentPackage = pkg;
                }
            }

            // 默认选择第一个
            if (currentPackage == null && !controllerMap.isEmpty()) {
                currentPackage = controllerMap.keySet().iterator().next();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册耳机插拔广播
     */
    private void registerHeadsetReceiver() {
        headsetReceiver = new HeadsetReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(headsetReceiver, filter);
    }

    private MediaController getCurrentController() {
        if (currentPackage == null) return null;
        return controllerMap.get(currentPackage);
    }

    public void play() {
        MediaController targetController = getCurrentController();
        Log.e(TAG, "play 点击了播放 targetController=" + targetController);
        if (targetController != null) {
            targetController.getTransportControls().play();
        }
    }

    public void pause() {
        MediaController targetController = getCurrentController();
        Log.e(TAG, "pause 点击了暂停 targetController=" + targetController);
        if (targetController != null) {
            targetController.getTransportControls().pause();
        }
    }

    public void next() {
        MediaController targetController = getCurrentController();
        Log.e(TAG, "next 点击了下一首 targetController=" + targetController);
        if (targetController != null) {
            targetController.getTransportControls().skipToNext();
        }
    }

    public void previous() {
        MediaController targetController = getCurrentController();
        Log.e(TAG, "previous 点击了上一首 targetController=" + targetController);
        if (targetController != null) {
            targetController.getTransportControls().skipToPrevious();
        }
    }

    public boolean isPlaying() {
        MediaController targetController = getCurrentController();
        if (targetController != null && targetController.getPlaybackState() != null) {
            Log.e(TAG, "isPlaying 当前播放状态 playStatus=" + (targetController.getPlaybackState().getState() == PlaybackState.STATE_PLAYING));
            return targetController.getPlaybackState().getState() == PlaybackState.STATE_PLAYING;
        }
        return false;
    }


    public void getCurrentMusicInfo() {
        MediaController targetController = getCurrentController();
        if (targetController != null) {
            MusicInfoLiveData.updateMetadata(targetController.getMetadata());
            MusicInfoLiveData.updatePlaybackState(targetController.getPlaybackState());
        }
    }

    public void release() {
        if (headsetReceiver != null) {
            context.unregisterReceiver(headsetReceiver);
        }
        controllerMap.clear();
        currentPackage = null;
        instance = null;
    }

    /**
     * 检测 是否有通知权限
     */
    public void checkNotificationService() {
        try {
            if (!isNotificationServiceEnabled(context)) {
                // 前往设置开启权限
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Log.d(TAG, "checkNotificationService: ");
                toggleNotificationListenerService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleNotificationListenerService() {
        PackageManager pm = context.getPackageManager();
        ComponentName thisComponent = new ComponentName(context, MusicNotifyService.class);

        // 先禁用再启用服务，以强制系统重新绑定
        pm.setComponentEnabledSetting(thisComponent,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(thisComponent,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private boolean isNotificationServiceEnabled(Context context) {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(pkgName);
    }

}
