package com.example.diabcalc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


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
    ArrayList<Food> finalFoods;
    int bool;
    private String menu_name = "";
    Context context;
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    @SuppressLint({"DefaultLocale", "SetTextI18n", "UseSupportActionBar"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        context = this;
        activity = this;

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /*
          Λίστα με τα επιλεγμένα φαγητά
         */
        finalFoods = getIntent().getParcelableArrayListExtra("list");
        ArrayList<String> list = new ArrayList<>();
        for (Food f : finalFoods)
            list.add(getResources().getString(R.string.name) + ": " + f.getFoodName() +
                    "\n" + getResources().getString(R.string.category) + ": " + f.getFoodCategory() +
                    "\n" + getResources().getString(R.string.grammars) + ": " + String.format("%.2f", f.getFoodGrammar()) +
                    "\n" + getResources().getString(R.string.carbohydrates) + ": " + String.format("%.2f", f.getFoodCar()) +
                    "\n" + getResources().getString(R.string.fat) + ": " + String.format("%.2f", f.getFoodFat()) +
                    " "  + getResources().getString(R.string.in) + ": " + String.format("%.2f", f.getFoodHour()) + " " + getResources().getString(R.string.hour) +
                    "\n" + getResources().getString(R.string.sum) + ": " + String.format("%.2f", f.getFoodCar() + f.getFoodFat()));
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
        textView.setText(getResources().getString(R.string.totalUnits) + ": " + String.format("%.2f", sum) +
                "\n" + getResources().getString(R.string.totalCarbohydrates) + ": " + String.format("%.2f", cars) +
                "\n" + getResources().getString(R.string.totalFat) + ": " + String.format("%.2f", fats) +
                " "  + getResources().getString(R.string.in) + ": " + finalFoods.get(0).getFoodHour() + " " + getResources().getString(R.string.hour));


        /*κουμπί για επιλογή/προσθήκη φαγητού στο μενού του χρήστη από τη λίστα με όλα τα φαγητά**/
        Button add = findViewById(R.id.addFood);
        add.setOnClickListener(view -> {
            Intent intent = new Intent(ThirdActivity.this,MainActivity.class);
            intent.putParcelableArrayListExtra("list", finalFoods);
            startActivity(intent);
        });

        /*καινούργιο μενού**/
        Button newMenu = findViewById(R.id.newMenu);
        if(Objects.equals(getIntent().getStringExtra("activity"), "favorite"))
            newMenu.setVisibility(View.INVISIBLE);
        newMenu.setOnClickListener(view -> {
            Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
            startActivity(intent);
        });

        /*Πίσω**/
        Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(ThirdActivity.this, MainPage.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.third_activity_menu, menu);
        MenuItem item = menu.getItem(0);
        if(bool == 1) {
            item.setIcon(android.R.drawable.star_big_on);
            item.setChecked(true);
        }
        SqlHandler sqlHandler = new SqlHandler(ThirdActivity.this,null,1);
        item.setOnMenuItemClickListener(menuItem -> {
            if(item.isChecked()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getResources().getString(R.string.areYouSureMenu))
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                            item.setIcon(android.R.drawable.star_big_off);
                            item.setChecked(false);
                            String name =getIntent().getStringExtra("menu_name");
                            if(name != null)
                                sqlHandler.deleteMenu(getIntent().getStringExtra("menu_name"));
                            else
                                sqlHandler.deleteMenu(menu_name);
                            Toast.makeText(this, getResources().getString(R.string.menuRemoved), Toast.LENGTH_SHORT).show();
                        }).setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> {
                        });
                builder.create().show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.menuName));

                EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                    menu_name = input.getText().toString();
                    HashMap<String, ArrayList<Food>> hashMap = sqlHandler.getMenu();
                    if(hashMap.containsKey(menu_name))
                        Toast.makeText(this, getResources().getString(R.string.menuAlreadyExists), Toast.LENGTH_SHORT).show();
                    else {
                        sqlHandler.addMenu(finalFoods,menu_name);
                        item.setIcon(android.R.drawable.star_big_on);
                        item.setChecked(true);
                        Toast.makeText(this, getResources().getString(R.string.menuAdded), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.cancel());
                builder.show();
            }
            return false;
        });

        if(Objects.equals(getIntent().getStringExtra("activity"), "favorite")){
            MenuItem item1 = menu.getItem(1);
            item1.setVisible(true);
            item1.setOnMenuItemClickListener(menuItem -> {
                finish();
                return false;
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

}