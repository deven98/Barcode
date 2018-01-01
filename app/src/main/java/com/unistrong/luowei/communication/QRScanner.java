package com.unistrong.luowei.communication;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import com.luowei.qrscan.RFControl;
import com.van.uart.LastError;
import com.van.uart.UartManager;
import com.van.uart.UartManager.BaudRate;

public class QRScanner extends Communication {
    private static final String TAG = QRScanner.class.getSimpleName();
    private boolean busy;
    private Handler handler = new Handler(Looper.getMainLooper());
    private UartManager manager = new UartManager();
    private final Object object = new Object();

    public boolean startScan(final ResultCallback callback) {
        if (this.busy) {
            return false;
        }
        this.busy = true;
        Log.d(TAG, "startScan: begin ");
        this.busy = true;
        write(new CallbackPacket() {
            public byte[] getRaw() {
                return new byte[]{(byte) 27, (byte) 49, (byte) 13, (byte) 10};
            }

            public boolean result(final boolean success, final byte[] bytes, final int length) {
                Log.d(QRScanner.TAG, "result: " + Utils.getHexString(bytes, length));
                if (!success) {
                    QRScanner.this.handler.post(new Runnable() {
                        public void run() {
                            callback.result(success, bytes, length);
                        }
                    });
                    Log.d(QRScanner.TAG, "startScan: finish");
                    QRScanner.this.busy = false;
                    return true;
                } else if (length == 1 && (byte) 6 == bytes[0]) {
                    return false;
                } else {
                    QRScanner.this.handler.post(new Runnable() {
                        public void run() {
                            callback.result(success, bytes, length);
                        }
                    });
                    QRScanner.this.busy = false;
                    Log.d(QRScanner.TAG, "startScan: finish");
                    return true;
                }
            }

            public long timeOut() {
                return 3700;
            }
        });
        Log.d(TAG, "startScan: ok");
        return true;
    }

    protected boolean _connect() {
        try {
            RFControl.rfidOn();
            this.manager.open("ttyHSL2", BaudRate.B9600);
            return true;
        } catch (LastError e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    protected void _disconnect() {
        RFControl.rfidOff();
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

    @Nullable
    protected IProcess getProcess() {
        return null;
    }

    public boolean isConnected() {
        return this.manager.isOpen();
    }
}
