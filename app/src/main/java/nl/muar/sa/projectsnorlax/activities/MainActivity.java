package nl.muar.sa.projectsnorlax.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import nl.muar.sa.projectsnorlax.db.EatContract;
import nl.muar.sa.projectsnorlax.db.EatHelper;
import sa.muar.nl.projectsnorlax.R;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "Main Activity";
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitude;
    private double longitude;
    private Location lastLocation;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private Cursor locationDbCursor;

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
//Stuff for master
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
                            compareDistance(lastLocation);

                        } else {
                            Log.w(TAG, "getLastLocation:exception "+ task.getException());
                        }
                    }
                });
    }
    private void initialiseLocationDb(){
        EatHelper dbHelper = new EatHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                EatContract.Restaurant._ID,
                EatContract.Restaurant.COLUMN_NAME_NAME,
                EatContract.Restaurant.COLUMN_NAME_LATITUDE,
                EatContract.Restaurant.COLUMN_NAME_LONGITUDE,
        };
        String selection = EatContract.Restaurant.COLUMN_NAME_NAME + "= ?";
        String[] selectionArgs = {"Guildford"}; //TODO:make it return everything not just Guildford

        Cursor cursor = db.query(EatContract.Restaurant.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }
    private String compareDistance(Location location) {
        initialiseLocationDb();

        double distance = 0;
        boolean firstLocationDone = false;
        Location closestLocation = null;
        while(locationDbCursor.moveToNext()) {
            if(firstLocationDone==false){
                closestLocation = new Location(locationDbCursor.getString(locationDbCursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_NAME)));
                closestLocation.setLatitude(locationDbCursor.getFloat(locationDbCursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LATITUDE)));
                closestLocation.setLatitude(locationDbCursor.getFloat(locationDbCursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LONGITUDE)));

                firstLocationDone=true;
            }

            Location tempOfficeLocation = new Location(locationDbCursor.getString(locationDbCursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_NAME)));
            tempOfficeLocation.setLatitude(locationDbCursor.getFloat(locationDbCursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LATITUDE)));
            tempOfficeLocation.setLatitude(locationDbCursor.getFloat(locationDbCursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LONGITUDE)));

            float distance2 = location.distanceTo(tempOfficeLocation);

            if(distance2<distance){
                distance = distance2;
                closestLocation = tempOfficeLocation;
            }

        }
        locationDbCursor.close();
        return closestLocation.getProvider();
    }

}
