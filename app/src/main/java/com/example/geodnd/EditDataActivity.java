package com.example.geodnd;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geodnd.R;

import java.util.Calendar;

public class EditDataActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    DatabaseHelper mDatabaseHelper;
    static EditText etLocName;
    EditText etDate,etNameTask,etRadius;
    Button selectDate,Delete,Save;
    Switch dateSwitch;
    private String selectedName,selectedState,selectedLatlong,selectedDD;
    private int selectedID,selectedRadius;
    String dailydate;

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
  /*  public void checkButton(View view){

        int radioId = radioGroup.getCheckedRadioButtonId();
        rbButton = (RadioButton) findViewById(radioId);
        boolean dailyDate = ((RadioButton) view).isChecked();


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

    }*/
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
        String setRadius = "";
        setRadius = etRadius.getText().toString();
        Log.d(TAG, "Gmap: setRadius:"+setRadius);
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("setRadius",setRadius);
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
        dateSwitch = (Switch)findViewById(R.id.switchDD);
        adapter = new CustomAdapter(this,states,icons);

        dropDown.setAdapter(adapter);
        dateSwitch.setOnCheckedChangeListener(this);
        btnAddUp();

    }
    public void onCheckedChanged (CompoundButton buttonView, boolean isChecked){

        switch (buttonView.getId()) {
            case R.id.switchDD:
                if (isChecked == false) {
                    etDate.setText("");
                } else {

                }
                break;
        }
    }

    public void btnAddUp(){
        Intent receivedIntent = getIntent();

        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedName = receivedIntent.getStringExtra("name");
        selectedState = receivedIntent.getStringExtra("state");
        selectedRadius = receivedIntent.getIntExtra("radius",-1);
        selectedLatlong = receivedIntent.getStringExtra("latlong");
        selectedDD = receivedIntent.getStringExtra("dd");

        Log.d(TAG, "btnAddUp: "+ selectedLatlong);
        etNameTask.setText(selectedName);
        etRadius.setText(""+selectedRadius);
        if (selectedState != null) {
            int spinnerPosition = adapter.getPosition(selectedState);
            dropDown.setSelection(spinnerPosition);
        }
        etLocName.setText(selectedLatlong);
        if (!selectedDD.equals("Daily")){
            dateSwitch.setChecked(true);
            etDate.setText(selectedDD);
            dailydate = etDate.getText().toString();
        }
        else {
            dateSwitch.setChecked(false);
            dailydate = "Daily";
        }



        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch dayTypeSwitch = (Switch)findViewById(R.id.switchDD);
                if (dayTypeSwitch.isChecked()){

                    dailydate = etDate.getText().toString();
                }
                else {
                    dailydate = "Daily";
                }
               String state = dropDown.getSelectedItem().toString();
               int rad = Integer.parseInt(etRadius.getText().toString());
               String item = etNameTask.getText().toString();
               // String rad = etRadius.getText().toString();
               String loc = etLocName.getText().toString();
               // && rad != 0 && !loc.equals("")
                if (!item.equals("") ){
                    mDatabaseHelper.updateName(item,selectedID,selectedName,dailydate,state,rad,loc);
                    Toast.makeText(EditDataActivity.this,"Data updated Successfully!!",Toast.LENGTH_SHORT).show();
                   // MainActivity.adapter.notify();
                }else {
                    Toast.makeText(EditDataActivity.this,"You must fill all fields",Toast.LENGTH_SHORT).show();
                }
            }

        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseHelper.deleteName(selectedID,selectedName);
                Toast.makeText(EditDataActivity.this,"Record Deleted Successfully!!",Toast.LENGTH_SHORT).show();
                //MainActivity.adapter.notify();
            }
        });
    }

}
