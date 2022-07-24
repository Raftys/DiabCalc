package com.example.diabcalc;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        EditText name = findViewById(R.id.name);
        EditText carbohydrates = findViewById(R.id.carbohydrates);
        EditText fat = findViewById(R.id.fat);
        EditText hour = findViewById(R.id.hour);
        context = this;

        Button add = findViewById(R.id.addFoodToDB);

        add.setOnClickListener(view -> {
            if (!name.getText().toString().isEmpty() &&
                    !carbohydrates.getText().toString().isEmpty() &&
                    !fat.getText().toString().isEmpty() &&
                    !hour.getText().toString().isEmpty()) {
                if (nameExist(name.getText().toString().trim().substring(0, 1).toUpperCase() + name.getText().toString().trim().substring(1)))
                    Toast.makeText(AddActivity.this, "Food Already Exists", Toast.LENGTH_SHORT).show();
                else {
                    sqlHandler sqlHandler = new sqlHandler(context, null, 1);
                    int k = generateId(sqlHandler);
                    sqlHandler.addFood(new Food(k,
                            name.getText().toString().trim().substring(0, 1).toUpperCase() + name.getText().toString().trim().substring(1),
                            Double.parseDouble(carbohydrates.getText().toString()),
                            Double.parseDouble(fat.getText().toString()),
                            Double.parseDouble(hour.getText().toString())));
                    Toast.makeText(AddActivity.this, "Food Added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddActivity.this, "Not Enough Information", Toast.LENGTH_SHORT).show();
            }
        });
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

}
