package nl.muar.sa.projectsnorlax.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserPreferenceManager
{
    private static final String TAG = "User Preference Manager";
    public static final String PREFERENCE_MODE = "nl.muar.sa.projectsnorlax.preferrencemode";
    public static final String PREFERRED_LOCATION = "nl.muar.sa.projectsnorlax.preferredlocation";
    public static final String LAST_LOCATION = "nl.muar.sa.projectsnorlax.lastlocation";

    public static void saveValue(Activity activity, String stringToSave, String value)
    {
        Log.v(TAG, "Saving " + value + " to " + stringToSave);
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(stringToSave, value);
        editor.commit();
    }

    public static String loadValue(Activity activity, String stringToLoad)
    {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        String output = preferences.getString(stringToLoad,"Guildford");
        Log.v(TAG, "Loading " + output + " from " + stringToLoad);
        return output;
    }
}