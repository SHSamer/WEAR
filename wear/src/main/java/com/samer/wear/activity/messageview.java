package com.samer.wear.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.samer.wear.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class  messageview extends WearableActivity  implements SensorEventListener {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        listView = findViewById(R.id.list_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("click","click click");

            }
        });

        updateUI();

        setAmbientEnabled();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();

    }

    public void updateUI() {

        String SAMPLE_URL = "https://hmin309-embedded-systems.herokuapp.com/message-exchange/messages/";
        new JsonTask().execute(SAMPLE_URL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        updateUI();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        updateUI();
    }


    private class JsonTask extends AsyncTask<String, String, String> {


        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    //Log.d("Response: ", "> " + line);   //here u ll get whole response...... 🙂

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray array = new JSONArray(result);
                ArrayList<Note> notes = new ArrayList<Note>();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    //Log.d("array", "" + object.get("student_message"));
                    Note note = new Note(object.get("student_id").toString(),
                            object.get("student_message").toString(),
                            object.get("gps_lat").toString(),
                            object.get("gps_long").toString()
                    );
                    notes.add(note);

                }
                listView.setAdapter(new ListViewAdapter(messageview.this,0,notes));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("end", ""+result);
            //txtJson.setText(result);
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        updateUI();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateUI();
    }
}