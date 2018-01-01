package com.unistrong.luowei.socket;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TcpServer {
    private static final boolean DEBUG = true;
    private static final String TAG = "TcpServer";
    private Socket accept;
    private boolean broadcastIsRun;
    private InputStream inputStream;
    private boolean isConnected;
    private OutputStream outputStream;
    private boolean serverIsRun;
    private ServerSocket serverSocket;

    class C01901 implements Runnable {
        C01901() {
        }

        public void run() {
            while (TcpServer.this.serverIsRun) {
                try {
                    TcpServer.this.accept = TcpServer.this.serverSocket.accept();
                    Log.d(TcpServer.TAG, "run: accept" + TcpServer.this.accept.getInetAddress());
                    TcpServer.this.outputStream = TcpServer.this.accept.getOutputStream();
                    TcpServer.this.inputStream = TcpServer.this.accept.getInputStream();
                    TcpServer.this.isConnected = TcpServer.DEBUG;
                    TcpServer.this.stopBroadcastThread();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class C01912 implements Runnable {
        C01912() {
        }

        public void run() {
            DatagramSocket datagramSocket;
            SocketException e;
            UnknownHostException e2;
            IOException e3;
            InterruptedException e4;
            byte[] msg = new String("Hello PC").getBytes();
            try {
                DatagramSocket client = new DatagramSocket();
                try {
                    DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, new InetSocketAddress(InetAddress.getByName("255.255.255.255"), 5500));
                    while (TcpServer.this.broadcastIsRun) {
                        Log.d(TcpServer.TAG, "run: send broadcast");
                        client.send(sendPacket);
                        Thread.sleep(1000);
                    }
                    client.close();
                    datagramSocket = client;
                } catch (SocketException e5) {
                    e = e5;
                    datagramSocket = client;
                    e.printStackTrace();
                } catch (UnknownHostException e6) {
                    e2 = e6;
                    datagramSocket = client;
                    e2.printStackTrace();
                } catch (IOException e7) {
                    e3 = e7;
                    datagramSocket = client;
                    e3.printStackTrace();
                } catch (InterruptedException e8) {
                    e4 = e8;
                    datagramSocket = client;
                    e4.printStackTrace();
                }
            } catch (SocketException e9) {
                e = e9;
                e.printStackTrace();
            } catch (Exception e10) {
                e2 = (UnknownHostException) e10;
                e2.printStackTrace();
            }
        }
    }

    public void start() throws IOException {
        startBroadcastThread();
        startServerListenerThread();
    }

    private void startServerListenerThread() throws IOException {
        if (!this.serverIsRun) {
            this.serverIsRun = DEBUG;
            this.serverSocket = new ServerSocket(5551);
            new Thread(new C01901()).start();
        }
    }

    private void startBroadcastThread() {
        if (!this.broadcastIsRun) {
            this.broadcastIsRun = DEBUG;
            new Thread(new C01912()).start();
        }
    }

    public void stop() {
        stopBroadcastThread();
        stopServerListenerThread();
    }

    private void stopBroadcastThread() {
        this.broadcastIsRun = false;
    }

    private void stopServerListenerThread() {
        this.serverIsRun = false;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public void write(byte[] data, int length) throws IOException {
        this.outputStream.write(data);
    }

    public int read(byte[] receive, int length) throws IOException {
        return this.inputStream.read(receive);
    }
}
