package com.example.pertti.vrjsonparser;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private Integer testi=0;

    private static String url = "http://rata.digitraffic.fi/api/v1/metadata/stations";

    ArrayList<HashMap<String, String>> asemaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        asemaList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        new GetContacts().execute();
    }


    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

// Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

// Getting JSON Array node
                    //JSONArray contacts = jsonObj.getJSONArray("contacts");
                    //JSONArray contacts = new JSONArray(jsonStr);

// looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        HashMap<String, String> contact = new HashMap<>();
                        JSONObject c = contacts.getJSONObject(i);

                        contact.put("stationShortCode", c.getString("stationShortCode"));
                        contact.put("stationName", c.getString("stationName"));
                        contact.put("stationUICCode", c.getString("stationUICCode"));

// adding contact to contact list
                        asemaList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
// Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
/**
 * Updating parsed JSON data into ListView
 * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, asemaList,
                    R.layout.list_item, new String[]{"stationName", "stationShortCode",
                    "stationUICCode"}, new int[]{R.id.stationName,
                    R.id.stationShortCode, R.id.stationUICCode});

            lv.setAdapter(adapter);
        }

    }
}
