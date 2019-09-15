package com.project.safewheels;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.project.safewheels.Tools.ReadAndWrite;

public class UpgradeEmergencyContact extends Activity {

    Button btn_done;
    TextInputEditText et_name;
    TextInputEditText et_email;
    TextInputEditText et_phone;
    TextView tv_phone_error;
    TextView tv_error;
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
                if (checkEmail(et_email.getText().toString()) == 0){
                    if (checkPhone(et_phone.getText().toString()) == 0){
                        String str = et_name.getText().toString() + "," + et_phone.getText().toString() + "," + et_email.getText().toString();
                        ReadAndWrite.writeToFile(str, getApplicationContext());
                        Intent intent = new Intent(UpgradeEmergencyContact.this, BottomNavigation.class);
                        startActivity(intent);
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


            }
        });

        et_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_error.setVisibility(View.INVISIBLE);
            }
        });

        et_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_phone_error.setVisibility(View.INVISIBLE);
            }
        });

        String info = ReadAndWrite.readFromFile(getApplicationContext());
        if (! (info == null)){
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
}
