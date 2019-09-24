package com.example.geodnd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class InfoFillActivity extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    RadioGroup radioGroup;
    RadioButton rbButton;
    static EditText etLocName;
    EditText etDate,etNameTask,etRadius;
    Button selectDate,Save;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int day;
    Calendar calendar;
    String drd;
    private static final String TAG = "InfoFillActivity";


    CustomAdapter adapter;
    Spinner dropDown;
    String[] states = {"General", "Slient", "Vibrate", "DnD"};
    int[] icons = {R.drawable.ic_general,R.drawable.ic_silent,R.drawable.ic_vibrate,R.drawable.ic_dnd};

    // select Daily or Date
    public void checkButton(View view){

        int radioId = radioGroup.getCheckedRadioButtonId();
        rbButton = (RadioButton) findViewById(radioId);
        boolean dailyDate = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.rbDate:
                etDate.setVisibility(View.VISIBLE);
                selectDate.setVisibility(View.VISIBLE);
                if (dailyDate)
                    drd = etDate.getText().toString();
                break;

            case R.id.rbDaily:
                etDate.setVisibility(View.INVISIBLE);
                selectDate.setVisibility(View.INVISIBLE);
                if (dailyDate)
                    drd = "Daily";
                break;
        }

    }
    // Calander button
    public void pickDate(View view) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(InfoFillActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etDate.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void Gmap(View view){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_fill);

        mDatabaseHelper = new DatabaseHelper(this);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        etDate = (EditText) findViewById(R.id.etDate);
        selectDate = (Button) findViewById(R.id.selectDate);
        etLocName = (EditText) findViewById(R.id.etLocName);
        etNameTask = (EditText)findViewById(R.id.etNameTask);
        etRadius = (EditText)findViewById(R.id.etRadius);
        Save = (Button)findViewById(R.id.Save);
        // Drop down option
        dropDown = (Spinner)findViewById(R.id.dropDown);

        adapter = new CustomAdapter(this,states,icons);

        dropDown.setAdapter(adapter);

        AddData();

    }

    private void tostMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    public void AddData(){
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etNameTask.getText().toString();
                String dd = drd;
                String state = dropDown.getSelectedItem().toString();
                int radius = Integer.parseInt(etRadius.getText().toString());
                String loc = etLocName.getText().toString();

                Log.d(TAG, "onClick: "+ newName + " ," + dd + " ,"+ state + " ,"+ radius + " ,"+ loc );
                boolean insertData = mDatabaseHelper.addData(newName,dd,state,radius,loc);

                if (insertData){
                    Toast.makeText(InfoFillActivity.this,"Data Successfully Inserted!",Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(InfoFillActivity.this,"Something went Wrong!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
