package com.example.geodnd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.Calendar;

public class InfoFillActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton rbButton;
    EditText etDate;
    Button selectDate;
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int day;
    Calendar calendar;

    CustomAdapter adapter;
    Spinner dropDown;
    String[] states = {"General", "Slient", "Vibrate", "DnD"};
    int[] icons = {R.drawable.ic_general,R.drawable.ic_silent,R.drawable.ic_vibrate,R.drawable.ic_dnd};

    // select Daily or Date
    public void checkButton(View view){

        int radioId = radioGroup.getCheckedRadioButtonId();
        rbButton = findViewById(radioId);

        switch (view.getId()){
            case R.id.rbDate:
                etDate.setVisibility(View.VISIBLE);
                selectDate.setVisibility(View.VISIBLE);
                break;

            case R.id.rbDaily:
                etDate.setVisibility(View.INVISIBLE);
                selectDate.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_fill);

        radioGroup = findViewById(R.id.radioGroup);
        etDate = findViewById(R.id.etDate);
        selectDate = findViewById(R.id.selectDate);

        // Drop down option
        dropDown = (Spinner)findViewById(R.id.dropDown);

        adapter = new CustomAdapter(this,states,icons);

        dropDown.setAdapter(adapter);

    }
}
