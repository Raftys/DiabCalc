package com.example.diabcalc;


import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;


/**
 * Σε αυτό το activity, εμφανίζεται το όνομα του φαγητού που έχει επιλέξει ο χρήστης ότι θα καταναλώσει.
 * Καθώς και ένα editText για να μπορέσει ο χρήστης να ορίσει τον αριθμό των γραμμαρίων που πρόκειται να
 * καταναλώσει. Τέλος, με τη χρήση του button ο χρήστης θα κατευθυνθεί στο τελευταίο activity.
 */
public class SecondActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    Intent intent;
    ArrayList<Food> finalFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        intent = getIntent();
        finalFoods = intent.getParcelableArrayListExtra("list");
        Food food = intent.getParcelableExtra("info");

        textView = findViewById(R.id.textView);
        textView.setText(food.getFoodName());


        Button button_next, button_back;

        button_next = findViewById(R.id.next);
        editText = findViewById(R.id.grammars);
        button_next.setOnClickListener(view -> {
            if (!editText.getText().toString().isEmpty()) {
                Intent i = new Intent(SecondActivity.this, ThirdActivity.class);
                double grammars = Double.parseDouble(editText.getText().toString());
                food.setFoodGrammar(grammars);
                food.calculate();
                if (finalFoods.contains(food)) {
                    finalFoods.remove(finalFoods.size() - 1);
                }
                finalFoods.add(food);
                i.putParcelableArrayListExtra("list", finalFoods);
                intent.putExtra("info", food);
                startActivityForResult(i, 1);
            } else
                Toast.makeText(SecondActivity.this, "Input Need", Toast.LENGTH_SHORT).show();
        });

        button_back = findViewById(R.id.back3);
        button_back.setOnClickListener(view -> {
            Intent i = new Intent();
            i.putParcelableArrayListExtra("list", finalFoods);
            setResult(-1, i);
            finish();
        });
    }

    /**
     * @param resultCode = 1 -> προσθήκη φαγητού στο μενού
     *                   resultCode = 0 -> νέα μενού
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent1 = new Intent();
        if (resultCode == 1) {
            assert data != null;
            ArrayList<Food> finalFoods = data.getParcelableArrayListExtra("list");
            intent1.putParcelableArrayListExtra("list", finalFoods);
            setResult(1, intent1);
            finish();
        } else if (resultCode == 0) {
            setResult(0, intent1);
            finish();
        }
    }


}