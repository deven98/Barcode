package com.unistrong.luowei.communication;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public abstract class Communication {
    private static final boolean DEBUG = true;
    private static final int SEND_BUFFER_SIZE = 1024;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private IProcess mainThread;
    private ReadThread readThread;
    private byte[] sendBuffer = new byte[getSendBufferSize()];
    private final Object syncObject = new Object();
    private WriteThread writeThread;

    private class ReadThread extends Thread {
        private static final String TAG = "ReadThread";
        private final ConditionVariable conditionVariable;
        private IProcess innerProcess;
        private IProcess replyWait;
        boolean run;

        private ReadThread() {
            this.conditionVariable = new ConditionVariable();
        }

        public synchronized void start() {
            super.start();
            this.run = Communication.DEBUG;
            this.innerProcess = Communication.this.getProcess();
        }

        void _stop() {
            this.run = false;
            interrupt();
        }

        public void run() {
            byte[] receive = new byte[1024];
            while (Communication.this.isConnected() && this.run) {
                int length = Communication.this._read(receive, receive.length);
                if (length > 0) {
                    Log.d(TAG, "length=" + length + "  " + Utils.getHexString(receive, Math.min(receive.length, length)));
                    if (this.innerProcess == null) {
                        sendResult(receive, length);
                    } else if (this.innerProcess.process(receive, length)) {
                        byte[] processAfterRaw = this.innerProcess.getProcessAfterRaw();
                        sendResult(processAfterRaw, processAfterRaw.length);
                    }
                }
            }
        }

        private void sendResult(byte[] processAfterRaw, int length) {
            if (this.replyWait != null && this.replyWait.process(processAfterRaw, length)) {
                this.replyWait = null;
            } else if (Communication.this.mainThread != null) {
                postToMainThread(processAfterRaw, length);
            }
        }

        private void postToMainThread(final byte[] receive, final int length) {
            this.conditionVariable.close();
            Log.d(TAG, "run: postToMainThread");
            Communication.this.mainHandler.post(new Runnable() {
                public void run() {
                    Communication.this.mainThread.process(receive, length);
                    Log.d(ReadThread.TAG, "run: main run done");
                    ReadThread.this.conditionVariable.open();
                }
            });
            Log.d(TAG, "run: read block");
            this.conditionVariable.block();
            Log.d(TAG, "run: read block done");
        }

        void setReplyWait(SimpleProcess replyWait) {
            this.replyWait = replyWait;
        }
    }

    private class WriteThread extends Thread {
        private static final String TAG = "WriteThread";
        private final Queue<IATEPacket> queue;
        private final Object replayObject;
        private boolean run;
        boolean sendOk;

        private WriteThread() {
            this.replayObject = new Object();
            this.queue = new ArrayDeque();
        }

        public synchronized void start() {
            super.start();
            this.run = Communication.DEBUG;
        }

        public void run() {
            Log.d(TAG, "run: write readThread begin");
            while (this.run && this.run) {
                if (this.queue.isEmpty()) {
                    synchronized (this.queue) {
                        try {
                            this.queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG, "run: write begin");
                IATEPacket packet = (IATEPacket) this.queue.poll();
                byte[] poll = packet.getRaw();
                setWriteCallback(packet);
                this.sendOk = false;
                Log.d(TAG, "write =" + Utils.getHexString(poll, poll.length));
                boolean write = Communication.this.write(poll, poll.length);
                if (packet instanceof ResultCallback) {
                    if (write) {
                        Log.d(TAG, "ReplyPacket wait ...");
                        synchronized (this.replayObject) {
                            try {
                                this.replayObject.wait(((ResultCallback) packet).timeOut());
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                            if (!this.sendOk) {
                                Log.d(TAG, "run: waite send failure");
                                ((ResultCallback) packet).result(false, null, 0);
                            }
                        }
                    } else {
                        failureResultCallback((ResultCallback) packet);
                    }
                }
                Log.d(TAG, "run: write done");
            }
            Log.d(TAG, "run: write readThread is done");
        }

        private void failureResultCallback(ResultCallback packet) {
            Log.d(TAG, "run: send failure");
            Communication.this.readThread.setReplyWait(null);
            packet.result(false, null, 0);
        }

        private void setWriteCallback(final IATEPacket packet) {
            if (packet instanceof ResultCallback) {
                Communication.this.readThread.setReplyWait(new SimpleProcess() {
                    public boolean process(byte[] bytes, int len) {
                        synchronized (WriteThread.this.replayObject) {
                            boolean b = false;
                            if (packet instanceof CallbackPacket) {
                                b = ((ResultCallback) packet).result(Communication.DEBUG, bytes, len);
                            }
                            WriteThread.this.sendOk = b;
                            if (b) {
                                WriteThread.this.replayObject.notifyAll();
                            }
                        }
                        return WriteThread.this.sendOk;
                    }
                });
            }
        }

        void _stop() {
            this.run = false;
            interrupt();
        }
    }

    protected abstract boolean _connect();

    protected abstract void _disconnect();

    protected abstract int _read(byte[] bArr, int i);

    protected abstract int _write(byte[] bArr, int i);

    @Nullable
    protected abstract IProcess getProcess();

    protected abstract boolean isConnected();

    public void setMainThread(IProcess mainThread) {
        this.mainThread = mainThread;
    }

    public boolean connect() {
        if (!isConnected()) {
            if (!_connect()) {
                return false;
            }
            closeThread();
            this.readThread = new ReadThread();
            this.writeThread = new WriteThread();
            this.readThread.start();
            this.writeThread.start();
        }
        return DEBUG;
    }

    public void disconnect() {
        _disconnect();
        closeThread();
    }

    private void closeThread() {
        if (this.readThread != null) {
            this.readThread._stop();
            this.readThread = null;
        }
        if (this.writeThread != null) {
            this.writeThread._stop();
            this.writeThread = null;
        }
    }

    public void write(IATEPacket packet) {
        if (this.writeThread != null) {
            this.writeThread.queue.offer(packet);
            synchronized (this.writeThread.queue) {
                this.writeThread.queue.notify();
            }
        }
    }

    protected int getSendBufferSize() {
        return 1024;
    }

    private boolean write(byte[] data, int length) {
        if (!isConnected()) {
            return false;
        }
        int retry = 10;
        int offset = 0;
        while (offset < data.length && retry > 0) {
            int len = Math.min(data.length - offset, 1024);
            if (len < this.sendBuffer.length) {
                Arrays.fill(this.sendBuffer, len, this.sendBuffer.length, (byte) 0);
            }
            System.arraycopy(data, offset, this.sendBuffer, 0, len);
            int result = _write(this.sendBuffer, this.sendBuffer.length);
            if (result > 0) {
                offset += result;
            } else {
                retry--;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (length <= offset) {
            return DEBUG;
        }
        return false;
    }
}
