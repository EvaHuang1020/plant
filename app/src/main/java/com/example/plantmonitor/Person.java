package com.example.plantmonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Person extends AppCompatActivity {


    private TextView person_name,person_account;
    final OkHttpClient client = new OkHttpClient();
    public ExecutorService service = Executors.newSingleThreadExecutor();
    public MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();
    Global gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        gv = (Global) getApplicationContext();
        person_name=findViewById(R.id.person_name);
        person_account=findViewById(R.id.person_account);

        String loginname=gv.getloginname();
        String loginpassword=gv.getloginpassword();
        service.submit(new Runnable() {
            @Override
            public void run() {

                JSONObject jsonObjectsend = new JSONObject();
                try {
                    jsonObjectsend.put("username",loginname);
                    jsonObjectsend.put("password",loginpassword);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                RequestBody body = RequestBody.create(JSON,jsonObjectsend.toString());

                Request request = new Request.Builder().url("http://192.168.10.114:9402/Login/getperson").post(body).build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();
                    Log.e("", resStr);
                    JSONObject jsonObjectrespon = new JSONObject(resStr);
                    JSONObject User = jsonObjectrespon.getJSONObject("User");


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        person_name.setText(User.getString("name"));
                        person_account.setText(User.getString("username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });



                }  catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

}