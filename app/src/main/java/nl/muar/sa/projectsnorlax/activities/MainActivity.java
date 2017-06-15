package nl.muar.sa.projectsnorlax.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import sa.muar.nl.projectsnorlax.R;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "Main Activity";

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
}
