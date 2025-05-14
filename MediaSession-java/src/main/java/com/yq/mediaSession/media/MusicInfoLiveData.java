package com.yq.mediaSession.media;

import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yq.mediaSession.media.MusicInfo;

/**
 * @author: jyq
 * @desc:  播放器 所有view 监听 liveData
 * @date: 2025/4/21
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicInfoLiveData {

    private static final MutableLiveData<MusicInfo> musicInfoLiveData = new MutableLiveData<>();
    private static final MutableLiveData<Boolean> notifyServiceStatusLiveData = new MutableLiveData<>();
    private static MusicInfo currentInfo = new MusicInfo("", "", null, null, false, false, 0, 0);

    public static LiveData<MusicInfo> getLiveData() {
        return musicInfoLiveData;
    }

    public static LiveData<Boolean> getNotifyServiceStatusLiveData() {
        return notifyServiceStatusLiveData;
    }

    /**
     * 更新 歌曲变化 （标题，歌手，封面，总时长）
     *
     * @param metadata
     */
    public static void updateMetadata(MediaMetadata metadata) {
        if (metadata == null) return;
        currentInfo.title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
        currentInfo.artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
        currentInfo.albumArt = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        currentInfo.albumArtUri = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI);
        currentInfo.duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        post();
    }

    /**
     * 更新 音乐播放器参数状态
     *
     * @param state
     */
    public static void updatePlaybackState(PlaybackState state) {
        if (state == null) return;
        currentInfo.isPlaying = state.getState() == PlaybackState.STATE_PLAYING;
        currentInfo.position = state.getPosition();
        post();
    }

    /**
     * 更新 耳机状态
     *
     * @param isHeadsetOn
     */
    public static void updateHeadsetState(boolean isHeadsetOn) {
        currentInfo.isHeadsetOn = isHeadsetOn;
        post();
    }

    private static void post() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            musicInfoLiveData.setValue(currentInfo);
        } else {
            musicInfoLiveData.postValue(currentInfo);
        }
    }

    /**
     * 更新 NotificationListenerService 连接状态
     * @param connectStatus true已经连接 ，false断开连接
     */
    public static void updateNotifyServiceStatus(boolean connectStatus) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            notifyServiceStatusLiveData.setValue(connectStatus);
        } else {
            notifyServiceStatusLiveData.postValue(connectStatus);
        }
    }
}

