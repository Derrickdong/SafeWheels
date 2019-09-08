package com.project.safewheels;


import android.os.Bundle;
import androidx.fragment.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;

public class EmergencyFragment extends Fragment {

    View vEmergency;
    String[] strs;
    Button btn_submit;
    Button btn_change;
    EditText et_phone;
    EditText et_email;
    TextView tv_phone;
    TextView tv_email;
    FileOutputStream outputStream;
    final static String fileName = "emergency";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        vEmergency = inflater.inflate(R.layout.fragment_emergency, container, false);
        strs = new String[2];
        btn_change = (Button) vEmergency.findViewById(R.id.btn_change);
        btn_submit = (Button) vEmergency.findViewById(R.id.btn_submit);
        et_email = (EditText) vEmergency.findViewById(R.id.et_email);
        et_phone = (EditText) vEmergency.findViewById(R.id.et_phone);
        tv_email = (TextView) vEmergency.findViewById(R.id.tv_email);
        tv_phone = (TextView) vEmergency.findViewById(R.id.tv_phone);
//        if (readFile()){
//                btn_submit.setVisibility(View.VISIBLE);
//                et_phone.setVisibility(View.VISIBLE);
//                et_email.setVisibility(View.VISIBLE);
//                btn_change.setVisibility(View.GONE);
//                btn_submit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String email = et_email.getText().toString();
//                        String phone = et_phone.getText().toString();
//                        if (email.isEmpty() || phone.isEmpty()){
//                            Toast.makeText(getActivity().getApplicationContext(), "The phone and email cannot be empty", Toast.LENGTH_SHORT).show();
//                        }else {
//                            strs[0] = phone;
//                            strs[1] = email;
//
//                        }
//                    }
//                });
//                writeToFile(strs);
//            }else{
//                btn_change.setVisibility(View.VISIBLE);
//                et_email.setVisibility(View.GONE);
//                et_phone.setVisibility(View.GONE);
//                btn_submit.setVisibility(View.GONE);
//            }
//        return vEmergency;
//    }
//
//    private void writeToFile(String[] strs){
//        try {
//            outputStream = getActivity().getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
////            outputStream.write(strs);
//            outputStream.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private boolean readFile(){
//        try {
//            Scanner reader = new Scanner(new File(fileName));
//            while (reader.hasNextLine()){
//                strs.add(reader.next());
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (strs.size() == 0){
//            return false;
//        }else{
//            return true;
//        }
//    }
        return vEmergency;
    }
}
