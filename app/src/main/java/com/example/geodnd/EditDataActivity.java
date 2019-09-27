package com.example.geodnd;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.geodnd.R;

import java.util.Calendar;

public class EditDataActivity extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    EditText etLocName;
    EditText etDate,etNameTask,etRadius;
    Button selectDate,Delete,Save;
    RadioGroup radioGroup;
    RadioButton rbButton;
    private String selectedName,selectedState,selectedLatlong;
    private int selectedID,selectedRadius;

    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int day;
    Calendar calendar;
    String drd;
    private static final String TAG = "InfoFillActivity";


    CustomAdapter adapter;
    Spinner dropDown;
    String[] states = {"General", "Silent", "Vibrate", "DnD"};
    int[] icons = {R.drawable.ic_general,R.drawable.ic_silent,R.drawable.ic_vibrate,R.drawable.ic_dnd};

    // select Daily or Date
    public void checkButton(View view){

        int radioId = radioGroup.getCheckedRadioButtonId();
        rbButton = (RadioButton) findViewById(radioId);
        String da = "Daily";
        String dat = "Date";
        boolean dailyDate = ((RadioButton) view).isChecked();

        if (da == rbButton.getText())
        {
            drd = "Daily";
        }
        else {
            drd = etDate.getText().toString();
        }

        switch (view.getId()){
            case R.id.rbDate:
                etDate.setVisibility(View.VISIBLE);
                selectDate.setVisibility(View.VISIBLE);
            //    if (dailyDate)
              //      drd = etDate.getText().toString();
                break;

            case R.id.rbDaily:
                etDate.setVisibility(View.INVISIBLE);
                selectDate.setVisibility(View.INVISIBLE);
              //  if (dailyDate)
                //    drd = "Daily";
                break;
        }

    }
    // Calander button
    public void pickDate(View view) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(EditDataActivity.this,
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_layout);

        etLocName = (EditText)findViewById(R.id.etLocName);
        etDate = (EditText)findViewById(R.id.etDate);
        etNameTask = (EditText)findViewById(R.id.etNameTask);
        etRadius = (EditText)findViewById(R.id.etRadius);
        mDatabaseHelper = new DatabaseHelper(this);
        dropDown = (Spinner)findViewById(R.id.dropDown);
        Save = (Button)findViewById(R.id.Save);
        Delete = (Button)findViewById(R.id.Delete);

        adapter = new CustomAdapter(this,states,icons);

        dropDown.setAdapter(adapter);

        btnAddUp();

    }

    public void btnAddUp(){
        Intent receivedIntent = getIntent();

        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedName = receivedIntent.getStringExtra("name");
        selectedState = receivedIntent.getStringExtra("state");
        selectedRadius = receivedIntent.getIntExtra("radius",-1);
        selectedLatlong = receivedIntent.getStringExtra("latlong");
        Log.d(TAG, "btnAddUp: "+ selectedLatlong);
        etNameTask.setText(selectedName);
//        etRadius.setText(selectedRadius);
        etLocName.setText(selectedLatlong);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String state = dropDown.getSelectedItem().toString();
               int rad = Integer.parseInt(etRadius.getText().toString());
               String item = etNameTask.getText().toString();
               // String rad = etRadius.getText().toString();
               String loc = etLocName.getText().toString();
               // && rad != 0 && !loc.equals("")
                if (!item.equals("") ){
                    mDatabaseHelper.updateName(item,selectedID,selectedName,state,rad,loc);
                    MainActivity.adapter.notify();
                }else {
                    Toast.makeText(EditDataActivity.this,"You must fill all fields",Toast.LENGTH_SHORT).show();
                }
            }

        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.deleteName(selectedID,selectedName);
                Toast.makeText(EditDataActivity.this,"Record Deleted From Database",Toast.LENGTH_SHORT).show();
                MainActivity.adapter.notify();
            }
        });
    }

}
