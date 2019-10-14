package com.project.safewheels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.safewheels.Tools.RepairDetailAdaptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class handle the operations that when you make safety checking
 */

public class RepairDetailActivity extends AppCompatActivity {

    private EditText durationInput;
    private ListView checkStepsListView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnSave;
    private Button btnSkip;
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

        getSteps();

        radioGroup = findViewById(R.id.remiders);

        checkStepsListView = (ListView) findViewById(R.id.check_steps);
        repairDetailAdaptor = new RepairDetailAdaptor(getApplicationContext());
        repairDetailAdaptor.setList(checkSteps);
        checkStepsListView.setAdapter(repairDetailAdaptor);
        checkStepsListView.setItemsCanFocus(true);
        checkStepsListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        checkStepsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(getResources().getColor(R.color.quantum_lightgreen));
            }
        });

        durationInput = (EditText) findViewById(R.id.duration_input);
        imageView = (ImageView) findViewById(R.id.detail_img);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSkip = (Button)findViewById(R.id.btn_back);

        imageView.setImageResource(img);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = "";
                int selectedId = radioGroup.getCheckedRadioButtonId();
                switch (selectedId){
                    case R.id.first:
                        input = 60+"";
                        break;
                    case R.id.second:
                        input = 30+"";
                        break;
                    case R.id.third:
                        input = 15 + "";
                        break;
                    case R.id.other:
                        input = durationInput.getText().toString();
                        break;
                }
                if (input.equals("")){
                    Toast.makeText(RepairDetailActivity.this, "The remind day cannot ne empty", Toast.LENGTH_SHORT).show();
                }else{
                    editor.putString(name + "duration", input);
                    editor.putString(name + "startDate", new Date().toString());
                    editor.apply();
                    Intent intent = new Intent(RepairDetailActivity.this, BottomNavigation.class);
                    intent.putExtra("from", "check");
                    startActivity(intent);
                }
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getSteps(){
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
    }
}