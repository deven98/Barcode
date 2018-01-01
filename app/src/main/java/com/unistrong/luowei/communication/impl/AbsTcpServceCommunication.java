package com.unistrong.luowei.communication.impl;

import com.unistrong.luowei.communication.Communication;
import com.unistrong.luowei.socket.TcpServer;
import java.io.IOException;

public abstract class AbsTcpServceCommunication extends Communication {
    private final TcpServer tcpServer = new TcpServer();

    protected boolean _connect() {
        try {
            this.tcpServer.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void _disconnect() {
        this.tcpServer.stop();
    }

    protected int _write(byte[] buffer, int length) {
        try {
            this.tcpServer.write(buffer, length);
            return length;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected int _read(byte[] receive, int length) {
        try {
            return this.tcpServer.read(receive, length);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    protected boolean isConnected() {
        return this.tcpServer.isConnected();
    }
}
