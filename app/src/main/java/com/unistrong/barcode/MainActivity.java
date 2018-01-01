package com.unistrong.barcode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.unistrong.luowei.communication.QRScanner;
import com.unistrong.luowei.communication.SimpleResultCallback;

public class MainActivity extends AppCompatActivity {
    private Button mBtnscanner;
    private TextView mTvresult;
    private QRScanner qrScanner;

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
            MainActivity.this.qrScanner.startScan(new C02611());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        this.qrScanner = new QRScanner();
        this.mBtnscanner = (Button) findViewById(R.id.btn_scanner);
        this.mTvresult = (TextView) findViewById(R.id.tv_result);
        this.mBtnscanner.setOnClickListener(new C01821());
    }

    protected void onResume() {
        super.onResume();
        if (!this.qrScanner.isConnected()) {
            this.qrScanner.connect();
        }
    }

    protected void onPause() {
        super.onPause();
        this.qrScanner.disconnect();
    }
}
