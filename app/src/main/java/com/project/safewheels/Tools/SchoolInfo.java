package com.project.safewheels.Tools;

import android.content.Context;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;

public class SchoolInfo {

    public static String[] ReadFile(Context context) {
        String[] line = new String[583];
        try {
            DataInputStream dataInputStream = new DataInputStream(context.getAssets().open("schools"));
            Scanner sc = new Scanner(dataInputStream);
            for (int i = 0; sc.hasNextLine(); i++){
                String aLine = sc.nextLine();
                line[i] = aLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return line;
    }
}
