package com.example.jacob.findmypharmacy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PharmListAdapter extends ArrayAdapter<Pharmacy> {
    ArrayList<Pharmacy> pharms;
    Context context;
    int resource;

    public PharmListAdapter(Context context, int resource, ArrayList<Pharmacy> pharms) {
        super(context, resource, pharms);
        this.context = context;
        this.resource = resource;
        this.pharms = pharms;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.pharm_row, null, true);
        }

        Pharmacy pharmacy = getItem(position);

        TextView pharmname = (TextView) convertView.findViewById(R.id.pharmname);
        pharmname.setText(pharmacy.getPhar_name());

        TextView pharmstreet = (TextView) convertView.findViewById(R.id.pharmstreet);
        pharmstreet.setText(pharmacy.getStreet());

        TextView pharmdistance = (TextView) convertView.findViewById(R.id.pharmdistance);

        pharmdistance.setText(pharmacy.getDistance()); // de calculat distanta pe viitor.

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Regular.ttf");
        pharmname.setTypeface(custom_font);
        //pharmstreet.setTypeface(custom_font);
        //pharmdistance.setTypeface(custom_font);

        return convertView;
    }



}
