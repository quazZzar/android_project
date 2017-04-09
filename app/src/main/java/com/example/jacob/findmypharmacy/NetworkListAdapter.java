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

/**
 * Created by Jacob on 26.03.2017.
 */

public class NetworkListAdapter extends ArrayAdapter<Network>{
    ArrayList<Network> networks;
    Context context;
    int resource;

    public NetworkListAdapter(Context context, int resource, ArrayList<Network> networks) {
        super(context, resource, networks);
        this.context = context;
        this.resource = resource;
        this.networks = networks;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.net_row, null, true);
        }

        Network network = getItem(position);
        TextView net_name = (TextView) convertView.findViewById(R.id.net_name_tv);
        net_name.setText(network.getNet_label());

        TextView net_pharms = (TextView) convertView.findViewById(R.id.pharms_quantity);
        net_pharms.setText(String.valueOf(network.getNet_pharms()));

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Regular.ttf");
        net_name.setTypeface(custom_font);
        net_pharms.setTypeface(custom_font);

        return convertView;
    }
}
