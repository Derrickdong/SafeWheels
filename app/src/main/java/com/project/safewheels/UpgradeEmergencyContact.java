package com.project.safewheels;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.project.safewheels.Tools.ReadAndWrite;

public class UpgradeEmergencyContact extends AppCompatActivity {

    Button btn_done;
    TextInputEditText et_name;
    TextInputEditText et_email;
    TextInputEditText et_phone;
    TextView tv_phone_error;
    TextView tv_error;
    SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatecontact);

        et_email = (TextInputEditText)findViewById(R.id.et_email);
        et_name = (TextInputEditText)findViewById(R.id.et_name);
        et_phone = (TextInputEditText)findViewById(R.id.et_phone);
        btn_done = (Button)findViewById(R.id.btn_done);
        smsManager = SmsManager.getDefault();
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmail(et_email.getText().toString()) == 0){
                    if (checkPhone(et_phone.getText().toString()) == 0){
                        String str = et_name.getText().toString() + "," + et_phone.getText().toString() + "," + et_email.getText().toString();
                        ReadAndWrite.writeToFile(str, getApplicationContext(), 1);
                        sendTextMessage(et_phone.getText().toString());
                    }else{
                        tv_phone_error = (TextView)findViewById(R.id.tv_phone_error);
                        tv_phone_error.setVisibility(View.VISIBLE);
                        if(checkPhone(et_phone.getText().toString()) == 1){
                            tv_phone_error.setText("Phone number cannot be empty");
                        }else{
                            tv_phone_error.setText("Wrong Phone number");
                        }
                    }
                }else{
                    tv_error = (TextView)findViewById(R.id.tv_email_error);
                    tv_error.setVisibility(View.VISIBLE);
                    if (checkEmail(et_email.getText().toString()) == 1){
                        tv_error.setText("Email address cannot be empty");
                    }else
                        tv_error.setText("Wrong Email Address");
                }
                checkAllPermission();

            }
        });

        String info = ReadAndWrite.readFromFile(getApplicationContext(),1);
        if (! (info.isEmpty())){
            String[] infos = info.split(",");
            et_phone.setText(infos[1]);
            et_name.setText(infos[0]);
            et_email.setText(infos[2]);
        }
    }

    private int checkPhone(String phone) {
        if (phone.length() == 0){
            return 1;
        }
        else if (android.util.Patterns.PHONE.matcher(phone).matches()){
            return 0;
        }
        else
            return 2;
    }

    private int checkEmail(String email) {
        if (email.length() == 0){
            return 1;
        }
        else if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return 0;
        }
        else
            return 2;
    }



    private void sendTextMessage(String phoneNumber){
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        Toast.makeText(getBaseContext(), "Sending message", Toast.LENGTH_LONG).show();
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case Activity.RESULT_FIRST_USER:
                        Toast.makeText(getBaseContext(), "SMS not sent, there might be something wrong with the phone number", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));

        smsManager.sendTextMessage(phoneNumber, null, "You have been set to my emergency contact", sentPI, null);
    }

    private void checkAllPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    1);
        }
    }
}
