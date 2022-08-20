package com.example.diabcalc;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Σε αυτό το activity, εμφανίζεται το όνομα του φαγητού που έχει επιλέξει ο χρήστης ότι θα καταναλώσει.
 * Καθώς και ένα editText για να μπορέσει ο χρήστης να ορίσει τον αριθμό των γραμμαρίων που πρόκειται να
 * καταναλώσει. Τέλος, με τη χρήση του button ο χρήστης θα κατευθυνθεί στο τελευταίο activity.
 */
public class SecondActivity extends AppCompatActivity {

    EditText editText;
    Intent intent;
    ArrayList<Food> finalFoods;

    private AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        activity = this;

        intent = getIntent();
        finalFoods = intent.getParcelableArrayListExtra("list");
        Food food = intent.getParcelableExtra("info");

        autoCompleteTextView = findViewById(R.id.optionText);
        arrayList = new ArrayList<>();
        arrayList.add(getResources().getString(R.string.grammars));
        if(food.getFoodCarPerMeal()>0) {
            arrayList.add(getResources().getString(R.string.piece));
        }
        arrayAdapter = new ArrayAdapter<>(
                SecondActivity.this,
                android.R.layout.simple_list_item_1,
                arrayList
        );
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setThreshold(1);

        TextView info = findViewById(R.id.info);

        String info_line = "<b>" + getResources().getString(R.string.name) + ": </b> " +food.getFoodName() +
                "<br><b>" + getResources().getString(R.string.category) + ": </b> " + food.getFoodCategory();
        if(food.getFoodDescription() == null)
            info_line += "<br><b>" + getResources().getString(R.string.description) + ": </b> " + getResources().getString(R.string.noDescription);
        else
            info_line += "<br><b>" + getResources().getString(R.string.description) + ": </b> " + food.getFoodDescription();
        if(food.getFoodFatPerMeal()>0)
            info_line += "<br><b>" + getResources().getString(R.string.carbohydrates) + " " +getResources().getString(R.string.perMeal) + ": </b> " + food.getFoodCarPerMeal() +
                "<br><b>" + getResources().getString(R.string.fat) + " " +getResources().getString(R.string.perMeal) + ": </b> " + food.getFoodFatPerMeal();
        info.setText(Html.fromHtml(info_line));


        Button next = findViewById(R.id.next);
        editText = findViewById(R.id.grammars);
        next.setOnClickListener(view -> {
            if (!editText.getText().toString().isEmpty()) {
                Intent i = new Intent(SecondActivity.this, ThirdActivity.class);
                double grammars = Double.parseDouble(editText.getText().toString());
                if(autoCompleteTextView.getText().toString().equals(getResources().getString(R.string.piece)))
                    food.calculatePerMeal(grammars);
                else {
                    food.setFoodGrammar(grammars);
                    food.calculate();
                }
                if (finalFoods.contains(food)) {
                    finalFoods.remove(finalFoods.size() - 1);
                }
                finalFoods.add(food);
                i.putParcelableArrayListExtra("list", finalFoods);
                if(Objects.equals(getIntent().getStringExtra("activity"), "favorite"))
                    i.putExtra("activity",getIntent().getStringExtra("activity"));
                startActivity(i);
            } else
                Toast.makeText(SecondActivity.this, getResources().getText(R.string.inputNeeded), Toast.LENGTH_SHORT).show();
        });

        Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());
    }
}