package com.samer.wear.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.samer.wear.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Sendmessage_activity extends AppCompatActivity {
    private Button sendmessagebutton;
    private EditText messagetosend;


    private FusedLocationProviderClient client ;
    // final static String url = "https://hmin309-embedded-systems.herokuapp.com/message-exchange/from/10001/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmessage_layout);
        sendmessagebutton = findViewById(R.id.sendmessagebutton);
        messagetosend = findViewById(R.id.messagetosend);

        requestPermissions() ;
        client = LocationServices.getFusedLocationProviderClient(this);




        sendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Sendmessage_activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(Sendmessage_activity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                client.getLastLocation().addOnSuccessListener(Sendmessage_activity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location!=null){
//                            TextView textView = findViewById(R.id.location);
//                            textView.setText(location.toString());
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String longg = String.valueOf(longitude);
                            String latt = String.valueOf(latitude);
                            showMessage("Your Current longitude is");
                            showMessage(longg);
                            showMessage("And you current latitude is");
                            showMessage(latt);

                        }
                    }
                });
                sendPost();
            }
        });
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this, new String[]{ACCESS_FINE_LOCATION},1
        );
    }



    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://hmin309-embedded-systems.herokuapp.com/message-exchange/messages/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("student_id", 21813570);
                    jsonParam.put("gps_lat", 43.632463);
                    jsonParam.put("gps_long", 3.8629348);
                    jsonParam.put("student_message", messagetosend.getText().toString());

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public void onBackPressed() {
        Intent registerActivity = new Intent(getApplicationContext(), Welcome_activity.class);
        startActivity(registerActivity);
        finish();
    }
    private void showMessage(String text) {

        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
