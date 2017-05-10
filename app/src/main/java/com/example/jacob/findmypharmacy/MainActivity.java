package com.example.jacob.findmypharmacy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Locale;


public class MainActivity extends Activity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private ArrayList<Pharmacy> arrayListFromPhLV;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng currentLocation;
    private ImageButton getPosBtn;
    private ImageButton settingsBtn;
    private View dialogView;
    private RadioButton map_rb;
    private RadioGroup map_rg;
    private RadioGroup lang_rg;
    private RadioButton lang_rb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        TextView the_activity_title = (TextView) findViewById(R.id.the_activity_title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Bold.ttf");
        the_activity_title.setTypeface(custom_font);
        the_activity_title.setText(this.getTitle());
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
        settingsBtn = (ImageButton) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder settingsDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogView = getLayoutInflater().inflate(R.layout.settings_dialog, null);
                map_rg = (RadioGroup) dialogView.findViewById(R.id.map_type_rg);
                lang_rg = (RadioGroup) dialogView.findViewById(R.id.lang_rg);

                //getting the user's saved choice for the map settings
                SharedPreferences saved_sett = getSharedPreferences("UserSettings", 0);
                int checked_map_option = saved_sett.getInt("MapType", 0);
                //checking for null settings
                if(checked_map_option != 0){
                    RadioButton the_checked_map = (RadioButton) dialogView.findViewById(checked_map_option);
                    the_checked_map.setChecked(true);
                }

                int checked_lang_option = saved_sett.getInt("Language", 0);
                if(checked_lang_option != 0){
                    RadioButton the_checked_lang = (RadioButton) dialogView.findViewById(checked_lang_option);
                    the_checked_lang.setChecked(true);
                }
                settingsDialogBuilder.setView(dialogView);
                settingsDialogBuilder.setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getBaseContext(), R.string.nothing_changed, Toast.LENGTH_SHORT).show();
                    }
                });
                settingsDialogBuilder.setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        map_rb = (RadioButton) dialogView.findViewById(map_rg.getCheckedRadioButtonId());
                        lang_rb = (RadioButton) dialogView.findViewById(lang_rg.getCheckedRadioButtonId());

                        SharedPreferences settings = getSharedPreferences("UserSettings", 0);
                        SharedPreferences.Editor editor = settings.edit();

                        // if the current changes are equal to previous display a propper message
                        if(settings.getInt("MapType", 0) == map_rb.getId() && settings.getInt("Language", 0) == lang_rb.getId()) {
                            Toast.makeText(getBaseContext(), R.string.nothing_changed, Toast.LENGTH_SHORT).show();
                        }

                        // if current changes aren't equal to previous then save them
                        if(settings.getInt("MapType", 0) != map_rb.getId()){
                            editor.putInt("MapType", map_rb.getId());
                            editor.putString("MapTypeString", map_rb.getText().toString());
                            editor.apply();
                            //setting the map type right now.
                            if(settings.getString("MapTypeString", "").equals("Terrain") || settings.getString("MapTypeString", "").equals("Teren")|| settings.getString("MapTypeString", "").equals("Земеля")){
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            }
                            else if(settings.getString("MapTypeString", "").equals("Normal") || settings.getString("MapTypeString", "").equals("Обычьная")){
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            }
                            else if(settings.getString("MapTypeString", "").equals("Hybrid") || settings.getString("MapTypeString", "").equals("Hibrid") || settings.getString("MapTypeString", "").equals("Гибрид")){
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            }
                            else{
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            }
                        }

                        // if current changes aren't equal to previous then save them
                        if(settings.getInt("Language", 0) != lang_rb.getId()) {
                            editor.putInt("Language", lang_rb.getId());
                            editor.putString("LangString", lang_rb.getText().toString());
                            editor.apply();
                            if(settings.getString("LangString", "").equals("Romanian")){
                                String languageToLoad = "ro"; // your language
                                Locale locale = new Locale(languageToLoad);
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                                //refresh to make the changes
                                Intent refresh = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(refresh);
                                finish();
                            }
                            if(settings.getString("LangString", "").equals("English")){
                                String languageToLoad = "en"; // your language
                                Locale locale = new Locale(languageToLoad);
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                                //refresh to make the changes
                                Intent refresh = new Intent(MainActivity.this, ScreenSplash.class);
                                startActivity(refresh);
                                finish();
                            }
                            if(settings.getString("LangString", "").equals("Russian")){
                                String languageToLoad = "ru"; // your language
                                Locale locale = new Locale(languageToLoad);
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                                //refresh to make the changes
                                Intent refresh = new Intent(MainActivity.this, ScreenSplash.class);
                                startActivity(refresh);
                                finish();
                            }
                            if(settings.getString("LangString", "").equals("Francais")){
                                String languageToLoad = "fr"; // your language
                                Locale locale = new Locale(languageToLoad);
                                Locale.setDefault(locale);
                                Configuration config = new Configuration();
                                config.locale = locale;
                                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                                //refresh to make the changes
                                Intent refresh = new Intent(MainActivity.this, ScreenSplash.class);
                                startActivity(refresh);
                                finish();
                            }
                        }
                        Toast.makeText(getBaseContext(), R.string.dialog_saved, Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = settingsDialogBuilder.create();
                dialog.show();
            }
        });

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
                    TextView pin_distance = (TextView) info_window.findViewById(R.id.pin_distance);

                    pin_title.setText(marker.getTitle());
                    try {
                        pin_open.setText(mainObject.getString("pin_open"));
                        pin_close.setText(mainObject.getString("pin_close"));
                        pin_street.setText(mainObject.getString("pin_street"));
                        pin_phone.setText(mainObject.getString("pin_phone"));
                        pin_email.setText(mainObject.getString("pin_email"));
                        pin_website.setText(mainObject.getString("pin_website"));
                        pin_distance.setText(mainObject.getString("pin_distance"));
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
                    jsonPinSnippet.put("pin_distance", selected.getDistance());
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
        //getting the user's saved choice for the map settings at the app start
        SharedPreferences saved_sett = getSharedPreferences("UserSettings", 0);
        if(saved_sett.getString("MapTypeString", "").equals("Terrain") || saved_sett.getString("MapTypeString", "").equals("Teren") || saved_sett.getString("MapTypeString", "").equals("Земеля")){
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        else if(saved_sett.getString("MapTypeString", "").equals("Normal") || saved_sett.getString("MapTypeString", "").equals("Обычьная")){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(saved_sett.getString("MapTypeString", "").equals("Hybrid") || saved_sett.getString("MapTypeString", "").equals("Hibrid") || saved_sett.getString("MapTypeString", "").equals("Гибрид")){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    // Bottom bar buttons
    public void getNetworksList(View view) {
        Intent intent = new Intent(MainActivity.this, NetworksListView.class);
        startActivity(intent);
        onPause();
    }
}
