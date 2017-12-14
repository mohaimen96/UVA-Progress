package com.example.uhunt.sample1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AboutProblem extends AppCompatActivity {

    String userid,prob_id,name,dacu;
    String ui;
    ArrayList<HashMap<String, String>> contactList;
    private String url = "https://uhunt.onlinejudge.org/api/p/ranklist/"+prob_id+"/"+userid+"/0/0";
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_problem);

        contactList = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();

        String use1 = bundle.getString("problem");
        prob_id = use1;
        String use = bundle.getString("id");
        userid = use;
        String use2 = bundle.getString("name");
        name = use2;
        String use3 = bundle.getString("dacu");
        dacu = use3;
        url = "https://uhunt.onlinejudge.org/api/p/ranklist/"+ prob_id +"/"+userid+"/0/0";
        new GetContacts().execute();

        Button link = (Button)findViewById(R.id.button);
        link.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String url = "https://uva.onlinejudge.org/index.php?option=com_onlinejudge&Itemid=8&page=show_problem&problem=" + prob_id;

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AboutProblem.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            com.example.uhunt.sample1.HttpHandler sh = new com.example.uhunt.sample1.HttpHandler();

            // Making a request to url and getting response
            Log.e(TAG, "u "+url);
            String sonStr = sh.makeServiceCall(url);
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray contacts = new JSONArray(jsonStr);

                    JSONObject c = contacts.getJSONObject(0);

                    String sub_id = c.getString("sid");
                    Log.e(TAG, "Response from url: " + sub_id);
                    String User_id = c.getString("uid");
                    Log.e(TAG, "Response from url: " + User_id);
                    String p_id = c.getString("pid");
                    Log.e(TAG, "Response from url: " + p_id);
                    String verdict = c.getString("ver");
                    String lang = c.getString("lan");
                    Log.e(TAG, "Response from url: " + lang);
                    String runtime = c.getString("run");
                    String memory = c.getString("mem");
                    String rank = c.getString("rank");
                    Log.e(TAG, "Response from url: " + rank);

                    HashMap<String, String> contact = new HashMap<>();
                    // adding each child node to HashMap key => value
                    contact.put("uid", User_id);
                    contact.put("sid", sub_id);
                    contact.put("ran", rank);
                    contact.put("uid", User_id);
                    contact.put("pid", p_id);
                    contact.put("ver", verdict);
                    contact.put("lan", lang);
                    contact.put("run", runtime);
                    contact.put("mem", memory);

                    contactList.add(contact);
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
            HashMap<String,String> contact=contactList.get(0);
            ui = contact.get("uid");
            String verdict = "Not Yet Solved";
            String lang = null;
            String run = null;
            String mem = null;
            String rank = "0";
            int a=ui.length();
            int b=userid.length();
            Log.e(TAG, "Response " + ui+ " "+userid);
            if(ui.equals(userid))
            {
                Log.e(TAG, "Opened if ");
                verdict = "Solved";
                lang = contact.get("lan");
                run = contact.get("run");
                mem = contact.get("mem");
                if(lang=="1")
                    lang="ANSI C";
                else if(lang=="2")
                    lang="JAVA";
                else if(lang=="3")
                    lang="C++";
                else if(lang=="4")
                    lang="PASCAL";
                else if(lang=="5")
                    lang="C++11";
                rank = contact.get("ran");
            }

            TextView tv = (TextView)findViewById(R.id.pid);
            tv.setText("Problem ID: "+prob_id);
            tv = (TextView)findViewById(R.id.verdict);
            tv.setText("Verdict: "+verdict);
            tv = (TextView)findViewById(R.id.rank);
            tv.setText("Rank: "+rank);
            tv = (TextView)findViewById(R.id.lang);
            tv.setText("Language: "+lang);
            tv = (TextView)findViewById(R.id.run);
            tv.setText("Runtime: "+ run +" ms");
            tv = (TextView)findViewById(R.id.mem);
            tv.setText("Memory: " + mem);
            tv = (TextView)findViewById(R.id.name);
            tv.setText(name);
            tv = (TextView)findViewById(R.id.dacu);
            tv.setText("Solved User: " +dacu);
        }

    }
}
