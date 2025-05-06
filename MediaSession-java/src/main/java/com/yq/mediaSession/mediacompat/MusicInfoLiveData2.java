package com.yq.mediaSession.mediacompat;

import android.media.MediaMetadata;
import android.media.session.PlaybackState;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yq.mediaSession.media.MusicInfo;

/**
 * @author: jyq
 * @desc:  播放器 所有view 监听 liveData
 * @date: 2025/4/21
 */
public class MusicInfoLiveData2 {

    private static final MutableLiveData<MusicInfo> musicInfoLiveData = new MutableLiveData<>();
    private static MusicInfo currentInfo = new MusicInfo("", "", null, null, false, false, 0, 0);

    public static LiveData<MusicInfo> getLiveData() {
        return musicInfoLiveData;
    }

    /**
     * 更新 歌曲变化 （标题，歌手，封面，总时长）
     *
     * @param metadata
     */
    public static void updateMetadata(MediaMetadataCompat metadata) {
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
    public static void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) return;
        currentInfo.isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
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
}

