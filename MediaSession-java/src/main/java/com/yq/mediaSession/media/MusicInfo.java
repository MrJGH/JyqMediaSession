package com.yq.mediaSession.media;

import android.graphics.Bitmap;
/**
 * @author: jyq
 * @desc: 音乐相关参数bean
 * @date: 2025/4/21
 */
public class MusicInfo {
    public String title; //标题
    public String artist;  //歌手名字
    public Bitmap albumArt; //歌曲封面 bitmap
    public String albumArtUri; //歌曲封面 uri

    public boolean isPlaying;  //播放状态
    public boolean isHeadsetOn;  //是否是耳机模式

    public long position;     // 当前播放进度（毫秒）
    public long duration;     // 总时长（毫秒）

    public MusicInfo(String title, String artist, Bitmap albumArt,String albumArtUri,
                     boolean isPlaying, boolean isHeadsetOn, long position, long duration) {
        this.title = title;
        this.artist = artist;
        this.albumArt = albumArt;
        this.albumArtUri = albumArtUri;
        this.isPlaying = isPlaying;
        this.isHeadsetOn = isHeadsetOn;
        this.position = position;
        this.duration = duration;
    }
}
