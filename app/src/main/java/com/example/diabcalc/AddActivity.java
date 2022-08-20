package com.example.diabcalc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Αυτό το activity είναι φτιαγμένο για να μπορεί ο χρήστης να προσθέτει φαγητά στην Βάση Δεδομένων.
 * Εμφανίζονται 4 editText για να μπορέσει ο χρήτης να ορίσει το όνομα του φαγητού, τους υδατάνθερακες,
 * τα λιπαρά και την ώρα που χρειάζεται για τις μονάδες των λιπαρών. Με το κουμπί ADD, το φαγητό προσθέται
 * στην Βάση. Ενώ ταυτόχρονα εμφανίζεται το κατάλληλο μύνημα σε περίπτωση που ο χρήστης δεν συμπληρώσει σωστά
 * όλα τα πεδία ή σε περίπτωση που το φαγητό έχει προσθεθεί.
 */
public class AddActivity extends AppCompatActivity {

    /**
     * Κατηγορίες
     */
    private AutoCompleteTextView autoCompleteTextViewCategory;
    private AutoCompleteTextView autoCompleteTextViewBrand;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> categories;
    ArrayList<String> brands;

    /**
     * last_activity: για να ξέρω από που ήρθε
     */
    String last_activity;
    Food food;
    String old_food;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    private Context context;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.context = this;
        activity = this;
        SqlHandler sqlHandler = new SqlHandler(this, null,1);
        old_food = "";

        /*Σε περίπτωση που μπώ στο Activity για επεξεργασία δεδομένων**/
        last_activity = getIntent().getStringExtra("activity");

        /*EditTexts**/
        EditText name = findViewById(R.id.name);
        EditText carbohydrates = findViewById(R.id.carbohydrates);
        EditText fat = findViewById(R.id.fat);
        EditText hour = findViewById(R.id.hour);
        EditText description = findViewById(R.id.description);

        /*TextViews**/
        TextView carbohydratesReadable = findViewById(R.id.carbohydratesReadable);
        TextView fatReadable = findViewById(R.id.fatReadable);
        carbohydratesReadable.setText(getResources().getString(R.string.carbohydrates) +" (100g)");
        fatReadable.setText(getResources().getString(R.string.fat) + " (100g)");

        /*Category**/
        setCategory(sqlHandler);

        /*Brand**/
        setBrand(sqlHandler);

        /*Button**/
        Button add = findViewById(R.id.addFoodToDB);

        /*DeleteActivity**/
        if(Objects.equals(last_activity, "edit")) {
            food = getIntent().getParcelableExtra("info");
            old_food = food.getFoodName();
            name.setText(food.getFoodName());
            carbohydrates.setText(Double.toString(food.getFoodCar()));
            fat.setText(Double.toString(food.getFoodFat()));
            hour.setText(Double.toString(food.getFoodHour()));
            description.setText(food.getFoodDescription());
            autoCompleteTextViewCategory.setText(food.getFoodCategory());
            autoCompleteTextViewBrand.setText(food.getFoodBrand());
            add.setText(getResources().getText(R.string.edit));
        }

