package com.van.uart;

import android.support.annotation.Keep;

@Keep
public class UartManager {
    private BaudRate baudRate = BaudRate.B115200;
    private int id = -1;
    private String name = "";

    public enum BaudRate {
        B2400,
        B4800,
        B9600,
        B19200,
        B38400,
        B57600,
        B115200
    }

    static {
        System.loadLibrary("VanUart");
    }

    private native void close(int i);

    public static native String[] devices();

    private native boolean isOpen(int i);

    private native int open(String str, int i) throws LastError;

    private native int read(int i, byte[] bArr, int i2, int i3, int i4) throws LastError;

    private native void stopRead(int i);

    private native int write(int i, byte[] bArr, int i2) throws LastError;

    public String getName() {
        return this.name;
    }

    public BaudRate getBaudRate() {
        return this.baudRate;
    }

    public void open(String name, BaudRate baudRate) throws LastError {
        this.id = open(name, baudRate.ordinal());
        this.name = name;
        this.baudRate = baudRate;
    }

    public void close() {
        if (-1 != this.id) {
            close(this.id);
        }
    }

    public boolean isOpen() {
        if (-1 != this.id) {
            return isOpen(this.id);
        }
        return false;
    }

    public int write(byte[] data, int size) throws LastError {
        if (-1 != this.id) {
            return write(this.id, data, size);
        }
        return -1;
    }

    public int read(byte[] buf, int size, int wait, int interval) throws LastError {
        if (-1 != this.id) {
            return read(this.id, buf, size, wait, interval);
        }
        return -1;
    }

    public void stopRead() {
        if (-1 != this.id) {
            stopRead(this.id);
        }
    }
}
