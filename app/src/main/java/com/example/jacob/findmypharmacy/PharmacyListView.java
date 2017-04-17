package com.example.jacob.findmypharmacy;

import android.app.Activity;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PharmacyListView extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    String JSON_STRING;
    ListView pharmListView;
    ArrayList<Pharmacy> arrayList;
    public static final String EXTRA_ARRAYLIST = "PharmsArrayList";
    String slug;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.pharmacy_lv_toolbar);
        setActionBar(toolbar);
        this.getActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(ContextCompat.getColor(PharmacyListView.this, R.color.textColorPrimary));
        this.getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView the_activity_title = (TextView) findViewById(R.id.the_activity_title);
        the_activity_title.setText(this.getTitle());
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Bold.ttf");
        the_activity_title.setTypeface(custom_font);

        //arrayList = new ArrayList<>();
        pharmListView = (ListView) findViewById(R.id.pharmListView);

        Intent intent_from_networks_activity = getIntent();
        slug = intent_from_networks_activity.getStringExtra(NetworksListView.EXTRA_MESSAGE);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    public void getJSON(String link) {
        new BackgroundTask(link).execute();
    }


    //////////// Getting the current location
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
            // TODO: Consider calling.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        arrayList = new ArrayList<>();
        getJSON("https://findmeapharmacy.000webhostapp.com/getpharmacies?network=" + slug + "&latitude=" + mLastLocation.getLatitude() + "&longitude=" + mLastLocation.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    /////////////////////////////////////////////////



    class BackgroundTask extends AsyncTask<Void, Void, String> {
        private static final int STYLE_SPINNER = 0;
        String json_url;
        ProgressDialog progressdialog = new ProgressDialog(PharmacyListView.this);

        public BackgroundTask(String json_url) {
            this.json_url = json_url;
        }

        @Override
        protected void onPreExecute() {
            progressdialog.setTitle("Fetching info");
            progressdialog.setMessage("Please Wait ...");
            progressdialog.setProgressStyle(STYLE_SPINNER);
            progressdialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_STRING = bufferedReader.readLine()) != null){
                    stringBuilder.append(JSON_STRING+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray the_json_from_net = new JSONArray(result);

                for(int i = 0; i < the_json_from_net.length(); i++)
                {
                    JSONObject obj = the_json_from_net.getJSONObject(i);
                    arrayList.add(new Pharmacy(
                            obj.getInt("phar_id"),
                            obj.getString("phar_name"),
                            obj.getString("street"),
                            obj.getDouble("latitude"),
                            obj.getDouble("longitude"),
                            obj.getString("phone"),
                            obj.getString("email"),
                            obj.getString("website"),
                            obj.getString("opening_at"),
                            obj.getString("closing_at"),
                            obj.getString("distance")
                    ));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            PharmListAdapter pharmListAdapter = new PharmListAdapter(getApplicationContext(), R.layout.pharm_row, arrayList);
            pharmListView.setAdapter(pharmListAdapter);
            progressdialog.dismiss();
            pharmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                    Pharmacy clicked_pharmacy = (Pharmacy) parent.getItemAtPosition(position);
                    ArrayList<Pharmacy> singlePin = new ArrayList<>();
                    singlePin.add(clicked_pharmacy);
                    sendAPharmacyToMap(singlePin);
                }
            });
        }
    }
    public void sendAPharmacyToMap(ArrayList<Pharmacy> pharmArrayList) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ARRAYLIST, pharmArrayList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void sendPharmaciesToMap(ArrayList<Pharmacy> pharmArrayList) {
        Intent intent1 = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ARRAYLIST, pharmArrayList);
        intent1.putExtras(bundle);
        startActivity(intent1);
    }

    public void sendBunledArList(View view) {
        sendPharmaciesToMap(arrayList);
    }




}
