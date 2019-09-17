package com.project.safewheels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.safewheels.Tools.RepairDetailAdaptor;

import java.util.ArrayList;
import java.util.List;

public class RepairDetailActivity extends AppCompatActivity {

    private EditText durationInput;
    private ListView checkStepsListView;
    private Button btnSave;
    private ImageView imageView;
    private List<String> checkSteps = new ArrayList<>();
    private String name;
    private int img;
    private RepairDetailAdaptor repairDetailAdaptor;
    private SharedPreferences rph;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_detail);

        Intent intent = getIntent();
        rph = getSharedPreferences("safeWheels",Context.MODE_PRIVATE);
        editor = rph.edit();
        name = intent.getStringExtra("baName");
        img = intent.getIntExtra("baImage", 0);

        checkStepsListView = (ListView) findViewById(R.id.check_steps);
        durationInput = (EditText) findViewById(R.id.duration_input);
        imageView = (ImageView) findViewById(R.id.detail_img);
        btnSave = (Button) findViewById(R.id.btn_save);

        imageView.setImageResource(img);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = durationInput.getText().toString();
                if (input != null && !"".equals(input)) {
                    editor.putString(name, input);
                    editor.commit();
                    Intent intent = new Intent(RepairDetailActivity.this, BottomNavigation.class);
                    startActivity(intent);
                }

            }
        });

        switch (name) {
            case "Breaks(front and rear)":
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                break;
            case "Tires(front and rear)":
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                break;
            case "Chain":
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                break;
            case "Crank & Gear":
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                break;
            case "FlashLight(front and rear)":
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                checkSteps.add("Brake Levels are easily accessible");
                break;
            default:
                break;
        }
        repairDetailAdaptor = new RepairDetailAdaptor(getApplicationContext());
        repairDetailAdaptor.setList(checkSteps);
        checkStepsListView.setAdapter(repairDetailAdaptor);


    }

}