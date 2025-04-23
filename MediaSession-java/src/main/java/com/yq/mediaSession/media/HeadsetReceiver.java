package com.yq.mediaSession.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * @author: jyq
 * @desc: 耳机插拔广播
 * @date: 2025/4/21
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HeadsetReceiver extends BroadcastReceiver {
    private static final String TAG = "HeadsetReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
            int state = intent.getIntExtra("state", -1);
            boolean isPlugged = state == 1;
            Log.e(TAG, "HeadsetReceiver 耳机状态变化 isPlugged：" + isPlugged);
            MusicInfoLiveData.updateHeadsetState(isPlugged);
        }
    }
}
