package com.example.uhunt.sample1;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class Ranklist extends AppCompatActivity {

    String userid;
    String name;
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private String url2 = "https://uhunt.onlinejudge.org/api/ranklist/"+userid+"/10/10";

    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranklist);

        Bundle bundle = getIntent().getExtras();
        String use = bundle.getString("id");
        //use = use.substring(0, use.length() - 1);
        url2 = "https://uhunt.onlinejudge.org/api/ranklist/"+use+"/10/10";
        Log.e(TAG, "Response from url: " + url2);
        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Ranklist.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            com.example.uhunt.sample1.HttpHandler sh = new com.example.uhunt.sample1.HttpHandler();

            // Making a request to url and getting response
            Log.e(TAG, "u "+url2);
            String sonStr = sh.makeServiceCall(url2);
            String jsonStr = sh.makeServiceCall(url2);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String rank = c.getString("rank");
                        int old = c.getInt("old");
                        String id = c.getString("userid");
                        String name = c.getString("name");
                        String username = c.getString("username");
                        String ac = c.getString("ac");
                        String nos = c.getString("nos");

                        // Activity node is JSON Array
                        JSONArray activity = c.getJSONArray("activity");
                        int at = activity.getInt(0);
                        at = activity.getInt(1);
                        at = activity.getInt(2);
                        at = activity.getInt(3);
                        at = activity.getInt(4);

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("rank", rank);
                        contact.put("name", name);
                        contact.put("ac", ac);
                        contact.put("nos", nos);

                        // adding contact to contact list
                        contactList.add(contact);
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
                    Ranklist.this, contactList,
                    R.layout.list_item, new String[]{ "rank",
                    "name", "ac", "nos"}, new int[]{ R.id.rank, R.id.name, R.id.ac, R.id.nos});

            lv.setAdapter(adapter);
        }

    }

}
