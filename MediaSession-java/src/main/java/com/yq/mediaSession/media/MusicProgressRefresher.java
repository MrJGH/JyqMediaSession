package com.yq.mediaSession.media;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: jyq
 * @desc: 进度条刷新
 * @date: 2025/4/21
 */
public class MusicProgressRefresher {
    private static final String TAG = "MusicProgressRefresher";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable;
    private boolean isRunning = false;

    private final AtomicLong progress = new AtomicLong(0); // 当前播放进度（毫秒）

    public interface OnProgressUpdateListener {
        void onProgressUpdate(long position, long duration);
    }

    /**
     * 开始 进度条
     *
     * @param startTimeMillis 进度条起始时长
     * @param duration        进度条总时长
     * @param listener        进度条每隔1秒 回调监听
     */
    public void start(long startTimeMillis, long duration, OnProgressUpdateListener listener) {
        Log.e(TAG, "start  startTimeMillis=" + startTimeMillis + "  duration=" + duration);
        progress.addAndGet(startTimeMillis);
        if (isRunning) return;
        isRunning = true;
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;
                progress.addAndGet(1000);
                if (progress.get() >= duration) {
                    stop();
                }
                listener.onProgressUpdate(progress.get(), duration);
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(refreshRunnable);
    }

    /**
     * 停止进度条
     */
    public void stop() {
        Log.e(TAG, "stop: ");
        isRunning = false;
        if (refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
        progress.set(0);
    }
}
