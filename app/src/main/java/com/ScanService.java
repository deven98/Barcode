package com;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

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

        public boolean result(boolean success, byte[] bytes, int length) {

            Log.d("barcode","result " + success);

            if (success) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("barcode", new String(bytes,0,length));
                clipboard.setPrimaryClip(clip);
            } else {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("barcode", "Error");
                clipboard.setPrimaryClip(clip);
            }
            return true;
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        qrScanner.connect();

        Log.d("barcode","Scanner Connected " + qrScanner.isConnected());

        qrScanner.startScan(new C02611());

    }

}
