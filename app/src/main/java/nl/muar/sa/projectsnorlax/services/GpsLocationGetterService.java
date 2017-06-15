package nl.muar.sa.projectsnorlax.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by android-eng on 15/06/17.
 */

public class GpsLocationGetterService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LocationManager locationManager = getSystemService(Context.LOCATION_SERVICE);
    }
}
