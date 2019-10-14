package com.project.safewheels;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.safewheels.Tools.ReadAndWrite;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class handle the look of the bottom navigation and also the toolbar and falling detection
 */

public class BottomNavigation extends AppCompatActivity implements SensorEventListener {

    Toolbar toolbar;
    String phoneNumber;
    SensorManager sensorManager;
    Sensor accelerometer;
    static int sensorValuesSize = 70;
    float accelValuesX[] = new float[sensorValuesSize];
    float accelValuesY[] = new float[sensorValuesSize];
    float accelValuesZ[] = new float[sensorValuesSize];
    int index = 0;
    boolean fallDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        BottomNavigationView navView = (BottomNavigationView)findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.getMenu().findItem(R.id.dmap).setChecked(true);

        checkAllPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        String str = ReadAndWrite.readFromFile(getApplicationContext(), 1);
        phoneNumber = str.split(",")[1];
        Intent intent = getIntent();
        intent.putExtra("phoneNumber", phoneNumber);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        String code = intent.getStringExtra("from");
        if (code == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();
        }else if (code.equals("check")){
            navView.setSelectedItemId(R.id.dsecurity);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SafetyCheckFragment()).commit();
        }else if (code.equals("me")){
            navView.setSelectedItemId(R.id.dme);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmergencyFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_call){
            showCountdownDialog();
//            callEmergencyContact();
        }
        return true;
    }

    private void showCountdownDialog(){
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        final TextView numberView = new TextView(this);
        numberView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        numberView.setTextColor(Color.BLACK);
        numberView.setText("  Call " + phoneNumber + " in: ");
        final TextView countDown = new TextView(this);
        layout.addView(numberView);
        layout.addView(countDown);
        final Timer timer = new Timer(true);
        final Dialog dialog = new AlertDialog.Builder(BottomNavigation.this)
                .setView(layout)
                .setPositiveButton("call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callEmergencyContact();
                        timer.cancel();
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        timer.cancel();
                    }
                }).show();
        dialog.setCanceledOnTouchOutside(false);
        final Handler offHandler = new Handler(){
            public void handleMessage(Message msg){
                if (msg.what > 0){
                    countDown.setText(Integer.toString(msg.what));
                    countDown.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    countDown.setTextColor(Color.RED);
                }else{
                    if (dialog != null){
                        dialog.dismiss();
                    }
                    timer.cancel();
                    callEmergencyContact();
                }
                super.handleMessage(msg);
            }
        };
        TimerTask tt = new TimerTask() {
            int countTime = 7;
            @Override
            public void run() {
                if (countTime > 0){
                    countTime--;
                }
                Message msg = new Message();
                msg.what = countTime;
                offHandler.sendMessage(msg);
            }
        };
        timer.schedule(tt, 1000, 1000);
    }

    private void callEmergencyContact() {
        Toast.makeText(BottomNavigation.this, "Calling", Toast.LENGTH_LONG).show();
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + phoneNumber));
        getApplicationContext().startActivity(call);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragement = null;
            switch (item.getItemId()) {
                case R.id.dmap:
                    selectedFragement = new MapFragment();
                    break;
                case R.id.dme:
                    selectedFragement = new EmergencyFragment();
                    break;
                case R.id.dsecurity:
                    selectedFragement = new SafetyCheckFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragement).commit();
            return true;
        }
    };

    private void checkAllPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    1);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (accelerometer.getType() == Sensor.TYPE_ACCELEROMETER){
            index++;
            accelValuesX[index] = event.values[0];
            accelValuesY[index] = event.values[1];
            accelValuesZ[index] = event.values[2];

            if (index >= sensorValuesSize-1){
                index = 0;
                sensorManager.unregisterListener(this);
                if (!fallDetected){
                    callForRecognition("fall");
                }
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    private void callForRecognition(String parameter) {
        float currx = 0;
        float curry = 0;
        float currz = 0;
        double rootSquare=0.0;
        int smsCount = 0;

        for (int i = 5; i < sensorValuesSize; i++) {
            currx = accelValuesX[i];
            curry = accelValuesY[i];
            currz = accelValuesZ[i];

            if(parameter == "fall")
            {
                rootSquare = Math.sqrt(Math.pow(currx, 2) + Math.pow(curry, 2) + Math.pow(currz, 2));
                if (rootSquare < 2.0) {
                    smsCount++;
                    if (smsCount == 1) {
                        Toast.makeText(this, "Fall detected", Toast.LENGTH_SHORT).show();
                        fallDetected = true;
                        showCountdownDialog();
                    }
                }
            }
            smsCount = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
