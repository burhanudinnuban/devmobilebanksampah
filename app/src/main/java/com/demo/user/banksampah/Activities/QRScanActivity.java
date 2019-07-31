package com.demo.user.banksampah.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import com.demo.user.banksampah.R;

public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    ImageView btn_flash_on, btn_flash_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        setTitle("Barcode Scan eRecycle");

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mScannerView = findViewById(R.id.zxscan);

        btn_flash_off = findViewById(R.id.btnFlashOff);
        btn_flash_on = findViewById(R.id.btnFlashOn);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause(){
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult){
        Log.v("Tag", rawResult.getText());
        Log.v("Tag", rawResult.getBarcodeFormat().toString());
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();*/
        mScannerView.resumeCameraPreview(this);
        super.onPause();
        mScannerView.stopCamera();

        Intent intent = getIntent();
        intent.putExtra("SCAN_RESULT", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void btnFlashOn(View view){
        mScannerView.setFlash(true);
    }

    public void btnFlashOff(View view){
        mScannerView.setFlash(false);
    }
}
