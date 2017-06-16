package nl.muar.sa.projectsnorlax;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.muar.sa.projectsnorlax.activities.MainActivity;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CompareTimeTest {

    @Rule
    public ActivityTestRule<MainActivity> mainRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void openingLongTest(){
        MainActivity main = mainRule.getActivity();
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.UK);
        String exampleTime = "09:00";
        String openingString = "12:00";
        String closingString = "14:00";

        Date d = null;
        try {
            d = df.parse(exampleTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result = main.compareTime(openingString, closingString, d);
        assertEquals(result, mainRule.getActivity().getString(R.string.open_text_start_2, openingString));
    }

    @Test
    public void openingSoonTest(){
        MainActivity main = mainRule.getActivity();
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.UK);
        String exampleTime = "11:42";
        String openingTime = "12:00";
        String closingTime = "14:00";

        Date d = null;
        try {
            d = df.parse(exampleTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result = main.compareTime(openingTime, closingTime, d);
        assertEquals(result, mainRule.getActivity().getString(R.string.open_text_start_1, 18));
    }


    @Test
    public void openTest(){
        MainActivity main = mainRule.getActivity();
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.UK);
        String exampleTime = "13:00";
        String openingTime = "12:00";
        String closingTime = "14:00";

        Date d = null;
        try {
            d = df.parse(exampleTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result = main.compareTime(openingTime, closingTime, d);
        assertEquals(result, mainRule.getActivity().getString(R.string.close_text_start_2, closingTime));
    }


    @Test
    public void closingSoonTest(){
        MainActivity main = mainRule.getActivity();
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.UK);
        String exampleTime = "13:45";
        String openingTime = "12:00";
        String closingTime = "14:00";

        Date d = null;
        try {
            d = df.parse(exampleTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result = main.compareTime(openingTime, closingTime, d);
        assertEquals(result, mainRule.getActivity().getString(R.string.close_text_start_1, 15));
    }
}
