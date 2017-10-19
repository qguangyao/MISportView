package com.qt.miview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qt.miview.views.CircleVIew;

public class MainActivity extends AppCompatActivity {
    public static final int RESTART_ANIMATION = 100;

    public static final long ANIMATION_DURATION = 2000;//撒花旋转周期
    public static final long FACK_PROGRESS_TIME = 3000;//假的连接蓝牙的时间
    private int step;
    private int moveValue;

    private CircleVIew circleVIew;//自定义的View
    private Button play;//开始连接设备
    private Button stop;//连接成功或者失败
    private Button add_step;//增加100步数
    private TextView tv_step;//显示步数的
    private RelativeLayout circle_layout;//自定义View的布局

    private ObjectAnimator animator;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESTART_ANIMATION:
                    if (!circleVIew.isSuccess()) {
                        animator.start();
                        mHandler.sendEmptyMessageDelayed(RESTART_ANIMATION, ANIMATION_DURATION - 1);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        circleVIew = (CircleVIew) findViewById(R.id.circleView1);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        add_step = (Button) findViewById(R.id.add_step);
        tv_step = (TextView) findViewById(R.id.step);
        circle_layout = (RelativeLayout) findViewById(R.id.circle_layout);
    }

    private void initData() {
        animator = ObjectAnimator.ofFloat(
                circleVIew, "rotation", 0f, 360f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(ANIMATION_DURATION);
        animator.setRepeatCount(0);
        animator.setRepeatMode(ValueAnimator.RESTART);
        palyAnim();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palyAnim();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAnim();
            }
        });
        add_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step += 100;
                circleVIew.setmSteps(step);
                tv_step.setText("" + step);
            }
        });
        circleVIew.setOnScaleListener(new CircleVIew.onScaleListener() {
            @Override
            public void onScale(int changeValue, int max, int count) {
                if (count == 0) {
                    circle_layout.scrollTo(0, changeValue);
                } else if (count == 1) {
                    circle_layout.scrollTo(0, changeValue);
                } else if (count == 2 && moveValue <= max) {
                    if (moveValue > max) {
                        moveValue = max;
                    }
                    circle_layout.scrollTo(0, moveValue);
                    circleVIew.scrollTo(0, moveValue);
                    moveValue += circleVIew.getSpan();
                }

            }
        });
    }//initData

    //开始动画
    private void palyAnim() {
        circleVIew.scrollTo(0,0);
        moveValue = 0;
        circleVIew.startProgress();
        mHandler.sendEmptyMessage(RESTART_ANIMATION);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAnim();
            }
        }, FACK_PROGRESS_TIME);
        play.setEnabled(false);
    }

    //停止一阶段动画
    private void stopAnim() {
        circleVIew.setSuccess();
        animator.cancel();
        animator.end();
        mHandler.removeCallbacksAndMessages(null);
        play.setEnabled(true);
    }
}
