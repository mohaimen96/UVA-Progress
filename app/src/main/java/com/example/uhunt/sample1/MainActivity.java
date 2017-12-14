package com.example.uhunt.sample1;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String user=null,tempuser;
    String userid=null;

    private ProgressDialog pDialog;
    private GoogleMap gMap;
    private String TAG = MainActivity.class.getSimpleName();

    // URL to get contacts JSON
    private String url = "https://uhunt.onlinejudge.org/api/uname2uid/" + user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv = (TextView)findViewById(R.id.username);
        final DatabaseHandler db = new DatabaseHandler(this);

        int cc = db.empty();
        Log.e(TAG, "empty " + cc);
        if(cc==1)
        {
            UserDetails userDetails = db.getContact(1);
            user = userDetails._name;
            userid = userDetails._number;
        }

        tv.setText("Username: " + user);

        final Button clickButton = (Button) findViewById(R.id.button2);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText et = (EditText)findViewById(R.id.editText1);
                tempuser = et.getText().toString();
                url = "https://uhunt.onlinejudge.org/api/uname2uid/"+ tempuser;
                try {
                    String tempuserid = new GetContacts().execute().get();

                    tempuserid= tempuserid.substring(0, tempuserid.length() - 1);
                    Log.e(TAG, "Response" + tempuserid);

                    if(tempuserid == null || tempuserid.equals("0"))
                    {
                        Log.e(TAG, "Entered if");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Invalid Username",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                    else
                    {
                        user = tempuser;
                        userid = tempuserid;
                        int aa = db.empty();
                        if(aa == 1)
                        {
                            db.deleteContact();
                        }
                        db.addContact(new UserDetails(user, userid));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                tv.setText("Username: " + user);


            }
        });

        final Button problem = (Button) findViewById(R.id.problem);
        problem.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CheckProblem.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", userid);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        final Button rank = (Button)findViewById(R.id.Ranklist);
        rank.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(MainActivity.this, Ranklist.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", userid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private class GetContacts extends AsyncTask<Void, Void, String> {

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
        protected String doInBackground(Void... arg0) {
            com.example.uhunt.sample1.HttpHandler sh = new com.example.uhunt.sample1.HttpHandler();

            // Making a request to url and getting response
            //String sonStr = sh.makeServiceCall(url);
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "url: " + url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                String temp= jsonStr;

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Invalid Username",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

        }

    }

}
