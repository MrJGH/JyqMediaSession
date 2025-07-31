package com.yq.mediaSession.media;

import android.graphics.Bitmap;

import java.util.Objects;

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

    public MusicInfo(String title, String artist, Bitmap albumArt, String albumArtUri,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MusicInfo)) return false;
        MusicInfo that = (MusicInfo) o;

        return isHeadsetOn == that.isHeadsetOn &&
                isPlaying == that.isPlaying &&
                duration == that.duration &&
                Objects.equals(title, that.title) &&
                Objects.equals(artist, that.artist) &&
                Objects.equals(albumArt, that.albumArt) &&
                Objects.equals(albumArtUri, that.albumArtUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist, albumArt, albumArtUri, isPlaying, isHeadsetOn, duration);
    }

    public MusicInfo copy() {
        return new MusicInfo(
                this.title,
                this.artist,
                this.albumArt,
                this.albumArtUri,
                this.isPlaying,
                this.isHeadsetOn,
                this.position,
                this.duration
        );
    }

    @Override
    public String toString() {
        return "🎵 MusicInfo {" +
                "\n  🎶 Title        = '" + title + '\'' +
                "\n  👤 Artist       = '" + artist + '\'' +
                "\n  🖼️ Album        = '" + albumArt + '\'' +
                "\n  🖼️ Album URI    = '" + albumArtUri + '\'' +
                "\n  ▶️ Is Playing   = " + isPlaying +
                "\n  🎧 Headset On   = " + isHeadsetOn +
                "\n  ⏱️ Position     = " + position + " ms" +
                "\n  ⏳ Duration     = " + duration + " ms" +
                "\n}";
    }


}
