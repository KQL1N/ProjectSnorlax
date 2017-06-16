package nl.muar.sa.projectsnorlax.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

/*Created by HCEvans on 15/06/17.*/

public class EatHelper extends SQLiteOpenHelper{

    // If you change the DB schema, you must increment the DB version.
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "Eat.db";
    private static final String TAG = "Eat DB Helper";
    private static String SQL_ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys=ON";

    private static String SQL_CREATE_Restaurant = "CREATE TABLE " + EatContract.Restaurant.TABLE_NAME
            + "(" + EatContract.Restaurant._ID + " INTEGER PRIMARY KEY," +
            EatContract.Restaurant.COLUMN_NAME_NAME + " TEXT," +
            EatContract.Restaurant.COLUMN_NAME_LONGITUDE + " REAL," +
            EatContract.Restaurant.COLUMN_NAME_LATITUDE + " REAL," +
            EatContract.Restaurant.COLUMN_NAME_OPENING_TIME + " TEXT," +
            EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME + " TEXT)";

    private static final String SQL_DELETE_RESTAURANT_TABLE =
            "DROP TABLE IF EXISTS " +  EatContract.Restaurant.TABLE_NAME;

    private static final String SQL_DELETE_ALL_RESTAURANTS =
            "DELETE FROM " + EatContract.Restaurant.TABLE_NAME;

    private static final String SQL_CREATE_MENU_ITEM = "CREATE TABLE " + EatContract.MenuItem.TABLE_NAME
            + "(" + EatContract.MenuItem._ID + " INTEGER PRIMARY KEY," +
            EatContract.MenuItem.COLUMN_NAME_NAME + " TEXT," +
            EatContract.MenuItem.COLUMN_NAME_DESCRIPTION + " TEXT," +
            EatContract.MenuItem.COLUMN_NAME_PRICE + " REAL," +
            EatContract.MenuItem.COLUMN_NAME_SECTION + " TEXT," +
            EatContract.MenuItem.COLUMN_NAME_RESTAURANT_ID + " INTEGER," +
            EatContract.MenuItem.COLUMN_NAME_DATE + " INTEGER," +
            " FOREIGN KEY ("+EatContract.MenuItem.COLUMN_NAME_RESTAURANT_ID +") REFERENCES "+EatContract.Restaurant.TABLE_NAME+"("+EatContract.Restaurant._ID+")"
            + "ON DELETE CASCADE )";

    private static final String SQL_DELETE_MENU_ITEM =
            "DROP TABLE IF EXISTS " + EatContract.MenuItem.TABLE_NAME;

    private static final String SQL_DELETE_ALL_MENU_ITEMS =
            "DELETE FROM " + EatContract.MenuItem.TABLE_NAME;

