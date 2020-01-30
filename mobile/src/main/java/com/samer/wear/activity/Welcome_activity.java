package com.samer.wear.activity;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.samer.wear.R;



public class Welcome_activity extends AppCompatActivity {

    private LinearLayout app;
    private LinearLayout aboutus;
    private LinearLayout sendmessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);


        app = findViewById(R.id.app);
        aboutus = findViewById(R.id.aboutus);
        sendmessage = findViewById(R.id.sendmessage);


        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("Some information about the application");
                Intent registerActivity = new Intent(getApplicationContext(), Aboutus_activity.class);
                startActivity(registerActivity);
                finish();


            }
        });
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("Back press to exit the application");
                Intent registerActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(registerActivity);
                finish();


            }
        });
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage("Send message from here");
                Intent registerActivity = new Intent(getApplicationContext(), Sendmessage_activity.class);
                startActivity(registerActivity);
                finish();


            }
        });

    }
    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

}
