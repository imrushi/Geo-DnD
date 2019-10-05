package com.example.geodnd;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomAdapter extends ArrayAdapter<String> {

    Context context;
    String[] states;
    int[] icons;
    SharedPref sharedpref;

    public CustomAdapter(@NonNull Context context, String[] states, int[] icons) {
        super(context, R.layout.spinner_layout, states);
        this.context = context;
        this.states = states;
        this.icons = icons;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_layout,null);
        TextView t1 = (TextView)row.findViewById(R.id.textView);
        ImageView i1 = (ImageView)row.findViewById(R.id.icon);

        t1.setText(states[position]);
        i1.setImageResource(icons[position]);

        return row;

    }

    public View getView(int position, View convertView, ViewGroup parent){
        sharedpref = new SharedPref(getContext());

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_layout,null);
        TextView t1 = (TextView)row.findViewById(R.id.textView);
        ImageView i1 = (ImageView)row.findViewById(R.id.icon);

        t1.setText(states[position]);
        i1.setImageResource(icons[position]);

        if(sharedpref.loadNightModeState()==true) {
            t1.setTextColor(Color.WHITE);
            i1.setColorFilter(Color.WHITE);
        }
        else {
            t1.setTextColor(Color.BLACK);
            i1.setColorFilter(Color.BLACK);
        }

            return row;
    }
}

