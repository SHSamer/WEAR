package com.samer.wear.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.samer.wear.R;

public class Aboutus_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus_layout);
    }
    @Override
    public void onBackPressed() {
        Intent registerActivity = new Intent(getApplicationContext(), Welcome_activity.class);
        startActivity(registerActivity);
        finish();
    }
}
