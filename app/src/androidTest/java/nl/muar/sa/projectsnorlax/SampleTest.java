package nl.muar.sa.projectsnorlax;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import nl.muar.sa.projectsnorlax.db.EatContract;
import nl.muar.sa.projectsnorlax.db.EatHelper;

import static org.junit.Assert.assertEquals;


/*Created by HCEvans on 15/06/17.*/
@RunWith(AndroidJUnit4.class)
public class SampleTest {

    private Context context;
    private EatHelper eatHelper;

    private static final String TAG = "Sample Test";

    private String restaurantName = "Guildford";
    private double longitude = 51.242774;
    private double latitude = -0.615271;
    private String openingTime = "1200";
    private String closingTime = "1430";
    private String menuItemName = "Cream of Tomato";
    private String description = "Like your average tomato soup, but creamier";
    private double price = 3.50;
    private String section = "Soup";
    private Date date = new Date();



    @Before
    public void beforeTest(){
        context = InstrumentationRegistry.getTargetContext();
        eatHelper = new EatHelper(context);
    }


    @Test
    public void addNewRestaurant(){
        eatHelper.clearDB();
        long newRestaurant = eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        // Check that only one restaurant has been added
        assertEquals(1, newRestaurant);
        // Return all restaurants in the database
        Cursor cursor = eatHelper.getAllRestaurants();
        // Check there is only one restaurant in the table
        assertEquals(1, cursor.getCount());
        // Check each value is correct for the given restaurant
        cursor.moveToFirst();
        assertEquals(cursor.getInt(cursor.getColumnIndex(EatContract.Restaurant._ID)),1);
        assertEquals(restaurantName, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_NAME)));
        assertEquals(longitude, cursor.getDouble(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LONGITUDE)),0);
        assertEquals(latitude, cursor.getDouble(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LATITUDE)),0);
        assertEquals(openingTime, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_OPENING_TIME)));
        assertEquals(closingTime, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME)));
    }

    @Test
    public void addNewMenuItemAtSpecificRestaurant(){
        eatHelper.clearDB();
        // Create a restaurant
        Long guildfordRestaurantId = eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        assertEquals(1, guildfordRestaurantId.intValue());
        // Check restaurants added
        Cursor cursor = eatHelper.getAllRestaurants();
        assertEquals(1, cursor.getCount());
        cursor.close();
        // Create a new menu item for given restaurant
        date.setTime(1497607975);
        long newMenuItem = eatHelper.insertMenuItem(menuItemName, description, price, section, date, guildfordRestaurantId);
        assertEquals(1, newMenuItem);
        // Return all menu items at restaurant
        Cursor cursor2 = eatHelper.getAllMenuItemsGivenRestaurantId(guildfordRestaurantId);
        cursor2.moveToFirst();
        // Check there is only one menu item at given restaurant
        assertEquals(1, cursor2.getCount());
        // Check each value is correct for given menu item at given restaurant
        assertEquals(menuItemName, cursor2.getString(cursor2.getColumnIndex(EatContract.MenuItem.COLUMN_NAME_NAME)));
        assertEquals(description, cursor2.getString(cursor2.getColumnIndex(EatContract.MenuItem.COLUMN_NAME_DESCRIPTION)));
        assertEquals(price, cursor2.getDouble(cursor2.getColumnIndex(EatContract.MenuItem.COLUMN_NAME_PRICE)),0);
        assertEquals(section, cursor2.getString(cursor2.getColumnIndex(EatContract.MenuItem.COLUMN_NAME_SECTION)));
        assertEquals(date.getTime(), cursor2.getInt(cursor2.getColumnIndex(EatContract.MenuItem.COLUMN_NAME_DATE)));

    }

    @Test
    public void updateRestaurantNameAndClosingTime(){
        eatHelper.clearDB();
        // Create some restaurants
        Long guildfordRestaurantId = eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        assertEquals(1, guildfordRestaurantId.intValue());

        // New values
        String newRestaurantName = "GFord";
        String newClosingTime = "1400";
        eatHelper.updateRestaurant(guildfordRestaurantId, newRestaurantName, longitude, latitude, openingTime, newClosingTime);

        Cursor cursor = eatHelper.getAllRestaurants();
        assertEquals(1,cursor.getCount());
        cursor.moveToFirst();
        assertEquals(newRestaurantName, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_NAME)));
        assertEquals(newClosingTime, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME)));

    }

    @Test
    public void updateMenuItemName(){
        eatHelper.clearDB();
        // Create some restaurants
        Long guildfordRestaurantId = eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        assertEquals(1, guildfordRestaurantId.intValue());
        // Create a menu item for given restaurant
        Long menuItemId = eatHelper.insertMenuItem(menuItemName, description, price, section, date, guildfordRestaurantId);

        // New values
        String newMenuItemName = "Cream of Mushroom";
        eatHelper.updateMenuItem(menuItemId, newMenuItemName, description, price, section, date);

        Cursor cursor = eatHelper.getAllMenuItemsGivenRestaurantId(guildfordRestaurantId);
        assertEquals(1,cursor.getCount());
        cursor.moveToFirst();
        assertEquals(newMenuItemName, cursor.getString(cursor.getColumnIndex(EatContract.MenuItem.COLUMN_NAME_NAME)));


    }

    @Test
    public void deleteRestaurantGivenId(){
        eatHelper.clearDB();
        // Create two restaurants
        Long guildfordRestaurantId = eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        Long londonRestaurantId = eatHelper.insertRestaurant("BlueFin",-0.100193, 51.506279, openingTime, closingTime);
        eatHelper.insertMenuItem(menuItemName, description, price, section, date, guildfordRestaurantId);
        eatHelper.insertMenuItem("Baked Potato", "Add toppings", price, section, date, londonRestaurantId);

        // Delete given restaurant
        eatHelper.deleteRestaurant(londonRestaurantId);
        // Check restaurant deleted
        Cursor cursor = eatHelper.getAllRestaurants();
        cursor.moveToFirst();
        assertEquals(1, cursor.getCount());
        assertEquals(restaurantName, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_NAME)));

        // Check that menu items are deleted when restaurant is deleted
        Cursor cursor2 = eatHelper.getAllMenuItems();
        cursor2.moveToFirst();/*
        assertEquals(1, cursor2.getCount());*/
        assertEquals(menuItemName, cursor2.getString(cursor2.getColumnIndex(EatContract.MenuItem.COLUMN_NAME_NAME)));
    }

    @Test
    public void deleteMenuItemGivenId(){
        eatHelper.clearDB();
        // Create a restaurant
        Long guildfordRestaurantId = eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        // Add a menu item
        Long newMenuItemId = eatHelper.insertMenuItem(menuItemName, description, price, section, date, guildfordRestaurantId);

        // Delete menu item at given restaurant
        eatHelper.deleteMenuItem(newMenuItemId);
        // Check restaurant deleted
        Cursor cursor = eatHelper.getAllMenuItemsGivenRestaurantId(guildfordRestaurantId);
        cursor.moveToFirst();
        assertEquals(0, cursor.getCount());

        Cursor cursor2 = eatHelper.getAllMenuItems();
        assertEquals(0, cursor2.getCount() );
    }


    @Test
    public void returnAllRestaurants(){
        eatHelper.clearDB();
        eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        Cursor cursor = eatHelper.getAllRestaurants();
        cursor.moveToFirst();
        assertEquals(cursor.getInt(cursor.getColumnIndex(EatContract.Restaurant._ID)),1);
        assertEquals(restaurantName, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_NAME)));
        assertEquals(longitude, cursor.getDouble(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LONGITUDE)),0);
        assertEquals(latitude, cursor.getDouble(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LATITUDE)),0);
        assertEquals(openingTime, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_OPENING_TIME)));
        assertEquals(closingTime, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME)));

    }

    @Test
    public void getSingleMenuItemByDateAndLocation(){
        eatHelper.clearDB();
        // Create a restaurant
        Long guildfordRestaurantId =  eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        // Create a new menu item for given restaurant
        date.setTime(1497607975);
        long newMenuItem = eatHelper.insertMenuItem(menuItemName, description, price, section, date, guildfordRestaurantId);
        assertEquals(1, newMenuItem);
        //
        Date dateStart = new Date();
        dateStart.setTime(123456);
        Date dateEnd = new Date();
        dateEnd.setTime(1234567);
        Cursor cursor = eatHelper.getMenuItemGivenDateAndLocation(guildfordRestaurantId, dateStart, dateEnd);
        cursor.moveToFirst();
        assertEquals(menuItemName, cursor.getString(cursor.getColumnIndex(EatContract.MenuItem.COLUMN_NAME_NAME)));

    }

    @Test
    public void getRestaurantTimes(){
        eatHelper.clearDB();
        Long guildfordRestaurantId =  eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        Cursor cursor = eatHelper.getRestaurantOpeningClosingTimesGivenLocation(guildfordRestaurantId);
        cursor.moveToFirst();
        assertEquals(openingTime, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_OPENING_TIME)));
        assertEquals(closingTime, cursor.getString(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_CLOSING_TIME)));

    }

    @Test
    public void getRestaurantCoordinates(){
        eatHelper.clearDB();
        Long guildfordRestaurantId =  eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        Cursor cursor = eatHelper.getRestaurantCoordinatesGivenLocation(guildfordRestaurantId);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(longitude, cursor.getDouble(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LONGITUDE)),0);
        assertEquals(latitude, cursor.getDouble(cursor.getColumnIndex(EatContract.Restaurant.COLUMN_NAME_LATITUDE)),0);

    }

    @Test
    public void getRestaurantIdGivenName(){
        eatHelper.clearDB();
        long guildfordRestaurantId =  eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);

        Cursor cursor = eatHelper.getRestaurantIdGivenLocation(restaurantName);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(guildfordRestaurantId, cursor.getLong(cursor.getColumnIndex(EatContract.Restaurant._ID)));
    }

    @Test
    public void getMenuItemIdGivenDescription(){
        eatHelper.clearDB();
        // Create a restaurant
        Long guildfordRestaurantId =  eatHelper.insertRestaurant(restaurantName, longitude, latitude, openingTime, closingTime);
        // Create a new menu item for given restaurant
        date.setTime(1497607975);
        long newMenuItem = eatHelper.insertMenuItem(menuItemName, description, price, section, date, guildfordRestaurantId);
        assertEquals(1, newMenuItem);

        Cursor cursor = eatHelper.getMenuItemIdGivenDescription(description);
        cursor.moveToFirst();

        assertEquals(newMenuItem, cursor.getLong(cursor.getColumnIndex(EatContract.MenuItem._ID)));

    }
}
