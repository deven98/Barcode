package com.unistrong.luowei.communication.impl;

import com.unistrong.luowei.communication.Communication;
import com.van.hid.VanHid;

public abstract class AbsHidCommunication extends Communication {
    protected boolean _connect() {
        return VanHid.open() == 0;
    }

    protected void _disconnect() {
        VanHid.close();
    }

    protected int _write(byte[] buffer, int length) {
        return VanHid.write(buffer, length);
    }

    protected int _read(byte[] receive, int length) {
        return VanHid.read(receive, length);
    }

    protected boolean isConnected() {
        return VanHid.isOpen();
    }
}
