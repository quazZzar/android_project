package com.example.jacob.findmypharmacy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

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

public class NetworksListView extends Activity {
    String JSON_STRING;
    ListView netListView;
    ArrayList<Network> arrayList;
    public static final String EXTRA_MESSAGE = "com.example.jacob.findmypharmacy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networks_list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        this.getActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(ContextCompat.getColor(NetworksListView.this, R.color.textColorPrimary));
        //this.getActionBar().setDisplayHomeAsUpEnabled(true);

        TextView the_activity_title = (TextView) findViewById(R.id.the_activity_title);
        the_activity_title.setText(this.getTitle());
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Bold.ttf");
        the_activity_title.setTypeface(custom_font);

        arrayList = new ArrayList<>();
        netListView = (ListView) findViewById(R.id.netListView);

        getJSON();
    }

    public void getJSON(){
        new BackgroundTask(" https://the-cinemax.com/pharmacies/getnetworks/").execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        private static final int STYLE_SPINNER = 0;
        String json_url;
        ProgressDialog progressdialog = new ProgressDialog(NetworksListView.this);

        public BackgroundTask(String json_url) {
            this.json_url = json_url;
        }

        @Override
        protected void onPreExecute() {
            progressdialog.setTitle(R.string.fetching_info);
            progressdialog.setMessage("Please Wait ...");
            progressdialog.setProgressStyle(STYLE_SPINNER);
            progressdialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDefaultUseCaches(false);
                httpURLConnection.setUseCaches(false);
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
                    arrayList.add(new Network(
                            obj.getInt("net_id"),
                            obj.getString("net_name"),
                            obj.getString("net_label"),
                            obj.getInt("net_pharms")
                    ));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            NetworkListAdapter networkListAdapter = new NetworkListAdapter(getApplicationContext(), R.layout.net_row, arrayList);
            netListView.setAdapter(networkListAdapter);
            progressdialog.dismiss();

            netListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Network clicked_network = (Network) adapterView.getItemAtPosition(position);
                    String slug = clicked_network.getNet_name();
                    //Toast.makeText(NetworksListView.this, slug, Toast.LENGTH_SHORT).show();
                    getTheRightPharmacies(slug);
                }
            });

        }
    }
    public void getTheRightPharmacies(String data) {
        Intent intent = new Intent(this, PharmacyListView.class);
        intent.putExtra(EXTRA_MESSAGE, data);
        startActivity(intent);
    }

}
