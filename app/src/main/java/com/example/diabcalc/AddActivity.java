package com.example.diabcalc;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;

/**
 * Αυτό το activity είναι φτιαγμένο για να μπορεί ο χρήστης να προσθέτει φαγητά στην Βάση Δεδομένων.
 * Εμφανίζονται 4 editText για να μπορέσει ο χρήτης να ορίσει το όνομα του φαγητού, τους υδατάνθερακες,
 * τα λιπαρά και την ώρα που χρειάζεται για τις μονάδες των λιπαρών. Με το κουμπί ADD, το φαγητό προσθέται
 * στην Βάση. Ενώ ταυτόχρονα εμφανίζεται το κατάλληλο μύνημα σε περίπτωση που ο χρήστης δεν συμπληρώσει σωστά
 * όλα τα πεδία ή σε περίπτωση που το φαγητό έχει προσθεθεί.
 */
public class AddActivity extends AppCompatActivity {

    private Context context;
    private AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        EditText name = findViewById(R.id.name);
        EditText carbohydrates = findViewById(R.id.carbohydrates);
        EditText fat = findViewById(R.id.fat);
        EditText hour = findViewById(R.id.hour);
        EditText description = findViewById(R.id.description);
        context = this;

        fat.setHint(getResources().getString(R.string.fat) + " (100)");
        carbohydrates.setHint(getResources().getString(R.string.carbohydrates) +" (100)");

        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autoTextview);
        sqlHandler sqlHandler = new sqlHandler(this, null,1);
        categories = sqlHandler.getCategories();
        arrayAdapter = new ArrayAdapter<>(
                AddActivity.this,
                android.R.layout.simple_list_item_1,
                categories
        );
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setThreshold(1);



        Button add = findViewById(R.id.addFoodToDB);

        add.setOnClickListener(view -> {
            if (!name.getText().toString().isEmpty() &&
                    !carbohydrates.getText().toString().isEmpty() &&
                    !fat.getText().toString().isEmpty() &&
                    !hour.getText().toString().isEmpty()) {
                if (nameExist(name.getText().toString().trim().substring(0, 1).toUpperCase() + name.getText().toString().trim().substring(1)))
                    Toast.makeText(AddActivity.this, getResources().getString(R.string.foodExists), Toast.LENGTH_SHORT).show();
                int k = generateId(sqlHandler);
                Food food = new Food(k,
                        name.getText().toString().trim().substring(0, 1).toUpperCase() + name.getText().toString().trim().substring(1),
                        "null",
                        description.getText().toString(),
                        Double.parseDouble(carbohydrates.getText().toString()),
                        Double.parseDouble(fat.getText().toString()),
                        Double.parseDouble(hour.getText().toString()),
                        100,
                        1);
                if(!categories.contains(autoCompleteTextView.getText().toString())) {
                    food.setFoodCategory(autoCompleteTextView.getText().toString());
                    addCategory(sqlHandler, food);
                }
                else {
                    food.setFoodCategory(autoCompleteTextView.getText().toString());
                    addFood(sqlHandler,food);
                }
            } else {
                Toast.makeText(AddActivity.this, getResources().getString(R.string.notEnoughInformation), Toast.LENGTH_SHORT).show();
                System.out.println(autoCompleteTextView.getText());

            }
        });
    }

    public void addCategory(sqlHandler sqlHandler, Food food) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.categoryAdd))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    addFood(sqlHandler,food);
                    categories = new ArrayList<>();
                    categories = sqlHandler.getCategories();
                    arrayAdapter = new ArrayAdapter<>(
                            AddActivity.this,
                            android.R.layout.simple_list_item_1,
                            categories
                    );
                    autoCompleteTextView.setAdapter(arrayAdapter);
                    autoCompleteTextView.setThreshold(1);
                }).setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> {
                });
        builder.create().show();
    }

    public boolean nameExist(String name) {
        sqlHandler sqlHandler = new sqlHandler(context, null, 1);
        Food food = sqlHandler.findFood(name);
        return food != null;
    }

    public int generateId(sqlHandler sqlHandler) {
        ArrayList<Food> foods = sqlHandler.getAll();
        for (int i = 0; i < foods.size(); i++)
            if (i != foods.get(i).getFoodId())
                return i;
        return foods.get(foods.size() - 1).getFoodId() + 1;
    }

    public void addFood(sqlHandler sqlHandler, Food food) {
        sqlHandler.addFood(food);
        Toast.makeText(AddActivity.this, getResources().getString(R.string.foodAdded), Toast.LENGTH_SHORT).show();
    }

}
