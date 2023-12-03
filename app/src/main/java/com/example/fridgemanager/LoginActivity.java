package com.example.fridgemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    final ArrayList<Integer> fails = new ArrayList<>(Arrays.asList(0));

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);

    }

    public void onClick(View v) {

        if ( fails.get(0) >= 3 ) {
            Toast.makeText(getApplicationContext(), "After trying to reach or exceed the limit of 3 times, you have been banned from logging in!", Toast.LENGTH_SHORT).show();
        } else {
            EditText usernameEditText = findViewById(R.id.usernameEditText);
            EditText passwordEditText = findViewById(R.id.passwordEditText);

            String username = "Curry";
            // String username = "";
            String password = "!@#$%^";
            // String password = "";

            String usernameInput = usernameEditText.getText().toString();
            String passwordInput = passwordEditText.getText().toString();

            if ( !usernameInput.equals(username) || !passwordInput.equals(password) ) {
                Toast.makeText(getApplicationContext(), String.format("Wrong username %s or password %s", usernameInput, passwordInput), Toast.LENGTH_SHORT).show();
                fails.set(0, fails.get(0)+1);
            } else {
                Toast.makeText(getApplicationContext(), "Redirecting…", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        }
    }

}