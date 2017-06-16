package nl.muar.sa.projectsnorlax.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.List;

import nl.muar.sa.projectsnorlax.db.eatContract;
import sa.muar.nl.projectsnorlax.R;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "Main Activity";
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitude;
    private double longitude;
    private Location lastLocation;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "Creating Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            getLocation();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.i(TAG, "Building Menu");
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menugloucester:
                Log.i(TAG, "Gloucester Menu Item Clicked");
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                getLocation();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        String log="the task is not succesful";
                        if(task.isSuccessful()==true){
                            log = "The task is successful";
                        }
                        Log.w(TAG, log);
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();

                                    latitude = lastLocation.getLatitude();

                                    longitude = lastLocation.getLongitude();

                        } else {
                            Log.w(TAG, "getLastLocation:exception "+ task.getException());
                        }
                    }
                });
    }

    //iterate through the database - Hannah might do that for me
    //make a method that extracts the latitude and longituden from the cursor
    //actually work out and compare the distances


    private String compareDistance(Location location) {

        Cursor cursor; //need to initialise this (Hannah might do it for me)
        Location closestLocation = new Location(cursor.getFirstLocation);
        double distance = location.distanceTo(cursor.getFirstLocation);
        while(cursor.moveToNext()) {
            Location tempOfficeLocation = new Location(""+cursor.getName);
            tempOfficeLocation.setLatitude(cursor.getLatitude);
            tempOfficeLocation.setLongitude(cursor.getLongitude);


            float distance2 = location.distanceTo(officeLocation);
            if(distance2<distance){
                distance = distance2;
                closestLocation = tempOfficeLocation;
            }

        }
        cursor.close();
        return closestLocation.getName();
    }

}
