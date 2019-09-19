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
import java.util.Date;
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
                if (input != null && !input.equals("")) {
                    editor.putString(name + "duration", input);
                    editor.putString(name + "startDate", new Date().toString());
                    editor.apply();
                    finish();
//                    Intent intent = new Intent(RepairDetailActivity.this, BottomNavigation.class);
//                    startActivity(intent);
                }

            }
        });

        switch (name) {
            case "Breaks(front and rear)":
                checkSteps.add("Brake levers are easily accessible");
                checkSteps.add("No excess pull required on the levers");
                checkSteps.add("Brake pads are clear from the rim");
                checkSteps.add("All components are tight ");
                checkSteps.add("No frayed cables");
                break;
            case "Tires(front and rear)":
                checkSteps.add("Check the condition (not worn or cracked)");
                checkSteps.add("Check pressure");
                checkSteps.add("No spokes missing or loose");
                checkSteps.add("Wheels roll smoothly");
                checkSteps.add("Rims run free of brakes");
                break;
            case "Chain":
                checkSteps.add("Chain remains on sprockets");
                checkSteps.add("No sign of rust or stiff links");
                checkSteps.add("No excessive play in the chain");
                break;
            case "Crank & Gear":
                checkSteps.add("Hold pedal crank arms and check there is no side-to-side movement");
                checkSteps.add("Derailleur is clear of spokes");
                checkSteps.add("All gears can be selected");
                checkSteps.add("Ensure gears don’t slip");
                break;
            case "FlashLight(front and rear)":
                checkSteps.add("Make sure front and rear flashlights are functioning");
                checkSteps.add("Front and rear lights are mounted and sturdy");
                checkSteps.add("Lights are pointing towards correct direction");
                checkSteps.add("Check battery");
                break;
            default:
                break;
        }
        repairDetailAdaptor = new RepairDetailAdaptor(getApplicationContext());
        repairDetailAdaptor.setList(checkSteps);
        checkStepsListView.setAdapter(repairDetailAdaptor);
    }
}