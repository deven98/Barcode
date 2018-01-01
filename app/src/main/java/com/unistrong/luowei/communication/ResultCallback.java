package com.unistrong.luowei.communication;

public interface ResultCallback {
    public static final long TIME_OUT = 2000;

    boolean result(boolean z, byte[] bArr, int i);

    long timeOut();
}
