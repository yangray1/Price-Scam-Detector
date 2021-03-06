package com.example.pricescamdetector;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void scanBarcode(View v) {
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToProductHowToUseApp(View v) {
        Intent intent = new Intent(this, HowToUse.class);
        startActivity(intent);
    }
}
