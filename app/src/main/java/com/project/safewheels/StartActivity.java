package com.project.safewheels;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.project.safewheels.Tools.ReadAndWrite;

public class StartActivity extends AppCompatActivity {



    private Button btn_start;
    Intent intent;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        toolbar = (Toolbar)findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);

        checkAllPermission();

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setAlpha(0.75f);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = ReadAndWrite.readFromFile(getApplicationContext(), 1);
                if (info.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please set the emergency contact before get into the app!", Toast.LENGTH_LONG).show();
                    intent = new Intent(StartActivity.this, UpgradeEmergencyContact.class);
                }else{
                    intent = new Intent(StartActivity.this, BottomNavigation.class);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_info){
            Intent intent = new Intent(StartActivity.this, Instruction.class);
            startActivity(intent);
        }
        return true;
    }

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

        if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
        }
    }



}
