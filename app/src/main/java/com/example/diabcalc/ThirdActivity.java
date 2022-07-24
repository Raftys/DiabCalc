package com.example.diabcalc;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Σε αυτό το activity, εμφανίζεται μία λίστα (finalFoods) με τα φαγητά που έχει επιλέξει ο χρήστης
 * ότι θα καταναλώσει. Εμφανίζεται το όνομα του φαγητού και οι μονάδες τον υδατανθράκων και των λιπαρών
 * αναλόγως με τα πόσα γραμμάρια φαγητού έχει επιλέξει ότι θα καταναλώσει ο χρήστης. Καθώς και το σύνολο
 * των μονάδων για κάθε φαγητό, αλλά και το σύνολο των μονάδων, υδατανθράκων, λιπαρών και συνολικά, για
 * όλα τα φαγητά. Επίσης, με τη χρήση του button ADD ο χρήστης μπορεί να προσθέσει ένα φαγητό στη λίστα
 * με τα φαγητά που πρόκειται να καταναλώσει. Ενώ με τη χρήση του button NEW ο χρήστης μπορεί να αδειάζει
 * τη λίστα με τα φαγητά που πρόκειται να καταναλώσει, ώστε να δημιουργήσει ένα καινούργιο μενού.
 */
public class ThirdActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;
    Intent intent;
    ArrayList<Food> finalFoods;
    int bool;
    private String menu_name = "";

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        intent = getIntent();

        bool = intent.getIntExtra("favorite",0);

        /*
          Λίστα με τα επιλεγμένα φαγητά
         */
        finalFoods = intent.getParcelableArrayListExtra("list");
        ArrayList<String> list = new ArrayList<>();
        for (Food f : finalFoods)
            list.add("Name: " + f.getFoodName() +
                    "\nGrammars: " + String.format("%.2f", f.getFoodGrammar()) +
                    "\nCarbohydrates: " + String.format("%.2f", f.getFoodCar()) +
                    "\nFat: " + String.format("%.2f", f.getFoodFat()) + " in " + f.getFoodHour() + " hour(s)." +
                    "\nSum: " + String.format("%.2f", f.getFoodCar() + f.getFoodFat()));

        listView = findViewById(R.id.final_list);
        adapter = new ArrayAdapter<>(
                ThirdActivity.this,
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(adapter);


        /*
          Σύνολο μονάδων
         */
        TextView textView = findViewById(R.id.textView2);
        double cars = 0;
        double fats = 0;
        for (Food f : finalFoods) {
            cars += f.getFoodCar();
            fats += f.getFoodFat();
        }
        double sum = cars + fats;
        textView.setText("Total Units: " + String.format("%.2f", sum) +
                "\nTotal Carbohydrates: " + String.format("%.2f", cars) +
                "\nTotal Fat: " + String.format("%.2f", fats) +
                " in " + finalFoods.get(0).getFoodHour() + " hour(s).");


        /*
         * κουμπί για επιλογή/προσθήκη φαγητού στο μενού του χρήστη από τη λίστα με όλα τα φαγητά
         */
        Button button = findViewById(R.id.addFood);
        button.setOnClickListener(view -> {
            Intent i = new Intent();
            i.putParcelableArrayListExtra("list", finalFoods);
            setResult(1, i);
            finish();
        });

        /*
         * καινούργιο μενού
         */
        Button button2 = findViewById(R.id.newMenu);
        button2.setOnClickListener(view -> {
            Intent i = new Intent();
            setResult(2, i);
            finish();
        });


        Button button3 = findViewById(R.id.back4);
        button3.setOnClickListener(view -> {
            Intent i = new Intent();
            Food food = finalFoods.get(finalFoods.size() - 1);
            intent.putExtra("info", food);
            i.putParcelableArrayListExtra("list", finalFoods);
            setResult(-1, i);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.third_activity_menu, menu);
        MenuItem item = menu.getItem(0);
        if(bool == 1) {
            item.setIcon(android.R.drawable.star_big_on);
            item.setChecked(true);
        }
        sqlHandler sqlHandler = new sqlHandler(ThirdActivity.this,null,1);
        item.setOnMenuItemClickListener(menuItem -> {
            if(item.isChecked()) {
                item.setIcon(android.R.drawable.star_big_off);
                item.setChecked(false);
                String name =intent.getStringExtra("menu_name");
                if(name != null)
                    sqlHandler.deleteMenu(intent.getStringExtra("menu_name"));
                else
                    sqlHandler.deleteMenu(menu_name);
                Toast.makeText(this, "Menu Removed From Favorites", Toast.LENGTH_SHORT).show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Menu Name");

                EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    menu_name = input.getText().toString();
                    HashMap<String, ArrayList<Food>> hashMap = sqlHandler.getMenu();
                    if(hashMap.containsKey(menu_name))
                        Toast.makeText(this, "Menu Name Already Exists", Toast.LENGTH_SHORT).show();
                    else {
                        sqlHandler.addMenu(menu_name, finalFoods);
                        item.setIcon(android.R.drawable.star_big_on);
                        item.setChecked(true);
                        Toast.makeText(this, "Menu Added To Favorites", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            }
            return false;
        });
        return super.onCreateOptionsMenu(menu);

    }

}