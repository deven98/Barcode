package com.unistrong.luowei.communication;

public abstract class SimpleProcess implements IProcess {
    public byte[] getProcessAfterRaw() {
        return new byte[0];
    }
}
