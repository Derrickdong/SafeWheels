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

    public static String readFromFile(Context context, int mode) {
        String result = "";
        FileInputStream fileInputStream = null;
        if (mode == 1){
            if (!fileExist("emergency.txt", context)){
                writeToFile("", context, 1);
                return "";
            }
        }else{
            if (!fileExist("favorite.txt", context)){
                writeToFile("", context, 2);
            }
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

    public static void writeToFile(String str, Context context, int mode){
        try {
            FileOutputStream outputStream;
            if (mode == 1){
                outputStream = context.openFileOutput("emergency.txt", Context.MODE_PRIVATE);
            }else{
                outputStream = context.openFileOutput("favorite.txt", Context.MODE_PRIVATE);
            }
            outputStream.write(str.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static boolean fileExist(String fname, Context context){
        File file = context.getFileStreamPath(fname);
        return file.exists();
    }
}
