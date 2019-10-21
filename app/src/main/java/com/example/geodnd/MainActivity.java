package com.example.geodnd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    DatabaseHelper mDatabaseHelper;
    static ListAdapter madapter;
    private ListView mListView;
   // SwipeRefreshLayout swipeRefreshLayout;
   // private Switch myswitch;
    SharedPref sharedpref;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startLocationService();
        sharedpref = new SharedPref(this);
        if(sharedpref.loadNightModeState()==true) {
            setTheme(R.style.darktheme);
        }
        else  setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_settings);

        mListView = (ListView)findViewById(R.id.lvToDoList);
        mDatabaseHelper = new DatabaseHelper(this);
      //  swipeRefreshLayout = findViewById(R.id.SwipeRefresh);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart) {
            showStartDialog();
        }

        //getAccessPermission();

        populateListView();

      /*  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateListView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);

        return true;
    }

  /*  Switch aSwitch = findViewById(R.id.bar_switch);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    });*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
                MainActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showStartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("One Time Instruction & Permissions")
                .setMessage("Please grant this app access to your phone's 'Do Not Disturb' model!\n " +
                        "You can do this Manually via Settings > Apps & notifications > Special App Access > Do Not Disturb Access > Allow Access for this app \n" +
                        "- When you come out From the Radius you selected 'PELEASE SELECT PHONE MODE BY YOUR SELF' for eg: Vibrate,Silent,General \n" +
                        "- For Do Not Disturb Setting you have to set it Manually \n" +
                        "Enjoy ;)")
                .setPositiveButton("OPEN ALLOWED APPS LIST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                && !notificationManager.isNotificationPolicyAccessGranted()) {

                            Intent intent = new Intent(
                                    android.provider.Settings
                                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                            startActivity(intent);
                        }
                    }
                })
                .create().show();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

   /* private void getAccessPermission() {
        Log.d(TAG, "getAccessPermission: Notification permission");
        //String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
          //      Manifest.permission.ACCESS_COARSE_LOCATION};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Permissions");
        builder.setMessage("Give Prmission for auto DND & other modes:");
        builder.setPositiveButton("Asscess", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        && !notificationManager.isNotificationPolicyAccessGranted()) {

                    Intent intent = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                    startActivity(intent);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        dialog.show();
    }*/

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                MainActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.codingwithmitch.googledirectionstest.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }


    public void fabClick(View view){
        Intent intent = new Intent(getApplicationContext(), InfoFillActivity.class);

        startActivity(intent);
        MainActivity.this.finish();

    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext())
        {
            //get the value from the database in column1
            //then add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create the list adapter and set the adapter
        madapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listData){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                if(sharedpref.loadNightModeState()==true) {
                    tv.setTextColor(Color.WHITE);
                }
                else  tv.setTextColor(Color.BLACK);
                // Set the text color of TextView (ListView Item)


                // Generate ListView Item using TextView
                return view;
            }
        };
        mListView.setAdapter(madapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG,"onItemClick: You Clicked on " + name);

                Cursor data = mDatabaseHelper.getItemID(name); //get the id associated with that name
                int itemID = -1;
                String pstate = "";
                int Radius = -1;
                String Latlong = "";
                String dd = "";
                while (data.moveToNext()){
                    itemID = data.getInt(0);
                    dd = data.getString(2);
                    pstate = data.getString(3);
                    Radius = data.getInt(4);
                    Latlong = data.getString(5);
                }
                Log.d(TAG, "onItemClick: "+Radius);
                if (itemID > -1){
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent editScreenIntent = new Intent(MainActivity.this,EditDataActivity.class);
                    editScreenIntent.putExtra("id",itemID);
                    editScreenIntent.putExtra("name",name);
                    editScreenIntent.putExtra("dd",dd);
                    editScreenIntent.putExtra("state",pstate);
                    editScreenIntent.putExtra("radius",Radius);
                    editScreenIntent.putExtra("latlong",Latlong);
                    startActivity(editScreenIntent);
                }
                else {
                    Toast.makeText(MainActivity.this,"No ID associated with that name",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();

    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            //EveryThing is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google play services is working");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this,"You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
