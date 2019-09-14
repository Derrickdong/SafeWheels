package com.project.safewheels;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    Button btn_start;
    TextView tv_upgrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final Intent intent = new Intent(StartActivity.this, BottomNavigation.class);
        btn_start = (Button)findViewById(R.id.btn_start);
        btn_start.setAlpha(0.8f);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        tv_upgrade = (TextView)findViewById(R.id.tv_upgrade);
        tv_upgrade.setPaintFlags(tv_upgrade.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(StartActivity.this, UpgradeEmergencyContact.class);
                startActivity(intent1);
            }
        });
    }
}
