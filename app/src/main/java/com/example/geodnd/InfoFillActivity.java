package com.example.geodnd;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;

public class InfoFillActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    DatabaseHelper mDatabaseHelper;

    static EditText etLocName;
    EditText etDate,etNameTask,etRadius;
    Button selectDate,Save;
    DatePickerDialog datePickerDialog;
    Switch dateSwitch;
    SharedPref sharedpref;

    int year;
    int month;
    int day;
    Calendar c;
    String drd = "Daily";
    private static final String TAG = "InfoFillActivity";


    CustomAdapter adapter;
    Spinner dropDown;
    String[] states = {"General", "Silent", "Vibrate", "DnD"};
    int[] icons = {R.drawable.ic_general,R.drawable.ic_silent,R.drawable.ic_vibrate,R.drawable.ic_dnd};

    // select Daily or Date
  /*  public void checkButton(View view){

        int radioId = radioGroup.getCheckedRadioButtonId();
        rbButton = (RadioButton) findViewById(radioId);

        //boolean dailyDate = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.rbDate:
                //etDate.setVisibility(View.VISIBLE);
               // selectDate.setVisibility(View.VISIBLE);
               // if (rbButton.getText().equals(dat))
               //    drd = etDate.getText().toString();
                //Log.d(TAG, "checkButton: "+ drd);
                break;

            case R.id.rbDaily:
                //etDate.setVisibility(View.INVISIBLE);
               // selectDate.setVisibility(View.INVISIBLE);
               // if (rbButton.getText().equals(da))
                    drd = "Daily";
                Log.d(TAG, "checkButton: "+ drd);
                break;
        }
    }*/
    // Calander button
    public void pickDate(View view) {
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(InfoFillActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        drd = year + "-" + (month + 1) + "-" + dayOfMonth;
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
        intent.putExtra("fromInfo","Info");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new SharedPref(this);
        if(sharedpref.loadNightModeState()==true) {
            setTheme(R.style.darktheme);
        }
        else  setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_fill);

        mDatabaseHelper = new DatabaseHelper(this);

        etDate = (EditText) findViewById(R.id.etDate);
        selectDate = (Button) findViewById(R.id.selectDate);
        etLocName = (EditText) findViewById(R.id.etLocName);
        etNameTask = (EditText)findViewById(R.id.etNameTask);
        etRadius = (EditText)findViewById(R.id.etRadius);
        Save = (Button)findViewById(R.id.Save);
        // Drop down option
        dropDown = (Spinner)findViewById(R.id.dropDown);
        dateSwitch = (Switch)findViewById(R.id.switchDD);
        adapter = new CustomAdapter(this,states,icons);

        dropDown.setAdapter(adapter);

        dateSwitch.setOnCheckedChangeListener(this);

        AddData();

    }

    public void onCheckedChanged (CompoundButton buttonView, boolean isChecked){

        switch (buttonView.getId()) {
            case R.id.switchDD:
                if (isChecked == true) {
                    etDate.setVisibility(View.VISIBLE);
                    selectDate.setVisibility(View.VISIBLE);
                } else {
                    etDate.setVisibility(View.INVISIBLE);
                    selectDate.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void tostMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void AddData(){

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Switch dayTypeSwitch = (Switch)findViewById(R.id.switchDD);

                Log.d(TAG, "onClick: Switch value::" + dayTypeSwitch.isChecked());

                if (dayTypeSwitch.isChecked()){

                    drd = etDate.getText().toString();
                }

                Log.d(TAG, "checkButton: " + drd);
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
      //          MainActivity.madapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        finish();
        startActivity(intent);
    }
}
