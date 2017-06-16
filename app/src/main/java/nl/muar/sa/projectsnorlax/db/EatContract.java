package nl.muar.sa.projectsnorlax.db;

import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by HCEvans on 15/06/17.
 */

public class EatContract {

    private EatContract(){}

    public static class Restaurant implements BaseColumns{
        public static final String TABLE_NAME = "restaurant";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_OPENING_TIME = "opening_time";
        public static final String COLUMN_NAME_CLOSING_TIME = "closing_time";
    }

    public static class MenuItem implements BaseColumns{
        public static final String TABLE_NAME = "menu_item";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_SECTION= "section";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_RESTARAUNT_ID = "restaurant_ID";
    }
}