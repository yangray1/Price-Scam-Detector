package com.example.antiscamdetector;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static android.Manifest.permission.CAMERA;

public class ScanBarcodeActivity extends AppCompatActivity {
    /* Tutorial from https://www.youtube.com/watch?v=czmEC5akcos */

    private static final int REQUEST_CAMERA = 1;
    private boolean recievedData = false;
    SurfaceView cameraPreview;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        // Dont need this b/c we already to .label, .parent in manifest. If uncomment => ERROR
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        cameraPreview = findViewById(R.id.cameraPreview);
        textView = findViewById(R.id.textView);
        createCameraSource();
    }

    public void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        // Add callback so we can start/stop camera when surfaceView is created/destroyed
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermissions()) {
//                        Toast.makeText(ScanBarcodeActivity.this, "Camera permission granted",
//                                Toast.LENGTH_LONG).show();
//
                        try {
                            cameraSource.start(cameraPreview.getHolder());
                            System.out.println("STARED CAMERA.....@@@@@@@@@@@@@@@@@@2");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else {
                        requestPermission();
                    }
                }
                System.out.println("RETURNING...");
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() > 0 && !recievedData){
                    recievedData = true;

                    // Send the barcode to product list activity through intent
                    Intent intent = new Intent(ScanBarcodeActivity.this, ProductListActivity.class);
                    intent.putExtra("barcode", barcodes.valueAt(0).displayValue);
                    intent.putExtra("statusCode", CommonStatusCodes.SUCCESS);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(ScanBarcodeActivity.this,
                CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }
}
