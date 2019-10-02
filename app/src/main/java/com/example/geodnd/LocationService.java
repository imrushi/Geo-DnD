package com.example.geodnd;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service{

    private static final String TAG = "LocationService";

    private FusedLocationProviderClient mFusedLocationClient;
     private final static long UPDATE_INTERVAL = 4 * 1000; /*  4 secs */
    private final static long FASTEST_INTERVAL = 2000;  /*2 sec */
    DatabaseHelper mDatabaseHelper;
    String address,state;
    float radius;
    private AudioManager myAudioManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabaseHelper = new DatabaseHelper(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            Intent activityIntent = new Intent(this,MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_LOW);
           // channel.setDescription("Geo Dnd Service");
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifytest)
                    .setContentTitle("Geo Dnd")
                    .setContentText("Radar is enable")
                    .setColor(Color.argb(100,144,111,210))
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setContentIntent(contentIntent).build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResult: got location result.");


                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            try {


                                            Cursor data = mDatabaseHelper.getLatlon();
                                            while (data.moveToNext()) {
                                                address = data.getString(5);
                                                radius = data.getInt(4);
                                                state = data.getString(3);
                                                Log.d(TAG, "onLocationResult: addrees:" + address);
                                                String[] words = address.split(",");
                                                double mLatitude = Double.valueOf(words[0]);
                                                double mLongitude = Double.parseDouble(words[1]);
                                                Log.d(TAG, "onLocationResult: current location:" + location.getLatitude() + ", " + location.getLongitude());
                                                float[] distance = new float[2];
                                                Location.distanceBetween(location.getLatitude(), location.getLongitude(), mLatitude, mLongitude, distance);
                                                //Log.d(TAG, "onMyLocationChange: distance" + distance[0] + "," +distance[1]);
                                                if (distance[0] > radius) {
                                                   // Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] + "Radius:" + radius, Toast.LENGTH_LONG).show();
                                                    Log.d(TAG, "onMyLocationChange: Outside");
                                                } else {
                                                   // Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + "Radius:" + radius, Toast.LENGTH_LONG).show();
                                                    Log.d(TAG, "onMyLocationChange: Inside");
                                                    if (state.equals("Silent")){
                                                        Toast.makeText(LocationService.this,"Silent",Toast.LENGTH_SHORT).show();
                                                        myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                                    }else if(state.equals("Vibrate")){
                                                        Toast.makeText(LocationService.this,"Vibrate",Toast.LENGTH_SHORT).show();
                                                        myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                                    }else if (state.equals("DnD")){
                                                        Toast.makeText(LocationService.this,"DnD",Toast.LENGTH_SHORT).show();
                                                    }else if (state.equals("General")){
                                                        Toast.makeText(LocationService.this,"General",Toast.LENGTH_SHORT).show();
                                                        myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                                    }
                                                }
                                            }



                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }
}
