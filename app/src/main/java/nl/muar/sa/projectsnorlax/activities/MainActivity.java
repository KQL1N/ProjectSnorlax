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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nl.muar.sa.projectsnorlax.db.EatContract;
import nl.muar.sa.projectsnorlax.db.EatHelper;
import nl.muar.sa.projectsnorlax.parser.Restaurant;
import nl.muar.sa.projectsnorlax.providers.MenuItemCursorAdapter;
import nl.muar.sa.projectsnorlax.util.UserPreferenceManager;
import nl.muar.sa.projectsnorlax.R;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

         eatHelper = new EatHelper(getApplicationContext());
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
//        pager = (ViewPager) findViewById(R.id.pagingview);
//        adapter = new Page(getSupportFragmentManager());
//        pager.setAdapter(adapter);
//        PageListener listener = new PageListener();
//        pager.setOnPageChangeListener(listener);

    }

//        public int currentPage = 0;
//
//        public void onPageSelected(int position)
//        {
//            Log.i(TAG, "page selected " + position);
//            currentPage= position;
//            currentDayText.setText(days[currentPage]);
//        }

    public void getMenuRequest() {
        boolean netConnection = isNetworkAvailable();
        boolean localDbUpToDate = isLocalDbUpToDate();

        if (localDbUpToDate) {
            if (netConnection) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                final String url = "http://sa.muar.nl/weeksmenu";

                StringRequest menuRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            boolean hasBeenUsed = false;

                            @Override
                            public void onResponse(String response) {


                                if (!hasBeenUsed) {
                                    Type listOfRestaurantsType = new TypeToken<List<Restaurant>>() {
                                    }.getType();
                                    Log.i("Menu Request", "Data received: " + response);
                                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                                    List<Restaurant> restaurants = gson.fromJson(response, listOfRestaurantsType);

                                    for (Restaurant r : restaurants) {
                                        Log.d("Menu Request", "\n" + r.getName());
                                        eatHelper.insertRestaurant(r.getName(), r.getLongitude(), r.getLatitude(), r.getOpeningTime(), r.getClosingTime());

                                        //a list with the all the menu items inside it for the current restaurant;
                                        List<nl.muar.sa.projectsnorlax.parser.MenuItem> MIlist = r.getMenuItems();

                                        for (nl.muar.sa.projectsnorlax.parser.MenuItem m : MIlist) {
                                            Log.d("", "Name: " + m.getName() + "\n");
                                            Log.d("", "ID: " + m.getId() + "\n");
                                            Log.d("", "Description: " + m.getDescription() + "\n");
                                            Log.d("", "Price: " + m.getPrice() + "\n");
                                            Log.d("", "Section: " + m.getSection() + "\n");
                                            Log.d("", "Date: " + m.getDate() + "");
                                            Double itemPrice = m.getPrice().doubleValue();

                                            Long theID = eatHelper.getRestaurantIdGivenLocation(r.getName());

                                            eatHelper.insertMenuItem(m.getName(), m.getDescription(), itemPrice, m.getSection(), m.getDate(), theID);
                                        }
                                    }
                                    hasBeenUsed = true;
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Menu Request", "Unable to get restaurant data from server: " + error.getMessage());
                            }
                        });

                queue.add(menuRequest);

            } else {
                Toast.makeText(getApplicationContext(), "No network connection detected", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Cannot refresh menu while offline", Toast.LENGTH_LONG).show();
                // TODO (johnjerome) add function for checking the local repository for recent data
            }

        }

        // View Pager
//        pager = (ViewPager) findViewById(R.id.pagingview);
//        adapter = new Page(getSupportFragmentManager());
//        pager.setAdapter(adapter);
//        pager.setOffscreenPageLimit(5);
//        PageListener listener = new PageListener();
//        pager.setOnPageChangeListener(listener);
//        eatHelper = new EatHelper(this);
    }
    
