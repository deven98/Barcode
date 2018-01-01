package com.van.hid;

import android.support.annotation.Keep;

@Keep
public final class VanHid {

    public enum WorkMode {
        HIGH_SPEED,
        FULL_SPEED
    }

    public static native void close();

    private static native int getWorkMode();

    public static native boolean isOpen();

    public static native int open();

    public static native int read(byte[] bArr, int i);

    private static native int setWorkMode(int i);

    public static native int write(byte[] bArr, int i);

    static {
        System.loadLibrary("VanHid");
    }

    public static boolean setMode(WorkMode mode) {
        return setWorkMode(mode.ordinal()) == 0;
    }

    public static WorkMode getMode() {
        int ret = getWorkMode();
        if (ret == 0) {
            return WorkMode.HIGH_SPEED;
        }
        if (1 == ret) {
            return WorkMode.FULL_SPEED;
        }
        return null;
    }
}
