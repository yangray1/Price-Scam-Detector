package com.example.pricescamdetector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CustomDialog extends Dialog implements View.OnClickListener {
    /* Guide from: https://stackoverflow.com/questions/13341560/how-to-create-a-custom-dialog-box-in-android */

    private Activity activity;
    private Button ok_button;

    public CustomDialog(Activity a) {
        super(a);
        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        ok_button = findViewById(R.id.btn_ok);
        ok_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                Intent intent = new Intent(activity, ScanBarcodeActivity.class);
                activity.startActivity(intent);
                activity.finish();
                break;
            default:
                break;
        }
        dismiss();
    }
}