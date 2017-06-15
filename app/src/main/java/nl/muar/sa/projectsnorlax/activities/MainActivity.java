package nl.muar.sa.projectsnorlax.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import nl.muar.sa.projectsnorlax.util.UserPreferenceManager;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.PREFERENCE_MODE;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.PREFERRED_LOCATION;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.LAST_LOCATION;
import sa.muar.nl.projectsnorlax.R;
import static android.view.View.OnTouchListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "Creating Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentLocationText = (TextView) findViewById(R.id.currentrestauranttext);
        closingTimeText = (TextView) findViewById(R.id.closingtimetext);
        currentDayText = (TextView) findViewById(R.id.currentdaytext);
        loadCorrectLocation();
        currentLocationText.setText(currentLocation);
        days = getDates();
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
                savePreferenceMode(GPS_MODE);
                // TODO; LOAD FROM GPS
                printStuff();
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
        return new String[]{"Monday 1st", "Tuesday 2nd", "Wednesday 3rd", "Thursday 4th", "Friday 5th"};
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Log.d(TAG, "Moving...");

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN):
                Log.d(TAG, "DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                Log.d(TAG, "MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                Log.d(TAG, "UP");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
}
