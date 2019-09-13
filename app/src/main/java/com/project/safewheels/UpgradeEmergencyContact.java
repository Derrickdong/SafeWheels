package com.project.safewheels;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpgradeEmergencyContact extends Activity {

    Button btn_done;
    TextInputEditText et_name;
    TextInputEditText et_email;
    TextInputEditText et_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatecontact);

        et_email = (TextInputEditText)findViewById(R.id.et_email);
        et_name = (TextInputEditText)findViewById(R.id.et_name);
        et_phone = (TextInputEditText)findViewById(R.id.et_phone);
        btn_done = (Button)findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()){
                    String str = et_name.getText().toString() + "," + et_phone.getText().toString() + "," + et_email.getText().toString();
                    writeToFile(str, getApplicationContext());
                    Intent intent = new Intent(UpgradeEmergencyContact.this, BottomNavigation.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Wrong input", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean checkInput() {
        return true;
    }

    private void writeToFile(String str, Context context){
        try {
            FileOutputStream outputStream = openFileOutput("emergency.txt", Context.MODE_PRIVATE);
            outputStream.write(str.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
