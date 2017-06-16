package nl.muar.sa.projectsnorlax.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import nl.muar.sa.projectsnorlax.util.UserPreferenceManager;
import nl.muar.sa.projectsnorlax.R;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.LAST_LOCATION;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.PREFERENCE_MODE;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.PREFERRED_LOCATION;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "Main Activity";
    public static final String GPS_MODE = "nl.muar.sa.projectsnorlax.gpsmode";              // The user wants to default to the nearest locations
    public static final String LAST_MODE = "nl.muar.sa.projectsnorlax.lastmode";            // The user wants to default to the last place they looked at
    public static final String SPECIFIED_MODE = "nl.muar.sa.projectsnorlax.specifiedmode";  // The user wants to default to a specific location from the list
    public static final String LONDON = "London Blue Fin";
    public static final String GUILDFORD = "Guildford Canteen";
    public String currentLocation;
    public TextView currentLocationText;
    public TextView currentDayText;
    public TextView closingTimeText;
    public String[] days;
    private FusedLocationProviderClient mFusedLocationClient;
    private String latitudeLabel;
    private String longitudeLabel;
    private Location lastLocation;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

    private static int PAGE_NUM = 5;
    private ViewPager pager;
    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Basics
        Log.i(TAG, "Creating Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Element
        currentLocationText = (TextView) findViewById(R.id.currentrestauranttext);
        closingTimeText = (TextView) findViewById(R.id.closingtimetext);
        currentDayText = (TextView) findViewById(R.id.currentdaytext);
        loadCorrectLocation();
        currentLocationText.setText(currentLocation);
        days = getDates();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // View Pager
        pager = (ViewPager) findViewById(R.id.pagingview);
        adapter = new Page(getSupportFragmentManager());
        pager.setAdapter(adapter);
        PageListener listener = new PageListener();
        pager.setOnPageChangeListener(listener);

    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener
    {
        public int currentPage = 0;

        public void onPageSelected(int position)
        {
            Log.i(TAG, "page selected " + position);
            currentPage= position;
            currentDayText.setText(days[currentPage]);
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
    public void onBackPressed()
    {
        if (pager.getCurrentItem() == 0)
        {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        }
        else
            {
            // Otherwise, select the previous step.
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuguildford:
                Log.i(TAG, "Guildford Menu Item Clicked");
                currentLocation = GUILDFORD;
                currentLocationText.setText(currentLocation);
                saveLastLocation(GUILDFORD);
                printStuff();
                return true;

            case R.id.menulondon:
                Log.i(TAG, "London Menu Item Clicked");
                currentLocation = LONDON;
                currentLocationText.setText(currentLocation);
                saveLastLocation(LONDON);
                printStuff();
                return true;

            case R.id.defaultlastused:
                Log.i(TAG, "Use last-viewed Menu Item Clicked");
                savePreferenceMode(LAST_MODE);
                currentLocation = loadLastLocation();
                currentLocationText.setText(currentLocation);
                printStuff();
                return true;

            case R.id.defaultgps:
                Log.i(TAG, "Use GPS Menu Item Clicked");

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                getLocation();

                //savePreferenceMode(GPS_MODE);
                // TODO; LOAD FROM GPS
                //printStuff();
                return true;

            case R.id.defaultguildford:
                Log.i(TAG, "Use Guildford Menu Item Clicked");
                savePreferenceMode(SPECIFIED_MODE);
                savePreferredLocation(GUILDFORD);
                currentLocation = GUILDFORD;
                currentLocationText.setText(currentLocation);
                printStuff();
                return true;

            case R.id.defaultlondon:
                Log.i(TAG, "Use London Menu Item Clicked");
                savePreferenceMode(SPECIFIED_MODE);
                savePreferredLocation(LONDON);
                currentLocation = LONDON;
                currentLocationText.setText(currentLocation);
                printStuff();
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

                            double a = lastLocation.getLatitude();

                            lastLocation.getLongitude();
                            Log.w(TAG, "getLastLocation worked" + a);
                        } else {
                            Log.w(TAG, "getLastLocation:exception "+ task.getException());
                        }
                    }
                });
    }

    private void compareDistance(Location location) {
        double num = location.getLatitude();
        String test = location.toString();
        int duration = Toast.LENGTH_SHORT;
        Log.d(TAG, "Got distances "+ num + test);
        Context context = getApplicationContext();
        CharSequence helpText =""+num+"";
        Toast toast = Toast.makeText(context, helpText, duration);
        toast.show();

        Toast toast2 = Toast.makeText(context, test, duration);
        toast2.show();
    }


    public void printStuff()
    {
        Log.d(TAG, "==========MENU BUTTON PRESS==========");
        Log.d(TAG, "Saved Preference Mode: " + loadPreferenceMode());
        Log.d(TAG, "Saved Preferred Location " + loadPreferredLocation());
        Log.d(TAG, "Saved Last Location: " + loadLastLocation());
        Log.d(TAG, "Current Location: " + currentLocation);
        Log.d(TAG, "=====================================");
    }

    public String loadCorrectLocation()
    {
        String output = "";

        String locationMode = loadPreferenceMode();
        switch (locationMode)
        {
            case SPECIFIED_MODE:
                // Load the string of the last location
                // If its not empty, its not GPS, and its not LAST, then...
                //      Set the location to that
                // If it is empty, then
                //      if that fails, then set it to Guildford
                String defaultLocation = loadPreferredLocation();
                currentLocation = defaultLocation;
                saveLastLocation(defaultLocation);
                break;

            case GPS_MODE:
                // Get Trev's GPS thing result
                // Set the current location to that location
                // Set the last location to that location
                // TODO: USE TREV'S METHOD
                String GPSlocation = "";
                currentLocation = GPSlocation;
                saveLastLocation(GPSlocation);

                break;

            case LAST_MODE:
                // Load the last location
                // Set the current location to that location
                currentLocation = loadLastLocation();
                break;

            default:
                savePreferredLocation(LAST_MODE);
                break;
        }

        return output;
    }

    public void savePreferenceMode(String mode)
    {
        UserPreferenceManager.saveValue(this, PREFERENCE_MODE, mode);
    }

    public String loadPreferenceMode()
    {
        return UserPreferenceManager.loadValue(this, PREFERENCE_MODE);
    }

    public void savePreferredLocation(String preferredLocation)
    {
        UserPreferenceManager.saveValue(this, PREFERRED_LOCATION, preferredLocation);
    }

    public String loadPreferredLocation()
    {
        return UserPreferenceManager.loadValue(this, PREFERRED_LOCATION);
    }

    public void saveLastLocation(String lastLocation)
    {
        UserPreferenceManager.saveValue(this, LAST_LOCATION, lastLocation);
    }

    public String loadLastLocation()
    {
        return UserPreferenceManager.loadValue(this, LAST_LOCATION);
    }

    public String[] getDates()
    {
        // TODO: REPLACE WITH DATABASE DATES
        return new String[]{"Monday 1st", "Tuesday 2nd", "Wednesday 3rd", "Thursday 4th", "Friday 5th"};
    }
}