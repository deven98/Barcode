package com.unistrong.luowei.communication;

public interface IProcess {

    public enum Status {
        Consume,
        Done,
        Unknown
    }

    byte[] getProcessAfterRaw();

    boolean process(byte[] bArr, int i);
}
