package com.example.fridgemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {

    private DatabaseWrapper db;
    private FusedLocationProviderClient fusedLocationClient;

    ArrayList<ItemDomainObject> itemDomainObjects;

    TextView contentExpiredTextView;
    TextView contentExpiresTodayTextView;
    TextView contentExpiresTomorrowTextView;
    TextView contentExpiresThisWeekTextView;
    TextView todayDateTextView;
    TextView locationContentTextView;
    TextView temperatureContentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseWrapper(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        itemDomainObjects = new ArrayList<>();

        contentExpiredTextView = findViewById(R.id.contentExpiredTextView);
        contentExpiresTodayTextView = findViewById(R.id.contentExpiresTodayTextView);
        contentExpiresTomorrowTextView = findViewById(R.id.contentExpiresTomorrowTextView);
        contentExpiresThisWeekTextView = findViewById(R.id.contentExpiresThisWeekTextView);
        todayDateTextView = findViewById(R.id.todayDateTextView);
        locationContentTextView = findViewById(R.id.locationContentTextView);
        temperatureContentTextView = findViewById(R.id.temperatureContentTextView);

        contentExpiredTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        contentExpiresTodayTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        contentExpiresTomorrowTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        contentExpiresThisWeekTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        // fetch location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("HOME", "location information will not be loaded because location permission denied");
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // get temperature by location
                            try {
                                URL url = new URL(String.format("https://api.open-meteo.com/v1/forecast?latitude=%d&longitude=%d&current=temperature_2m", location.getLatitude(), location.getLongitude()));
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");

                                int responseCode = connection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    InputStream inputStream = connection.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    StringBuilder stringBuilder = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        stringBuilder.append(line);
                                    }
                                    reader.close();
                                    Gson gson = new Gson();
                                    JsonObject jsonObject = gson.fromJson(stringBuilder.toString(), JsonObject.class);
                                    JsonObject currentObject = jsonObject.getAsJsonObject("current");
                                    if (currentObject != null) {
                                        Double temperature = currentObject.getAsJsonPrimitive("temperature_2m").getAsDouble();
                                        temperatureContentTextView.setText(temperature.toString());
                                        System.out.println("Temperature: " + temperature);
                                    }
                                }
                                connection.disconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        // fetch data
        Cursor cursor = db.getAllData();

        String pattern = "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        Log.i("HOME", "FIN");

        if (null != cursor && cursor.moveToFirst()) {
            do {
                ItemDomainObject itemDomainObject = new ItemDomainObject(
                        // id
                        cursor.getInt(0),
                        // name
                        cursor.getString(1),
                        // count
                        cursor.getInt(2),
                        // date
                        LocalDate.parse(cursor.getString(3), formatter)
                );
                itemDomainObjects.add(itemDomainObject);
            } while (cursor.moveToNext());
        }

        db.close();

        ArrayList<ItemDomainObject> expiredList = new ArrayList<>();
        ArrayList<ItemDomainObject> expiresTodayList = new ArrayList<>();
        ArrayList<ItemDomainObject> expiresTomorrowList = new ArrayList<>();
        ArrayList<ItemDomainObject> expiresThisWeekList = new ArrayList<>();

        LocalDate today = LocalDate.now();
        todayDateTextView.setText(today.toString());
        Log.i("HOME TODAY", today.toString());

        for ( ItemDomainObject item : itemDomainObjects ) {
            Log.i("HOME", item.toString());
            LocalDate date = item.getExpiredDate();
            Log.i("HOME", date.toString());
            if (date.isBefore(today) && !date.isEqual(today)) {
                expiredList.add(item);
            } else if (date.isEqual(today)) {
                expiresTodayList.add(item);
            } else if (date.isEqual(today.plusDays(1))) {
                expiresTomorrowList.add(item);
            } else if (date.isAfter(today.plusDays(1)) && date.isBefore(today.plusDays(7))) {
                expiresThisWeekList.add(item);
            }
        }

        contentExpiredTextView.setText(toText(expiredList));
        contentExpiresTodayTextView.setText(toText(expiresTodayList));
        contentExpiresTomorrowTextView.setText(toText(expiresTomorrowList));
        contentExpiresThisWeekTextView.setText(toText(expiresThisWeekList));

    }

    public void onClickAdd(View v) {
        try {
            Intent i = new Intent(MainActivity.this, AddActivity.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i("Main", e.toString());
        }
    }

    public void onClickRepo(View v) {
        try {
            Intent i = new Intent(MainActivity.this, RepoActivity.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i("Add", e.toString());
        }
    }

    public String toText(ArrayList<ItemDomainObject> list) {
        String result = "";
        if ( list.size() == 0 ) {
            return "None";
        }
        for (ItemDomainObject item : list ) {
            result += String.format(
                    "ID: %d, Name: %s, Count: %d\n",
                    item.getId(),
                    item.getName(),
                    item.getCount()
            );
        }
        return result;
    }


}