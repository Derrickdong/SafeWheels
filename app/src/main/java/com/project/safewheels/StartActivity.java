package com.project.safewheels;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.project.safewheels.Entity.Weather;
import com.project.safewheels.Tools.ReadAndWrite;
import com.project.safewheels.Tools.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class handles the landing page
 */

public class StartActivity extends AppCompatActivity {

    private Button btn_start;
    Intent intent;
    Toolbar toolbar;
    Weather weather;
    ImageView iv_weather;
    TextView tv_desc;
    TextView tv_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        toolbar = (Toolbar)findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);

        iv_weather = (ImageView) findViewById(R.id.iv_weather);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        tv_temp = (TextView) findViewById(R.id.tv_temp);

        checkAllPermission();

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setAlpha(0.75f);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = ReadAndWrite.readFromFile(getApplicationContext(), 1);
                if (info.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please set the emergency contact before get into the app!", Toast.LENGTH_LONG).show();
                    intent = new Intent(StartActivity.this, UpgradeEmergencyContact.class);
                }else{
                    intent = new Intent(StartActivity.this, BottomNavigation.class);
                }
                startActivity(intent);
            }
        });

        AsyncWeather asyncWeather = new AsyncWeather();
        asyncWeather.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_info){
            Intent intent = new Intent(StartActivity.this, Instruction.class);
            startActivity(intent);
        }
        return true;
    }

    private void checkAllPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    1);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
//
//
//
//        if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
//        }
    }


    private class AsyncWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                result = RestClient.getFromWeather(getApplicationContext().getString(R.string.weather_api_key1));
                if (result.isEmpty()){
                    result = RestClient.getFromWeather(getApplicationContext().getString(R.string.weather_api_key2));
                    if (result.isEmpty()){
                        result = RestClient.getFromWeather(getApplicationContext().getString(R.string.weather_api_key3));
                        if (result.isEmpty()){
                            result = RestClient.getFromWeather(getApplicationContext().getString(R.string.weather_api_key4));
                            if (result.isEmpty()){
                                result = RestClient.getFromWeather(getApplicationContext().getString(R.string.weather_api_key5));
                                if (result.isEmpty()){
                                    result = RestClient.getFromWeather(getApplicationContext().getString(R.string.weather_api_key6));
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String weatherSearchResults) {
            if (weatherSearchResults != null && !weatherSearchResults.equals("")) {
                weather = new Weather();
                try {
                    JSONArray js1 = new JSONArray(weatherSearchResults);

                    JSONObject resultsObj = js1.getJSONObject(0);
                    iv_weather.setImageResource(getApplicationContext()
                            .getResources()
                            .getIdentifier("a"+resultsObj.getInt("WeatherIcon"), "drawable", getApplicationContext().getPackageName()));
                    JSONObject temperatureObj = resultsObj.getJSONObject("Temperature");
                    Double temp = temperatureObj.getDouble("Value");
                    tv_temp.setText(temp.toString() + "\u2103");
                    tv_desc.setText(resultsObj.getString("IconPhrase"));
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.weather_layout);
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Intent intent = new Intent(StartActivity.this, WeatherForecast.class);
                            intent.putExtra("weather", weatherSearchResults);
                            startActivity(intent);
                        }
                    });
                    Toast.makeText(StartActivity.this, "Click weather to see the next 12 hours weather forecast!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
