package com.example.jacob.findmypharmacy;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private ArrayList<Pharmacy> arrayListFromPhLV;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng currentLocation;
    private ImageButton getPosBtn;

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
        if (arrayPharmLV != null) {
            arrayListFromPhLV = (ArrayList<Pharmacy>) arrayPharmLV.getSerializable(PharmacyListView.EXTRA_ARRAYLIST);
        }
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View info_window = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                    JSONObject mainObject = null;
                    try {
                        mainObject = new JSONObject(marker.getSnippet());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    TextView pin_title = (TextView) info_window.findViewById(R.id.pin_title);
                    TextView pin_open = (TextView) info_window.findViewById(R.id.pin_opening);
                    TextView pin_close = (TextView) info_window.findViewById(R.id.pin_closing);
                    TextView pin_street = (TextView) info_window.findViewById(R.id.pin_street);
                    TextView pin_phone = (TextView) info_window.findViewById(R.id.pin_phone);
                    TextView pin_email = (TextView) info_window.findViewById(R.id.pin_email);
                    TextView pin_website = (TextView) info_window.findViewById(R.id.pin_website);

                    pin_title.setText(marker.getTitle());
                    try {
                        pin_open.setText(mainObject.getString("pin_open"));
                        pin_close.setText(mainObject.getString("pin_close"));
                        pin_street.setText(mainObject.getString("pin_street"));
                        pin_phone.setText(mainObject.getString("pin_phone"));
                        pin_email.setText(mainObject.getString("pin_email"));
                        pin_website.setText(mainObject.getString("pin_website"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return info_window;
                }
            });
        }


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
        getPosBtn = (ImageButton) findViewById(R.id.getposition);
        getPosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        });

        if (arrayListFromPhLV != null) {
            for (int i = 0; i < arrayListFromPhLV.size(); i++) {
                Pharmacy selected = arrayListFromPhLV.get(i);
                JSONObject jsonPinSnippet = new JSONObject();

                try {
                    jsonPinSnippet.put("pin_street", selected.getStreet());
                    jsonPinSnippet.put("pin_phone", selected.getPhone());
                    jsonPinSnippet.put("pin_email", selected.getEmail());
                    jsonPinSnippet.put("pin_website", selected.getWebsite());
                    jsonPinSnippet.put("pin_open", selected.getOpening_at());
                    jsonPinSnippet.put("pin_close", selected.getClosing_at());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LatLng clicked_pharmacy = new LatLng(selected.getLatitude(), selected.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .title(selected.getPhar_name())
                        .snippet(jsonPinSnippet.toString())
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
