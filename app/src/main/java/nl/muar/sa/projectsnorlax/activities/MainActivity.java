package nl.muar.sa.projectsnorlax.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    //private EatHelper eatHelper;
    CountDownTimer cdt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "Creating Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void compareTime(){
        int openingDif = 0;
        int closingDif = 0;

        //Uncomment the following lines when helper class and method are implemented...
        //Cursor cursor = eatHelper.getTimesByRestaurantName();
        //String openString = cursor.getString(cursor.getColumnIndexOrThrow("opening_time"));
        //String closeString = cursor.getString(cursor.getColumnIndexOrThrow("closing_time"));

        String openString = "12:00";
        String closeString = "14:30";

        try{
            openingDif = checkTime(openString);
            closingDif = checkTime(closeString);

        } catch(ParseException e){
            Log.w(TAG, "Failed to parse given date to calendar object");
        }
        TextView timeText = (TextView) findViewById(R.id.adjust_height); //Replace with actual id of text box
        if(openingDif > 0 && openingDif < 240){
            if(openingDif < 30){
                timeText.setText(R.string.open_text_start_1 + openingDif + R.string.open_text_end);
            } else {
                timeText.setText(R.string.open_text_start_2 + openString);
            }
        } else if(closingDif > 0 && closingDif < 30){
            timeText.setText(R.string.close_text_start + closingDif + R.string.open_text_end);
        }
    }

    public void startOpenChecker(){
        cdt = new CountDownTimer(120_000, 30_000){
            public void onTick(long millisUntilFinished){
                compareTime();
            }

            public void onFinish(){
                startOpenChecker();
            }
        }.start();
    }

    public int checkTime(String openString) throws ParseException {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.UK);
        Date eventTime = timeFormat.parse(openString);
        int eventMinute = getMinuteOfDay(eventTime);
        int currentMinute = getMinuteOfDay(new Date());
        return eventMinute - currentMinute;
    }

    public int getMinuteOfDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return (c.get(Calendar.HOUR_OF_DAY) * 60) + (c.get(Calendar.MINUTE));
    }
}
