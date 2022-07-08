package com.example.diabcalc;


import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Αυτό είναι το αρχίκο activity, το οποίο ανοίγει με το που ανοίξει ο χρήστης την εφαρμογή.
 * Από εδώ μπορεί να επιλέξει αν θέλει να προσθέσει ή να διαγράψει κάποιο φαγητό απο την Βάση
 * Δεδομένων. Καθώς και να ξεκινήσει τον υπολογισμό για το επόμενο γεύμα του.
 */
public class MainPage extends AppCompatActivity {

    ArrayList<Food> foods;

    public static void sort(ArrayList<Food> arrayList) {
        Arrays.sort(new ArrayList[]{arrayList});
        for (int i = 0; i < arrayList.size() - 1; i++) {
            for (int j = i + 1; j < arrayList.size(); j++) {
                if (arrayList.get(i).getFoodName().compareTo(arrayList.get(j).getFoodName()) > 0) {
                    Food f = arrayList.get(i);
                    arrayList.set(i, arrayList.get(j));
                    arrayList.set(j, f);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        getFoods();

        Button start = findViewById(R.id.start);
        Button add = findViewById(R.id.add);
        Button delete = findViewById(R.id.delete);

        start.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            sort(foods);
            intent.putParcelableArrayListExtra("foods", foods);
            startActivityForResult(intent, 1);
        });

        add.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, AddActivity.class);
            startActivityForResult(intent, 1);
        });

        delete.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, DeleteActivity.class);
            sort(foods);
            intent.putParcelableArrayListExtra("foods", foods);
            startActivityForResult(intent, 1);
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getFoods();
        if (resultCode == -1) {
            Intent intent = new Intent(MainPage.this, DeleteActivity.class);
            intent.putParcelableArrayListExtra("foods", foods);
            startActivityForResult(intent, 1);
        }
    }

    private void getFoods() {
        sqlHandler sqlHandler = new sqlHandler(this, null, 1);
        if (sqlHandler.size() == 0)
            sqlHandler.insertFromFile();
        foods = new ArrayList<>();
        foods = sqlHandler.getAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void item1(MenuItem item) {
        System.out.println("Item 1 was Clicked!");
    }

    public void item2(MenuItem item) {
        System.out.println("Item 2 was Clicked!");
    }

    public void item3(MenuItem item) {
        System.out.println("Item 3 was Clicked!");
    }
}