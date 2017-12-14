package com.example.uhunt.sample1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class CheckProblem extends AppCompatActivity {

    String userid,prob_num,prob_id,prob_name,dacu;
    private String TAG = MainActivity.class.getSimpleName();
    private String url2 = "https://uhunt.onlinejudge.org/api/p/num/"+prob_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_problem);

        Bundle bundle = getIntent().getExtras();
        String use = bundle.getString("id");
        userid = use;
        final Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText et = (EditText)findViewById(R.id.editText);
                prob_num = et.getText().toString();
                url2 = "https://uhunt.onlinejudge.org/api/p/num/"+prob_num;
                Log.e(TAG, "Response from url checkproblem: " + url2);
                try {
                    String[] ss = new GetContacts().execute().get();
                    prob_id = ss[0];
                    prob_name = ss[1];
                    dacu = ss[2];
                    Log.e(TAG, "id " + prob_id+prob_name);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(CheckProblem.this, AboutProblem.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", userid);
                bundle.putString("problem", prob_id);
                bundle.putString("name", prob_name);
                bundle.putString("dacu", dacu);
                intent.putExtras(bundle);
                startActivity(intent);


            }
        });

    }

    private class GetContacts extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... arg0) {
            com.example.uhunt.sample1.HttpHandler sh = new com.example.uhunt.sample1.HttpHandler();

            // Making a request to url and getting response
            String sonStr = sh.makeServiceCall(url2);
            String jsonStr = sh.makeServiceCall(url2);
            String id = null,name;
            String[] s1 = new String[5];
            Log.e(TAG, "Response from url checkp: " + url2);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    id = jsonObj.getString("pid");
                    name = jsonObj.getString("title");
                    s1[0]=id;
                    s1[1]=name;
                    s1[2]= jsonObj.getString("dacu");

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

            return s1;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

        }

    }
}
