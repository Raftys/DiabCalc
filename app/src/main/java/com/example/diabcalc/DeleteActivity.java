package com.example.diabcalc;


import static com.example.diabcalc.MainPage.sort;

import android.content.Context;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;


/**
 * Αυτό το activity είναι φτιαγμένο για να μπορεί ο χρήστης να διαγράφει φαγητά από την Βάση Δεδομένων.
 * Εμφανίζονται μία λίστα με όλα τα υπάρχων φαγητά και ο χρήτης μπορεί να επιλέξει οποιοδήποτε για διαγράφη.
 * Η εντολή εκτελείται με το πάτημα του Button Delete. Εμφανίζεται ένα Alert για να επιβεβαιώση ο χρήστης ότι
 * θέλει να διαργάψει ΜΟΝΙΜΑ το συγκεκριμένο φαγητό.
 */
public class DeleteActivity extends AppCompatActivity {

    private Context context;
    ListView foods_list;
    ArrayAdapter<String> adapter;
    ArrayList<Food> list;
    ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        context = this;

        arrayList = new ArrayList<>();
        list = getIntent().getParcelableArrayListExtra("foods");
        for (Food food : list) {
            arrayList.add(food.getFoodName());
        }
        adapter = new ArrayAdapter<>(
                DeleteActivity.this,
                android.R.layout.simple_list_item_1,
                arrayList
        );
        foods_list = findViewById(R.id.list_to_delete);
        foods_list.setAdapter(adapter);

        foods_list.setOnItemClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are You Sure DUDE?")
                    .setPositiveButton("DAMN YEAH", (dialog, id) -> {
                        sqlHandler sqlHandler = new sqlHandler(context, null, 1);
                        System.out.println("name:" + arrayList.get(i));
                        if (sqlHandler.deleteFood(arrayList.get(i))) {
                            Toast.makeText(DeleteActivity.this, "Food Deleted", Toast.LENGTH_SHORT).show();
                            list = new ArrayList<>();
                            list = sqlHandler.getAll();
                            sort(list);
                            arrayList = new ArrayList<>();
                            for (Food food : list) {
                                arrayList.add(food.getFoodName());
                            }
                            adapter = new ArrayAdapter<>(
                                    DeleteActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    arrayList
                            );
                            foods_list = findViewById(R.id.list_to_delete);
                            foods_list.setAdapter(adapter);
                        } else
                            Toast.makeText(DeleteActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }).setNegativeButton("NAH", (dialog, id) -> {
                    });
            builder.create().show();
        });
    }

}