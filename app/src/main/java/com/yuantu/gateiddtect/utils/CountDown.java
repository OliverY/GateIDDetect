package com.yuantu.gateiddtect.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Author:  Yxj
 * Time:    2018/6/26 下午9:24
 * -----------------------------------------
 * Description:
 */
public class CountDown {

    final int MSG_STEP = 1;
    final int MSG_SUCCESS = 2;

    /*
    1.start正在进行中无法start
    2.start正常成功后不能再继续start
    3.进行中，点reset重置，可以重新start
    4.进行中，点stop停止，不可以重新start
    5.可多次点击restart
     */

    private int time;
    private int startNum;
    private Handler handler;
    private Timer timer;
    private volatile boolean isRunning;
    private Callback callback;
    private long startPre;

    public CountDown(int startNum, final Callback callback) {
        this(startNum,callback,200);
    }

    public CountDown(int startNum, final Callback callback,long startPre) {
        this.time = startNum;
        this.startNum = startNum;
        this.callback = callback;
        this.startPre = startPre;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_STEP:
                        callback.onStep(msg.arg1);
                        break;
                    case MSG_SUCCESS:
                        callback.onSuccess();
//                        isRunning = false;
                        break;
                }
            }
        };
    }

    public void start() {

        Log.e("yxj","count start");
        if (isRunning) {
            return;
        }
        isRunning = true;

        callback.onPreStart();

        time = startNum;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (time == 0) {
                    handler.sendEmptyMessage(MSG_SUCCESS);
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    return;
                } else {
                    Message msg = Message.obtain();
                    msg.what = MSG_STEP;
                    msg.arg1 = time;
                    handler.sendMessage(msg);
                }
                time--;
            }
        }, 1000, 1000);

    }

    /**
     * 重置，可以再start
     */
    public void reset() {
        Log.e("yxj","count reset");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        handler.removeMessages(MSG_STEP);
        handler.removeMessages(MSG_SUCCESS);
        isRunning = false;
    }

    /**
     * 停止，不能直接start，必须reset重置后才能restart
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        handler.removeMessages(MSG_STEP);
        handler.removeMessages(MSG_SUCCESS);
    }

    /**
     * 再次开始
     */
    public void restart() {
        reset();
        start();
    }

    public interface Callback {
        void onPreStart();
        void onStep(int count);
        void onSuccess();
    }

}
