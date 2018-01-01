package com.unistrong.luowei.communication;

public abstract class SimpleResultCallback implements ResultCallback {
    public long timeOut() {
        return ResultCallback.TIME_OUT;
    }
}
