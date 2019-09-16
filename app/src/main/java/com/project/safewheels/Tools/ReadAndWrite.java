package com.project.safewheels.Tools;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadAndWrite {

    public static String readFromFile(Context context) {
        String result = "";
        FileInputStream fileInputStream = null;
        if (!fileExist("emergency.txt", context)){
            writeToFile("", context);
        }
        try {
            fileInputStream = context.openFileInput("emergency.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            if ((result = bufferedReader.readLine()) != null){
                return result;
            }else
                return "";

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void writeToFile(String str, Context context){
        try {
            FileOutputStream outputStream = context.openFileOutput("emergency.txt", Context.MODE_PRIVATE);
            outputStream.write(str.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readMessageText(){
        return "Arrived";
    }

    public static boolean fileExist(String fname, Context context){
        File file = context.getFileStreamPath(fname);
        return file.exists();
    }
}
