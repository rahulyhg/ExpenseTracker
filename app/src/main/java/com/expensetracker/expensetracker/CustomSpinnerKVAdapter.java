package com.expensetracker.expensetracker;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Krush on 16-Nov-15.
 */
public class CustomSpinnerKVAdapter extends ArrayAdapter<HashMap<String,String>> {

    public Resources res;
    LayoutInflater inflater;
    private Context context1;
    private ArrayList<HashMap<String,String>> data;

    public CustomSpinnerKVAdapter(Context context, ArrayList<HashMap<String,String>> objects) {
        super(context, R.layout.spinner_row, objects);

        context1 = context;
        data = objects;

        inflater = (LayoutInflater) context1
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.spinner_hashmap_row, parent, false);

        TextView tvName = (TextView) row.findViewById(R.id.tvName);
        TextView tvId = (TextView) row.findViewById(R.id.tvId);

        tvName.setText(data.get(position).get("name").toString());
        tvId.setText(data.get(position).get("id").toString());

        return row;
    }
}