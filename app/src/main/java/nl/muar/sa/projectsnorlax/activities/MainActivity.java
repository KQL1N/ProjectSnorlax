package nl.muar.sa.projectsnorlax.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;
import nl.muar.sa.projectsnorlax.providers.MenuItemCursorAdapter;
import nl.muar.sa.projectsnorlax.db.EatContract;
import nl.muar.sa.projectsnorlax.db.EatHelper;
import nl.muar.sa.projectsnorlax.util.UserPreferenceManager;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.LAST_LOCATION;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.PREFERENCE_MODE;
import static nl.muar.sa.projectsnorlax.util.UserPreferenceManager.PREFERRED_LOCATION;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.muar.sa.projectsnorlax.R;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "Main Activity";
    private EatHelper eatHelper;
    CountDownTimer cdt;

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
    private Cursor locationDbCursor;

    private long currentId = 1;
    private final static String TIME_FORMAT = "HHmm";
    private int[] menuLists = {R.id.menu_item_list};

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

        // Boolean for whether network connection is true or false
        boolean netConnection = isNetworkAvailable();

        // If statement for true or false network connection
        if (netConnection) {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            // URL of web server
            String url = "http://sa.muar.nl/weeksmenu";

            StringRequest menuRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Simon's Parsing Stuff
                }
            }, new Response.ErrorListener() {
                // Iterating through the views making them red to show that there was an error
                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO (johnjerome) add function to check local repository in event of web server request error
                }
            });
            queue.add(menuRequest);
        } else {
            Toast.makeText(getApplicationContext(), "No network connection detected", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Cannot refresh menu while offline", Toast.LENGTH_LONG).show();
            //TODO (johnjerome) add function for checking the local repository for recent data
        }

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

    @Override
    protected void onPause(){
        super.onPause();
        cdt.cancel();
    }

    @Override
    protected void onResume(){
        super.onResume();
        startOpenChecker();
    }

    public String compareTime(String openString, String closeString, Date current) {
        int openingDif = 0;
        int closingDif = 0;

        try {
            openingDif = checkTime(openString, current);
            Log.i(TAG, "Minutes till open: " + openingDif);
            closingDif = checkTime(closeString, current);
            Log.i(TAG, "Minutes till closing " + closingDif);

        } catch (ParseException e) {
            Log.w(TAG, "Failed to parse given date to calendar object");
        }

        if (openingDif > 0) {
            if (openingDif < 30) {
                return getString(R.string.open_text_start_1, openingDif);
            } else {
                return getString(R.string.open_text_start_2, openString);
            }
        } else if (closingDif > 0) {
            if (closingDif < 30) {
                return getString(R.string.close_text_start_1, closingDif);
            } else {
                return getString(R.string.close_text_start_2, closeString);
            }
        } else {
            return "";
        }
    }

    public void fillListWithMenuItems(){
        List<Calendar> weekRange= calculateDateRange(new Date());

        for(int i = 0; i <= 5; i++){
            Date dayStart = weekRange.get(0).getTime();
            weekRange.get(0).add(Calendar.DAY_OF_WEEK, 1);
            Date dayEnd = weekRange.get(0).getTime();
            //Cursor cursor = eatHelper.getMenuItemByDateAndLocation(currentLocation, dayStart, dayEnd);
            Cursor cursor = null; //Replace this when method above works
            MenuItemCursorAdapter menuAdapter = new MenuItemCursorAdapter(this, cursor);
            ListView dayView = (ListView)pager.getChildAt(i).findViewById(R.id.menu_item_list);
            dayView.setAdapter(menuAdapter);
        }
    }

    public List<Calendar> calculateDateRange(Date currentDate){
        List<Calendar> weekRange = new ArrayList<Calendar>();
        Calendar cStart = Calendar.getInstance();
        cStart.setFirstDayOfWeek(Calendar.MONDAY);
        cStart.setTime(currentDate);
        int today = cStart.get(Calendar.DAY_OF_WEEK);
        cStart.add(Calendar.DAY_OF_WEEK, - today + Calendar.MONDAY);
        cStart.set(Calendar.YEAR, Calendar.MONTH, Calendar.DATE, 0, 0, 0);
        weekRange.add(cStart);
        cStart.add(Calendar.DAY_OF_WEEK, - today + Calendar.FRIDAY);
        weekRange.add(cStart);
        return weekRange;
    }

    public void startOpenChecker(){
        cdt = new CountDownTimer(120_000, 30_000){
            public void onTick(long millisUntilFinished){

                Cursor cursor = eatHelper.getAllMenuItemsGivenRestaurantId(currentId);
                String openString = cursor.getString(cursor.getColumnIndexOrThrow("opening_time"));
                String closeString = cursor.getString(cursor.getColumnIndexOrThrow("closing_time"));

                String outputString = compareTime(openString, closeString, new Date());
                TextView timeText = (TextView) findViewById(R.id.closingtimetext);
                timeText.setText(outputString);
            }

            public void onFinish(){
                startOpenChecker();
            }
        }.start();
    }

    public int checkTime(String openString, Date current) throws ParseException {
        DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.UK);
        Date eventTime = timeFormat.parse(openString);
        int eventMinute = getMinuteOfDay(eventTime);
        int currentMinute = getMinuteOfDay(current);
        return eventMinute - currentMinute;
    }

    public int getMinuteOfDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return (c.get(Calendar.HOUR_OF_DAY) * 60) + (c.get(Calendar.MINUTE));
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

    // Method to check for a network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Log.i(TAG, "Checking for a network connection");

        return activeNetworkInfo != null;
    }

}
