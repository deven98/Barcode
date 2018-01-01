package com.unistrong.luowei.communication;

public abstract class CallbackPacket implements IATEPacket, ResultCallback {
    public long timeOut() {
        return ResultCallback.TIME_OUT;
    }
}
