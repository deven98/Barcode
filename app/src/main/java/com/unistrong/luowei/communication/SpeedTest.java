package com.unistrong.luowei.communication;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SpeedTest {
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    Handler handler = new C01891();
    private int packetCount;
    private String startTime;
    long time;
    long totalSize;

    class C01891 extends Handler {
        C01891() {
        }

        public void handleMessage(Message msg) {
            SpeedTest.this.time = 0;
            SpeedTest.this.totalSize = 0;
        }
    }

    @SuppressLint({"DefaultLocale"})
    public String getSpeed(long size) {
        return getSpeed(size, ResultCallback.TIME_OUT);
    }

    @SuppressLint({"DefaultLocale"})
    public String getSpeed(long size, long wait) {
        if (this.time == 0) {
            this.time = System.currentTimeMillis();
            this.startTime = this.format.format(Long.valueOf(this.time));
            this.packetCount = 0;
        }
        this.packetCount++;
        long l = System.currentTimeMillis() - this.time;
        this.totalSize += size;
        if (l == 0) {
            l = 1;
        }
        double s = (double) (((float) ((this.totalSize * 1000) / l)) / 1024.0f);
        this.handler.removeMessages(0);
        this.handler.sendEmptyMessageDelayed(0, wait);
        return String.format("%.3fkb/s 包数:%d ", new Object[]{Double.valueOf(s), Integer.valueOf(this.packetCount)});
    }

    private String interval(long l) {
        StringBuilder builder = new StringBuilder();
        long MIUNT = 60 * 1;
        long HOUR = MIUNT * 60;
        long t = l;
        l /= 1000;
        int tmp = (int) (l / HOUR);
        l %= HOUR;
        if (tmp > 0) {
            builder.append(tmp).append("小时");
        }
        tmp = (int) (l / MIUNT);
        l %= MIUNT;
        if (tmp > 0) {
            builder.append(tmp).append("分");
        }
        tmp = (int) (l / 1);
        if (tmp > 0) {
            builder.append(tmp).append("秒");
        }
        t %= 1000;
        if (t > 0) {
            builder.append(t).append("毫秒");
        }
        return builder.toString();
    }
}
