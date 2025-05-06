package com.yq.mediaSession.media;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: jyq
 * @desc: 媒体控制器跟踪器 (第三方音乐应用 操作上一首 ，下一首 ，暂停，播放等行为)
 * @date: 2025/4/21
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MediaControllerTracker {
    private static final String TAG = "MediaControllerTracker2";

    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Map<String, MediaController.Callback> callbackMap = new HashMap<>();
    private final Map<String, MediaController> controllerMap = new HashMap<>();
    private final MediaSessionManager mediaSessionManager;
    private final ComponentName notificationListenerComponent;

    private MediaSessionListener sessionListener;

    public MediaControllerTracker(Context context, ComponentName listenerComponent) {
        this.context = context.getApplicationContext();
        this.mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        this.notificationListenerComponent = listenerComponent;
    }

    public void setMediaSessionListener(MediaSessionListener listener) {
        this.sessionListener = listener;
    }

    public void refreshControllers() {
        List<MediaController> controllers = mediaSessionManager.getActiveSessions(notificationListenerComponent);
        Set<String> activePackages = new HashSet<>();

        for (MediaController controller : controllers) {
            String pkg = controller.getPackageName();
            activePackages.add(pkg);
            if (!callbackMap.containsKey(pkg)) {
                Log.d(TAG, "Registering controller callback for: " + pkg);
                MediaController.Callback callback = new MediaController.Callback() {
                    @Override
                    public void onPlaybackStateChanged(PlaybackState state) {
                        Log.d(TAG, "onPlaybackStateChanged packageName= " + pkg + "  state = " + state);
                        if (state != null && sessionListener != null) {
                            sessionListener.onPlaybackStateChanged(pkg, state);
                        }
                    }

                    @Override
                    public void onMetadataChanged(MediaMetadata metadata) {
                        Log.d(TAG, "onMetadataChanged packageName= " + pkg + "  metadata= " + metadata);
                        if (metadata != null && sessionListener != null) {
                            sessionListener.onMetadataChanged(pkg, metadata);
                        }
                    }
                };

                controller.registerCallback(callback, handler);
                callbackMap.put(pkg, callback);
                controllerMap.put(pkg, controller);
            }
        }

        // 清理已经不活跃的控制器
        Iterator<Map.Entry<String, MediaController>> it = controllerMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, MediaController> entry = it.next();
            String pkg = entry.getKey();
            if (!activePackages.contains(pkg)) {
                Log.d(TAG, "Unregistering controller callback for: " + pkg);
                MediaController.Callback callback = callbackMap.get(pkg);
                if (callback != null) {
                    entry.getValue().unregisterCallback(callback);
                }
                it.remove();
                callbackMap.remove(pkg);
            }
        }
    }

    public void release() {
        // 在不需要时取消所有注册，释放资源
        for (Map.Entry<String, MediaController> entry : controllerMap.entrySet()) {
            MediaController.Callback callback = callbackMap.get(entry.getKey());
            if (callback != null) {
                entry.getValue().unregisterCallback(callback);
            }
        }
        controllerMap.clear();
        callbackMap.clear();
    }

    public interface MediaSessionListener {
        void onPlaybackStateChanged(String packageName, PlaybackState state);

        void onMetadataChanged(String packageName, MediaMetadata metadata);
    }
}