    public EatHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_Restaurant);
        db.execSQL(SQL_CREATE_MENU_ITEM);

    }

    public void onOpen(SQLiteDatabase db){
        db.execSQL(SQL_ENABLE_FOREIGN_KEYS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE_RESTAURANT_TABLE);
        db.execSQL(SQL_DELETE_MENU_ITEM);
        onCreate(db);
        Log.i(TAG, "Restaurant DB upgraded.");
    }

    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
        Log.i(TAG, "Restaurant DB downgraded.");
    }

    public long insertRestaurant(String name, double longitude, double latitude,  String openingTime, String closingTime){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EatContract.Restaurant.COLUMN_NAME_NAME, name);
        values.put(EatContract.Restaurant.COLUMN_NAME_LONGITUDE, longitude);
        values.put(EatContract.Restaurant.COLUMN_NAME_LATITUDE, latitude);
        values.put(EatContract.Restaurant.COLUMN_NAME_OPENING_TIME, openingTime);
        values.put(EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME, closingTime);

        long newRowId = db.insert(EatContract.Restaurant.TABLE_NAME, null, values);
        Log.v(TAG, "Restaurant at " + name + " added to the DB at row " + newRowId
                + ", with open hours: " + openingTime + "-" + closingTime );
        return newRowId;
    }

    public long insertMenuItem(String name, String description, double price, String section, Date date, Long restaurantId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EatContract.MenuItem.COLUMN_NAME_NAME, name);
        values.put(EatContract.MenuItem.COLUMN_NAME_DESCRIPTION, description);
        values.put(EatContract.MenuItem.COLUMN_NAME_PRICE, price);
        values.put(EatContract.MenuItem.COLUMN_NAME_SECTION, section);
        values.put(EatContract.MenuItem.COLUMN_NAME_DATE, date.getTime());
        values.put(EatContract.MenuItem.COLUMN_NAME_RESTAURANT_ID, restaurantId);

        long newRowId = db.insert(EatContract.MenuItem.TABLE_NAME, null, values);
        Log.v(TAG, description + "added to " + section + ", available on " + date
                + " at a price of " + price);
        return newRowId;
    }

    // Update a Restaurant
    public void updateRestaurant(Long _id, String name, double longitude, double latitude, String openingTime, String closingTime){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EatContract.Restaurant.COLUMN_NAME_NAME, name);
        values.put(EatContract.Restaurant.COLUMN_NAME_LONGITUDE, longitude);
        values.put(EatContract.Restaurant.COLUMN_NAME_LATITUDE, latitude);
        values.put(EatContract.Restaurant.COLUMN_NAME_OPENING_TIME, openingTime);
        values.put(EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME, closingTime);

        String selection = EatContract.Restaurant._ID + " = ? ";
        String [] selectionArgs = {_id.toString()};

        int count = db.update(EatContract.Restaurant.TABLE_NAME,values, selection, selectionArgs);

        Log.v(TAG, count + " Restaurants changed.");
        Log.v(TAG, "Restaurant " + name + " updated, with opening times: " + openingTime + "-"
                + closingTime + ". Longitude: " + longitude + ", Latitude: " + latitude );

    }

    // Update a menu item
    public void updateMenuItem(Long _id, String name, String description, double price, String section, Date date){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EatContract.MenuItem.COLUMN_NAME_NAME, name);
        values.put(EatContract.MenuItem.COLUMN_NAME_DESCRIPTION, description);
        values.put(EatContract.MenuItem.COLUMN_NAME_PRICE, price);
        values.put(EatContract.MenuItem.COLUMN_NAME_SECTION, section);
        values.put(EatContract.MenuItem.COLUMN_NAME_DATE, date.getTime());

        String selection = EatContract.MenuItem._ID + " = ? ";
        String[] selectionArgs = {_id.toString()};

        int count = db.update(EatContract.MenuItem.TABLE_NAME,values, selection, selectionArgs);

        Log.v(TAG, count + " menu items changed.");
        Log.v(TAG, "Menu item " + name + " " + description + "added to " + section + ", available on " + date
                + " at a price of " + price );

    }

    public void deleteRestaurant(Long _id){
        SQLiteDatabase db = this.getWritableDatabase();

      /*  ContentValues values = new ContentValues();
        values.get(EatContract.MenuItem._ID);*/

        String selection = EatContract.Restaurant._ID + " = ?";
        String[] selectionArgs = {_id.toString()};

        int count = db.delete(EatContract.Restaurant.TABLE_NAME, selection, selectionArgs);

        Log.v(TAG, count + " Restaurants deleted.");
    }

    public void deleteMenuItem(Long _id){
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = EatContract.MenuItem._ID + " = ?";
        String[] selectionArgs = {_id.toString()};

        int count = db.delete(EatContract.MenuItem.TABLE_NAME, selection, selectionArgs);

        Log.v(TAG, count + " Menu items deleted.");
    }

    // Clear Database
    public void deleteDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_RESTAURANT_TABLE);
        db.execSQL(SQL_DELETE_MENU_ITEM);

        Log.v(TAG, "Database deleted");
    }

    // Clear Database Tables
    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ALL_RESTAURANTS);
        db.execSQL(SQL_DELETE_ALL_MENU_ITEMS);

        Log.v(TAG, "Database tables cleared.");

    }

    public Cursor getAllRestaurants(){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                EatContract.Restaurant._ID,
                EatContract.Restaurant.COLUMN_NAME_NAME,
                EatContract.Restaurant.COLUMN_NAME_LONGITUDE,
                EatContract.Restaurant.COLUMN_NAME_LATITUDE,
                EatContract.Restaurant.COLUMN_NAME_OPENING_TIME,
                EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME
        };

        String sortOrder = EatContract.Restaurant.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                EatContract.Restaurant.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        return cursor;
    }

    public Cursor getAllMenuItemsGivenRestaurantId(Long restaurantId){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                EatContract.MenuItem.COLUMN_NAME_NAME,
                EatContract.MenuItem.COLUMN_NAME_DESCRIPTION,
                EatContract.MenuItem.COLUMN_NAME_PRICE,
                EatContract.MenuItem.COLUMN_NAME_SECTION,
                EatContract.MenuItem.COLUMN_NAME_DATE
        };

        String selection = EatContract.MenuItem.COLUMN_NAME_RESTAURANT_ID + " = ?";
        String[] selectionArgs = {restaurantId.toString()};

        String sortOrder = EatContract.MenuItem.COLUMN_NAME_SECTION + " ASC";

        Cursor cursor = db.query(
                EatContract.MenuItem.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return cursor;
    }

    public Cursor getAllMenuItems(){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                EatContract.MenuItem._ID,
                EatContract.MenuItem.COLUMN_NAME_NAME,
                EatContract.MenuItem.COLUMN_NAME_DESCRIPTION,
                EatContract.MenuItem.COLUMN_NAME_PRICE,
                EatContract.MenuItem.COLUMN_NAME_SECTION,
                EatContract.MenuItem.COLUMN_NAME_DATE
        };

        String sortOrder = EatContract.MenuItem.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(
                EatContract.MenuItem.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        return cursor;
    }

    public Cursor getMenuItemGivenDateAndLocation(Long restaurantId, Date dateStart, Date dateEnd){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                EatContract.MenuItem.COLUMN_NAME_NAME,
                EatContract.MenuItem.COLUMN_NAME_DESCRIPTION,
                EatContract.MenuItem.COLUMN_NAME_PRICE,
                EatContract.MenuItem.COLUMN_NAME_SECTION,
                EatContract.MenuItem.COLUMN_NAME_RESTAURANT_ID
        };

        String selection = EatContract.Restaurant._ID + " = ?" + " AND " +
                EatContract.MenuItem.COLUMN_NAME_DATE + " >= ?" + " OR " +
                EatContract.MenuItem.COLUMN_NAME_DATE + " <= ?";
        String[] selectionArgs = {restaurantId.toString(), String.valueOf(dateStart.getTime()), String.valueOf(dateEnd.getTime())};

        String sortOrder = EatContract.MenuItem.COLUMN_NAME_SECTION + " ASC";

        Cursor cursor = db.query(
                EatContract.MenuItem.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        return cursor;

    }

    public Cursor getRestaurantOpeningClosingTimesGivenLocation(Long restaurantId ){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                EatContract.Restaurant.COLUMN_NAME_OPENING_TIME,
                EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME
        };

        String selection = EatContract.Restaurant._ID + " = ?";
        String[] selectionArgs = {restaurantId.toString()};

        Cursor cursor = db.query(
                EatContract.Restaurant.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }

    public Cursor getRestaurantCoordinatesGivenLocation(Long restaurantId){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                EatContract.Restaurant.COLUMN_NAME_LONGITUDE,
                EatContract.Restaurant.COLUMN_NAME_LATITUDE
        };

        String selection = EatContract.Restaurant._ID + " = ?";
        String[] selectionArgs = {restaurantId.toString()};

        Cursor cursor = db.query(
                EatContract.Restaurant.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        return cursor;
    }

    public Long getRestaurantIdGivenLocation(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                EatContract.Restaurant._ID,
                EatContract.Restaurant.COLUMN_NAME_NAME
        };

        String selection = EatContract.Restaurant.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {name};



        Cursor cursor = db.query(
                EatContract.Restaurant.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        Long restaurantId = cursor.getLong(cursor.getColumnIndex(EatContract.Restaurant._ID));
        return restaurantId;
    }

    public Long getMenuItemIdGivenDescription(String description){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                EatContract.MenuItem._ID
        };

        String selection = EatContract.MenuItem.COLUMN_NAME_DESCRIPTION + " = ?";
        String[] selectionArgs = {description};

        Cursor cursor = db.query(
                EatContract.MenuItem.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        Long menuItemId = cursor.getLong(cursor.getColumnIndex(EatContract.MenuItem._ID));
        return menuItemId;
    }


}
