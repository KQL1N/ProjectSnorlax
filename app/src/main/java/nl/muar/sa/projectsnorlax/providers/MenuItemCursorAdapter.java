package nl.muar.sa.projectsnorlax.providers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import nl.muar.sa.projectsnorlax.R;

public class MenuItemCursorAdapter extends CursorAdapter {
    public MenuItemCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.viewitem_menu, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        // Create text views for all items in the view
        TextView tvSection = (TextView) view.findViewById(R.id.section_text);
        TextView tvPrice = (TextView) view.findViewById(R.id.price_text);
        TextView tvOptionName = (TextView) view.findViewById(R.id.option_name_text);
        TextView tvOptionDesc = (TextView) view.findViewById(R.id.option_description_text);

        // Extract properties from cursor item
        String section = cursor.getString(cursor.getColumnIndexOrThrow("section"));
        String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
        String optionName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String optionDesc = cursor.getString(cursor.getColumnIndexOrThrow("description"));

        // Set text to values gained from cursor item
        tvSection.setText(section);
        tvPrice.setText(price);
        tvOptionName.setText(optionName);
        tvOptionDesc.setText(optionDesc);
    }
}
