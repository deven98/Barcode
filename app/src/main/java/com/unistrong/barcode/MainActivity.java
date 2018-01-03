package com.unistrong.barcode;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ScanService;
import com.unistrong.luowei.communication.QRScanner;
import com.unistrong.luowei.communication.SimpleResultCallback;

public class MainActivity extends AppCompatActivity {
    private Button mBtnscanner;
    private TextView mTvresult;
    public static QRScanner qrScanner;

    PendingIntent resultPendingIntent;

    public static int NOTIFICATION_ID = 1000;

    class C01821 implements OnClickListener {

        class C02611 extends SimpleResultCallback {
            C02611() {
            }

            public boolean result(boolean success, byte[] bytes, int length) {
                if (success) {
                    MainActivity.this.mTvresult.setText(new String(bytes, 0, length));
                    MainActivity.this.mBtnscanner.setEnabled(true);
                } else {
                    MainActivity.this.mTvresult.setText("Failure");
                    MainActivity.this.mTvresult.setEnabled(true);
                }
                return true;
            }
        }

        C01821() {
        }

        public void onClick(View v) {
            qrScanner.startScan(new C02611());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        qrScanner = new QRScanner();
        this.mBtnscanner = (Button) findViewById(R.id.btn_scanner);
        this.mTvresult = (TextView) findViewById(R.id.tv_result);
        this.mBtnscanner.setOnClickListener(new C01821());

        Intent intent = new Intent(this, ScanService.class);
        resultPendingIntent = PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"general")
                .setContentText("Click Notification to open scanner. First click may initialise scanner, click again to start scanning.")
                .setColorized(true)
                .setOngoing(true)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.

        mNotifyMgr.notify(NOTIFICATION_ID, builder.build());

    }

    protected void onResume() {
        super.onResume();
        if (!qrScanner.isConnected()) {
            qrScanner.connect();
        }
    }

    protected void onPause() {
        super.onPause();
        qrScanner.disconnect();
    }
}
