package com.luowei.qrscan;

public class RFControl {
    public static native void rfidOff();

    public static native void rfidOn();

    static {
        System.loadLibrary("rf_control");
    }
}
