package com;

import android.app.Instrumentation;
import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.unistrong.barcode.MainActivity;
import com.unistrong.luowei.communication.QRScanner;
import com.unistrong.luowei.communication.SimpleResultCallback;

import static com.unistrong.barcode.MainActivity.qrScanner;

public class ScanService extends IntentService {

    public ScanService(String name) {
        super(name);
    }

    public ScanService(){
        super("demo");

    }

    class C02611 extends SimpleResultCallback {
        C02611() {
        }

        public boolean result(boolean success, final byte[] bytes, final int length) {

            Log.d("barcode","result " + success);

            if (success) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("barcode", new String(bytes,0,length));
                clipboard.setPrimaryClip(clip);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Result: " + new String(bytes,0,length) + "\n Result Copied", Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                //Write error code here
            }
            return true;
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(qrScanner!=null){
            qrScanner = new QRScanner();
        }

        if(qrScanner!=null) {
            if (!qrScanner.isConnected()) {
                qrScanner.connect();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Initialising Scanner... \n Please click again in a short while to start scan.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        if(qrScanner!=null) {
            Log.d("barcode", "Scanner Connected " + qrScanner.isConnected());
            qrScanner.startScan(new C02611());
        }
    }

}
