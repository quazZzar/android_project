package com.example.jacob.findmypharmacy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MainActivity extends Activity implements OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private ArrayList<Pharmacy> arrayListFromPhLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        TextView the_activity_title = (TextView) findViewById(R.id.the_activity_title);
        the_activity_title.setText(this.getTitle());
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Bold.ttf");
        the_activity_title.setTypeface(custom_font);

        MapFragment googleMap = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        googleMap.getMapAsync(this);

        Bundle arrayPharmLV = getIntent().getExtras();
        if(arrayPharmLV != null){
            arrayListFromPhLV = (ArrayList<Pharmacy>) arrayPharmLV.getSerializable(PharmacyListView.EXTRA_ARRAYLIST);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.maps_styles));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (arrayListFromPhLV != null) {
            for (int i = 0; i < arrayListFromPhLV.size(); i++) {
                Pharmacy selected = arrayListFromPhLV.get(i);
                LatLng clicked_pharmacy = new LatLng(selected.getLatitude(), selected.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .title(selected.getPhar_name())
                        .snippet(selected.getStreet())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                        .position(clicked_pharmacy));
            }
        }
    }

    // Bottom bar buttons
    public void getNetworksList(View view) {
        Intent intent = new Intent(MainActivity.this, NetworksListView.class);
        startActivity(intent);
        onPause();
    }

}