//    private class PageListener extends ViewPager.SimpleOnPageChangeListener
//    {
//        public int currentPage = 0;
//
//        public void onPageSelected(int position)
//        {
//            Log.i(TAG, "page selected " + position);
//            currentPage= position;
//            currentDayText.setText(days[currentPage]);
//            fillListWithMenuItems(position);
//        }
//    }

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

        getMenuRequest();

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

    public void fillListWithMenuItems(int position){
        List<Calendar> weekRange= calculateDateRange(new Date());

        Date dayStart = weekRange.get(0).getTime();
        weekRange.get(0).add(Calendar.DAY_OF_WEEK, position);
        Date dayEnd = weekRange.get(0).getTime();
        Cursor cursor = eatHelper.getMenuItemGivenDateAndLocation(currentId, dayStart, dayEnd);
        cursor.moveToFirst();
        MenuItemCursorAdapter menuAdapter = new MenuItemCursorAdapter(this, cursor);
        Log.i(TAG, cursor.getString(cursor.getColumnIndexOrThrow(EatContract.MenuItem.COLUMN_NAME_NAME)));
        ListView dayView = (ListView)pager.getChildAt(position).findViewById(R.id.menu_item_list);
        dayView.setAdapter(menuAdapter);
    }

    public List<Calendar> calculateDateRange(Date currentDate){
        List<Calendar> weekRange = new ArrayList<>();
        Calendar cStart = Calendar.getInstance();
        cStart.setFirstDayOfWeek(Calendar.MONDAY);
        cStart.setTime(currentDate);
        int today = cStart.get(Calendar.DAY_OF_WEEK);
        cStart.add(Calendar.DAY_OF_WEEK, - today + Calendar.MONDAY);
        cStart.set(Calendar.YEAR, Calendar.MONTH, Calendar.DATE, 0, 0, 0);
        weekRange.add(cStart);
        Log.i(TAG, "Week begins: " + cStart.getTime());
        cStart.add(Calendar.DAY_OF_WEEK, - today + Calendar.FRIDAY);
        Log.i(TAG, "Week ends: " + cStart.getTime());
        weekRange.add(cStart);
        return weekRange;
    }

    public void startOpenChecker(){
        cdt = new CountDownTimer(120_000, 30_000){
            public void onTick(long millisUntilFinished){

                Cursor cursor = eatHelper.getRestaurantOpeningClosingTimesGivenLocation(currentId);
                if (cursor != null && cursor.moveToFirst()) {
                    String openString = cursor.getString(cursor.getColumnIndexOrThrow(EatContract.Restaurant.COLUMN_NAME_OPENING_TIME));
                    String closeString = cursor.getString(cursor.getColumnIndexOrThrow(EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME));

                    String outputString = compareTime(openString, closeString, new Date());
                    TextView timeText = (TextView) findViewById(R.id.closingtimetext);
                    timeText.setText(outputString);
                } else {
                    //throw android.database.CursorIndexOutOfBoundsException;
                }


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
                        if(task.isSuccessful()){
                            log = "The task is successful";
                        }
                        Log.w(TAG, log);
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();

                            currentLocation = compareDistance(lastLocation);
                            saveLastLocation(currentLocation);

                        } else {
                            Log.w(TAG, "getLastLocation:exception "+ task.getException());
                        }
                    }
                });
    }

    private String compareDistance(Location location) {

        double distance = 0;
        boolean firstLocationDone = false;
        Location closestLocation = null;
        locationDbCursor = new EatHelper((getApplicationContext())).getAllRestaurants();
        while(locationDbCursor.moveToNext()) {
            if(!firstLocationDone){
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
        if(closestLocation == null){
            Log.w(TAG, "The closest location is null");
            return "London";
        }
        Log.w(TAG, "The closest location is NOT null");
        return closestLocation.getProvider();
    }

    private void printStuff()
    {
        Log.d(TAG, "==========MENU BUTTON PRESS==========");
        Log.d(TAG, "Saved Preference Mode: " + loadPreferenceMode());
        Log.d(TAG, "Saved Preferred Location " + loadPreferredLocation());
        Log.d(TAG, "Saved Last Location: " + loadLastLocation());
        Log.d(TAG, "Current Location: " + currentLocation);
        Log.d(TAG, "=====================================");
    }

//    private void loadRestaurants(Cursor curse)
//    {
//
//    }

    private String loadCorrectLocation()
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
                // Gete the nearest location
                // Save it to the preferences
                getLocation();
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

    private void savePreferenceMode(String mode)
    {
        UserPreferenceManager.saveValue(this, PREFERENCE_MODE, mode);
    }

    private String loadPreferenceMode()
    {
        return UserPreferenceManager.loadValue(this, PREFERENCE_MODE);
    }

    private void savePreferredLocation(String preferredLocation)
    {
        UserPreferenceManager.saveValue(this, PREFERRED_LOCATION, preferredLocation);
    }

    private String loadPreferredLocation()
    {
        return UserPreferenceManager.loadValue(this, PREFERRED_LOCATION);
    }

    private void saveLastLocation(String lastLocation)
    {
        UserPreferenceManager.saveValue(this, LAST_LOCATION, lastLocation);
    }

    private String loadLastLocation()
    {
        return UserPreferenceManager.loadValue(this, LAST_LOCATION);
    }

    private String[] getDates()
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

    private boolean isLocalDbUpToDate() {
        // EatHelper eatHelper = new EatHelper(getApplicationContext());

        // Cursor menuItems = eatHelper.getAllMenuItems();

        String menuItemDates = EatContract.MenuItem.COLUMN_NAME_DATE;
        int currentDate = Calendar.DAY_OF_MONTH;

        System.out.print(menuItemDates);
        System.out.print(currentDate);

        return false;
    }

    // Method to check for a network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        Log.i(TAG, "Checking for a network connection");

        return activeNetworkInfo != null;
    }

}
