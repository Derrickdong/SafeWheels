package com.project.safewheels.Tools;

import android.content.Context;

import com.project.safewheels.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * This is a tool class that deal with all http network connections
 */

public class RestClient {

    private static final String BASE_URL = "https://services2.arcgis.com/18ajPSI0b3ppsmMt/arcgis/rest/services/Strategic_Cycling_Corridor/FeatureServer/0/query?";
    private static final String WEATHERDB_BASE_URL = "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/26216";

    public static String requestFromUrl(String reqUrl, String key) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            if (!key.isEmpty()){
                httpURLConnection.setRequestProperty("X-API-KEY", key);
            }
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public static String getFromWeather(Context context) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            String urlString = WEATHERDB_BASE_URL + "?apikey=" + context.getString(R.string.weather_api_key) + "&metric=true";
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public static String getLanes(String param1, String param2, String param3){
        String str = "";
        final String methodPath1 = "where=LOCAL_NAME%20like%20'%25" + param1
                + "%25'%20AND%20LOCAL_TYPE%20%3D%20'" + param2 + "'&outFields=SIDE,SCC_NAME,LOCAL_NAME,LOCAL_TYPE&outSR=4326&f=json";
        final String methodPath2 = "where=SCC_NAME%20like%20'%25" + param3
                +"%25'&outFields=SIDE,SCC_NAME,LOCAL_NAME,LOCAL_TYPE&outSR=4326&f=json";
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";

        try {
            if (param1.isEmpty() && param2.isEmpty()){
                url = new URL(BASE_URL + methodPath2);
            }else{
                url = new URL(BASE_URL + methodPath1);
            }

            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            Scanner inStream = new Scanner(conn.getInputStream());

            while (inStream.hasNextLine()){
                textResult += inStream.nextLine();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textResult;
    }

}
