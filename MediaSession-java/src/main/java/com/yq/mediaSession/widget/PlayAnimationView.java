package com.yq.mediaSession.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.yq.mediaSession.R;


/**
 * @author: jyq
 * @desc: 播放器 播放动画 view
 * @date: 2025/4/11
 */
public class PlayAnimationView extends ConstraintLayout {

    private AnimationDrawable mPlayAnim;
    private AppCompatImageView mIvDefault;
    private AppCompatImageView mIvPlay;

    public PlayAnimationView(Context context) {
        this(context, null);
    }

    public PlayAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View mView = LayoutInflater.from(context).inflate(R.layout.layout_play_animation, this, true);
        setBackgroundColor(Color.TRANSPARENT);
        mIvDefault = mView.findViewById(R.id.iv_default);
        mIvPlay = mView.findViewById(R.id.iv_playing);
    }

    public void startAnimation() {
        if (mIvPlay != null) {
            mIvPlay.setVisibility(View.VISIBLE);
        }
        if (mIvDefault != null) {
            mIvDefault.setVisibility(View.GONE);
        }
        mPlayAnim = mIvPlay != null ? (AnimationDrawable) mIvPlay.getDrawable() : null;
        if (mPlayAnim != null) {
            mPlayAnim.start();
        }
    }

    public void stopAnimation() {
        if (mIvPlay != null) {
            mIvPlay.setVisibility(View.GONE);
        }
        if (mIvDefault != null) {
            mIvDefault.setVisibility(View.VISIBLE);
        }
        if (mPlayAnim != null) {
            mPlayAnim.stop();
        }
    }
}
