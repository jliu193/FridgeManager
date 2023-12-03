package com.example.fridgemanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

public class AddActivity extends AppCompatActivity {

    private DatabaseWrapper db;

    EditText foodNameEditText;
    NumberPicker countPicker;
    DatePicker expirationPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        foodNameEditText = findViewById(R.id.foodNameEditText);
        countPicker = findViewById(R.id.countPicker);
        expirationPicker = findViewById(R.id.expirationPicker);

        countPicker.setMinValue(1);
        countPicker.setMaxValue(99);
        countPicker.setValue(0);

        db = new DatabaseWrapper(this);
    }

    public void onClickSubmit(View v) {

        String foodNameInput = foodNameEditText.getText().toString();
        Integer countInput = countPicker.getValue();
        Integer expirationYear = expirationPicker.getYear();
        Integer expirationMonth = expirationPicker.getMonth() + 1;
        Integer expirationDay = expirationPicker.getDayOfMonth();
        LocalDate expiration = LocalDate.of(expirationYear, expirationMonth, expirationDay);

        Log.i("Add", foodNameInput + countInput + expiration);

        try {
            Boolean isInserted = db.insertData(foodNameInput, countInput, expiration.toString());
            Log.i("Add", isInserted.toString());
            db.close();
        } catch (Exception e) {
            Log.i("Add", e.toString());
        }

    }

    public void onClickCountDecrease(View v) {
        countPicker.setValue(countPicker.getValue() - 1);
    }

    public void onClickCountIncrease(View v) {
        countPicker.setValue(countPicker.getValue() + 1);
    }

    public void onClickHome(View v) {
        try {
            Intent i = new Intent(AddActivity.this, MainActivity.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i("Add", e.toString());
        }
    }

    public void onClickRepo(View v) {
        try {
            Intent i = new Intent(AddActivity.this, RepoActivity.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i("Add", e.toString());
        }
    }

}