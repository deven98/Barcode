package com.unistrong.luowei.communication.impl;

import com.unistrong.luowei.communication.Communication;
import com.van.uart.LastError;
import com.van.uart.UartManager;
import com.van.uart.UartManager.BaudRate;

public abstract class AbsUartCommunication extends Communication {
    private UartManager manager = new UartManager();

    protected abstract BaudRate getBaudRate();

    protected abstract String getOpenName();

    protected boolean isConnected() {
        return this.manager.isOpen();
    }

    protected boolean _connect() {
        try {
            this.manager.open(getOpenName(), getBaudRate());
            return true;
        } catch (LastError e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    protected void _disconnect() {
        this.manager.close();
    }

    protected int _write(byte[] buffer, int length) {
        int result = 0;
        try {
            result = this.manager.write(buffer, length);
        } catch (LastError lastError) {
            lastError.printStackTrace();
        }
        return result;
    }

    protected int _read(byte[] receive, int length) {
        try {
            return this.manager.read(receive, length, 100, 500);
        } catch (LastError lastError) {
            lastError.printStackTrace();
            return -1;
        }
    }
}
