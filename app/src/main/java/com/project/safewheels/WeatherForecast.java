package com.project.safewheels;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.safewheels.Entity.Weather;
import com.project.safewheels.Tools.WeatherAdaptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * This class handles the weather forecast page
 */

public class WeatherForecast extends AppCompatActivity {

    private ArrayList<Weather> weatherArrayList = new ArrayList<>();
    private ListView listView;
    Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        listView = findViewById(R.id.idListView);

        btn_back = (Button)findViewById(R.id.btn_goback);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask();
        weatherAsyncTask.execute();
    }

    private class WeatherAsyncTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Intent intent = getIntent();
            String weatherResult = intent.getStringExtra("weather");
            try {
                weatherArrayList = parseJSON(weatherResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (weatherArrayList != null) {
                WeatherAdaptor weatherAdapter = new WeatherAdaptor(WeatherForecast.this, weatherArrayList);
                listView.setAdapter(weatherAdapter);
            }
            return null;
        }

        private ArrayList<Weather> parseJSON(String weatherSearchResults) throws JSONException {
            if (weatherArrayList != null) {
                weatherArrayList.clear();
            }

            if (weatherSearchResults != null) {
                try {
                    JSONArray js1 = new JSONArray(weatherSearchResults);

                    for (int i = 0; i < js1.length(); i++) {
                        Weather weather = new Weather();
                        JSONObject resultsObj = js1.getJSONObject(i);

                        Date newDate = new Date();
                        String dateString = resultsObj.getString("DateTime");
                        String[] format = dateString.split("T");
                        String[] format1 = format[1].split("\\+");
                        String[] format3 = format1[0].split(":");
                        weather.setDateTime(format3[0] + ":00");

                        JSONObject temperatureObj = resultsObj.getJSONObject("Weather");
                        String temp = temperatureObj.getString("Value");
                        weather.setTemperature(temp);

                        String desc = resultsObj.getString("IconPhrase");
                        weather.setDescription(desc);

                        String link = resultsObj.getString("Link");
                        weather.setLink(link);

                        String iconNumber  = Integer.toString(resultsObj.getInt("WeatherIcon"));
                        weather.setIconNumber(iconNumber);

                        weatherArrayList.add(weather);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return weatherArrayList;
        }

    }
}
