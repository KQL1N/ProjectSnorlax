package nl.muar.sa.projectsnorlax.db;

import android.provider.BaseColumns;

public class eatContract {

    private eatContract(){}

    public static class Location implements BaseColumns{
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_OPENINGTIME = "openingTime";
        public static final String COLUMN_NAME_CLOSINGTIME = "closingTime";
    }

    public static class MenuItem implements BaseColumns{
        public static final String TABLE_NAME = "menuItem";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_SECTION= "section";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
