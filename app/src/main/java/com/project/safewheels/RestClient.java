package com.project.safewheels;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class RestClient {

    private static final String BASE_URL = "https://services2.arcgis.com/18ajPSI0b3ppsmMt/arcgis/rest/services/Strategic_Cycling_Corridor/FeatureServer/0/query?";

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
