package com.example.fridgemanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RepoActivity extends AppCompatActivity {

    private DatabaseWrapper db;

    ArrayList<ItemDomainObject> itemDomainObjects;

    TextView contentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        db = new DatabaseWrapper(this);

        itemDomainObjects = new ArrayList<>();

        contentTextView = findViewById(R.id.contentTextView);
        contentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        // fetch data
        Cursor cursor = db.getAllData();

        String pattern = "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        Log.i("REPO", "FETCH FIN");

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

        contentTextView.setText(toText(itemDomainObjects));

    }

    public void onClickHome(View v) {
        try {
            Intent i = new Intent(RepoActivity.this, MainActivity.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i("Repo", e.toString());
        }
    }

    public void onClickAdd(View v) {
        try {
            Intent i = new Intent(RepoActivity.this, AddActivity.class);
            startActivity(i);
        } catch (Exception e) {
            Log.i("Repo", e.toString());
        }

    }

    public String toText(ArrayList<ItemDomainObject> list) {
        String result = "";
        if ( list.size() == 0 ) {
            return "None";
        }
        for (ItemDomainObject item : list ) {
            result += String.format(
                    "No. %d %s %d Expire: %s\n",
                    item.getId(),
                    item.getName(),
                    item.getCount(),
                    item.getExpiredDate()
            );
        }
        return result;
    }
}