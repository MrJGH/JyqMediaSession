package com.yq.mediaSession.widget;


import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yq.mediaSession.R;
import com.yq.mediaSession.media.MusicProgressRefresher;
import com.yq.mediaSession.mediacompat.MusicInfoLiveData2;
import com.yq.mediaSession.mediacompat.MusicPlayManager2;


/**
 * @author: jyq
 * @desc: 播放器view
 * @date: 2025/4/11
 */
public class MusicPlayerView2 extends RelativeLayout implements MusicProgressRefresher.OnProgressUpdateListener {
    private static final String TAG = "MusicPlayerView";
    private ImageView mMusicHeader; //音乐头像
    private TextView mMusicAuthor; //音乐歌手
    private TextView mMusicName; //音乐名字
    private TextView mPlayTime; //当前播放的时间
    private TextView mTotalTime; //当前总时间
    private ImageView mMusicPlayModel; // 播放模式 是否是耳机
    private ImageView mMusicControlPre; // 上一首
    private ImageView mMusicControlPause; //播放或者暂停
    private ImageView mMusicControlNext; //下一首
    private PlayAnimationView mMusicAnimation; //播放动画
    private ProgressBar mSeekBar; //播放进度条
    private final MusicProgressRefresher mProgressRefresher = new MusicProgressRefresher();

    public MusicPlayerView2(Context context) {
        this(context, null);
    }

    public MusicPlayerView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicPlayerView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initListener();
    }


    private void initView(Context context) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_music_player_view2, this, true);
        Log.e(TAG, "initView....view =" + view);
        if (view != null) {
            mMusicHeader = view.findViewById(R.id.iv_yy_tx);
            mMusicAuthor = view.findViewById(R.id.tv_yy_author);
            mMusicName = view.findViewById(R.id.tv_yy_name);
            mPlayTime = view.findViewById(R.id.tv_play_time);
            mTotalTime = view.findViewById(R.id.tv_total_time);
            mMusicPlayModel = view.findViewById(R.id.iv_play_model);
            mMusicControlPre = view.findViewById(R.id.tv_yy_previous);
            mMusicControlPause = view.findViewById(R.id.tv_yy_pause);
            mMusicControlNext = view.findViewById(R.id.tv_yy_next);
            mMusicAnimation = view.findViewById(R.id.iv_music_animation);
            mSeekBar = view.findViewById(R.id.seekBar);
        }

//        MusicPlayManager2.getInstance(getContext()).getCurrentMusicInfo();
        addObserver();
    }

    private void initListener() {
        mMusicControlPre.setOnClickListener(v -> MusicPlayManager2.getInstance(getContext()).previous());

        mMusicControlNext.setOnClickListener(v -> MusicPlayManager2.getInstance(getContext()).next());

        mMusicControlPause.setOnClickListener(v -> {
            if (MusicPlayManager2.getInstance(getContext()).isPlaying()) {
                MusicPlayManager2.getInstance(getContext()).pause();
            } else {
                MusicPlayManager2.getInstance(getContext()).play();
            }
        });
    }

    private void addObserver() {
        MusicInfoLiveData2.getLiveData().observeForever(musicInfo -> {
            Log.e(TAG, "addObserver: musicInfo  = " + musicInfo);
            mMusicName.setText(musicInfo.title);
            mMusicAuthor.setText(musicInfo.artist);
            mMusicHeader.setImageBitmap(musicInfo.albumArt);
            mSeekBar.setMax((int) musicInfo.duration);
            if (musicInfo.isHeadsetOn) {
                mMusicPlayModel.setVisibility(View.VISIBLE);
            } else {
                mMusicPlayModel.setVisibility(View.GONE);
            }

            if (musicInfo.isPlaying) {
                mMusicControlPause.setSelected(true);
                mMusicAnimation.startAnimation();
                mProgressRefresher.stop();
                mProgressRefresher.start(musicInfo.position, musicInfo.duration, this);
            } else {
                mMusicControlPause.setSelected(false);
                mMusicAnimation.stopAnimation();
                mProgressRefresher.stop();
            }

        });
    }

    /**
     * 转换成时间 格式 00:11
     *
     * @param millis
     * @return
     */
    public String currentTimeMillisToString(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public void onProgressUpdate(long position, long duration) {
        mPlayTime.setText(currentTimeMillisToString(position));
        mTotalTime.setText(currentTimeMillisToString(duration));
        mSeekBar.setProgress((int) position);
    }
}