        /*Προσθήκη φαγητόυ ή επεξεργασία αυτού*/
        add.setOnClickListener(view -> {
            /*μη επαρκείς πληροφορίες*/
            if (!name.getText().toString().isEmpty() &&
                    !carbohydrates.getText().toString().isEmpty() &&
                    !fat.getText().toString().isEmpty() &&
                    !hour.getText().toString().isEmpty()) {
                /*ήρθε για edit και το όνομα υπάρχει ήδη*/
                if (nameExist(name.getText().toString().trim().substring(0, 1).toUpperCase() + name.getText().toString().trim().substring(1)) && !last_activity.equals("edit") )
                    Toast.makeText(AddActivity.this, getResources().getString(R.string.foodExists), Toast.LENGTH_SHORT).show();
                int k = generateId(sqlHandler);
                Food food = new Food(k,
                        name.getText().toString().trim().substring(0, 1).toUpperCase() + name.getText().toString().trim().substring(1),
                        "No Category",
                        "No Brand",
                        description.getText().toString(),
                        Double.parseDouble(carbohydrates.getText().toString()),
                        Double.parseDouble(fat.getText().toString()),
                        Double.parseDouble(hour.getText().toString()),
                        100,
                        1,
                        0,
                        0);
                /*Η κατηγορία δεν υπάρχει*/
                if(!categories.contains(autoCompleteTextViewCategory.getText().toString()) && !autoCompleteTextViewCategory.getText().toString().equals("")) {
                    food.setFoodCategory(autoCompleteTextViewCategory.getText().toString());
                    addCategory(sqlHandler);
                }
                else {
                    food.setFoodCategory(autoCompleteTextViewCategory.getText().toString());
                }
                /*Η μάρκα δεν υπάρχει*/
                if(!brands.contains(autoCompleteTextViewBrand.getText().toString()) && !autoCompleteTextViewBrand.getText().toString().equals("")) {
                    food.setFoodCategory(autoCompleteTextViewBrand.getText().toString());
                    addBrand(sqlHandler);
                }
                else {
                    food.setFoodBrand(autoCompleteTextViewBrand.getText().toString());
                }
                addFood(sqlHandler,food);
            } else {
                Toast.makeText(AddActivity.this, getResources().getString(R.string.notEnoughInformation), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Διαγραφή φαγητού αν έχω έρθει για edit
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        MenuItem item = menu.findItem(R.id.delete_food);
        if(Objects.equals(last_activity,"edit")) {
            item.setVisible(true);
            item.setOnMenuItemClickListener(menuItem -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getResources().getString(R.string.areYouSure))
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                            SqlHandler sqlHandler = new SqlHandler(context, null, 1);
                            if (sqlHandler.deleteFood(old_food)) {
                                sqlHandler.resetMenu(food.getFoodName(),"null");
                                sqlHandler.close();
                                Toast.makeText(AddActivity.this, getResources().getString(R.string.deleteConfirm), Toast.LENGTH_SHORT).show();
                                finish();
                            } else
                                Toast.makeText(AddActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }).setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> {
                        });
                builder.create().show();
                return false;
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @param name όνομα
     * @return true αν υπάρχει στην βάση φαγητό με αυτό το όνομα
     */
    public boolean nameExist(String name) {
        SqlHandler sqlHandler = new SqlHandler(context, null, 1);
        Food food = sqlHandler.findFood(name);
        return food != null;
    }

    /**
     * random id
     * @return random int
     */
    public int generateId(SqlHandler sqlHandler) {
        ArrayList<Food> foods = sqlHandler.getAll();
        for (int i = 0; i < foods.size(); i++)
            if (i != foods.get(i).getFoodId())
                return i;
        return foods.get(foods.size() - 1).getFoodId() + 1;
    }

    /**
     * Προσθήκη κατηγορίες στην βάση
     * @param sqlHandler βάση δεδομένων
     */
    public void addCategory(SqlHandler sqlHandler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.categoryAdd))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    categories = new ArrayList<>();
                    categories = sqlHandler.getInfo(SqlHandler.COLUMN_CATEGORY);
                    arrayAdapter = new ArrayAdapter<>(
                            AddActivity.this,
                            android.R.layout.simple_list_item_1,
                            categories
                    );
                    autoCompleteTextViewCategory.setAdapter(arrayAdapter);
                    autoCompleteTextViewCategory.setThreshold(1);
                }).setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> {
                });
        builder.create().show();
    }

    /**
     * Προσθήκη μάρκας στην βάση
     * @param sqlHandler βάση δεδομένων
     */
    public void addBrand(SqlHandler sqlHandler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getResources().getString(R.string.brandAdd))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    brands = new ArrayList<>();
                    brands = sqlHandler.getInfo(SqlHandler.COLUMN_BRAND);
                    arrayAdapter = new ArrayAdapter<>(
                            AddActivity.this,
                            android.R.layout.simple_list_item_1,
                            brands
                    );
                    autoCompleteTextViewBrand.setAdapter(arrayAdapter);
                    autoCompleteTextViewBrand.setThreshold(1);
                }).setNegativeButton(getResources().getString(R.string.no), (dialog, id) -> {
                });
        builder.create().show();
    }

    /**
     * Προσθήκη φαγητού στην βάση
     * @param sqlHandler βάση δεδομένων
     * @param food φαγητό
     */
    public void addFood(SqlHandler sqlHandler, Food food) {
        if(last_activity.equals("edit")) {
            sqlHandler.deleteFood(old_food);
            Toast.makeText(AddActivity.this, getResources().getString(R.string.foodEdited), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(AddActivity.this, getResources().getString(R.string.foodAdded), Toast.LENGTH_SHORT).show();
        sqlHandler.addFood(food,old_food);
    }

    /**
     * Set Categories for view
     * @param sqlHandler sqlHandler
     */
    public void setCategory(SqlHandler sqlHandler) {
        autoCompleteTextViewCategory = findViewById(R.id.categoryText);
        categories = sqlHandler.getInfo(SqlHandler.COLUMN_CATEGORY);
        categories.remove("No Category");
        arrayAdapter = new ArrayAdapter<>(
                AddActivity.this,
                android.R.layout.simple_list_item_1,
                categories
        );
        autoCompleteTextViewCategory.setAdapter(arrayAdapter);
        autoCompleteTextViewCategory.setThreshold(1);
    }

    /**
     * Set Brands for view
     * @param sqlHandler sqlHandler
     */
    @SuppressLint("SetTextI18n")
    public void setBrand(SqlHandler sqlHandler) {
        autoCompleteTextViewBrand = findViewById(R.id.brandText);
        brands = sqlHandler.getInfo(SqlHandler.COLUMN_BRAND);
        brands.remove("No Brand");
        arrayAdapter = new ArrayAdapter<>(
                AddActivity.this,
                android.R.layout.simple_list_item_1,
                brands
        );
        autoCompleteTextViewBrand.setAdapter(arrayAdapter);
        autoCompleteTextViewBrand.setThreshold(1);
    }

}
