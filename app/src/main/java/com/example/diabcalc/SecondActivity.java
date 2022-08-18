package com.example.diabcalc;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
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

    EditText editText;
    Intent intent;
    ArrayList<Food> finalFoods;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        intent = getIntent();
        finalFoods = intent.getParcelableArrayListExtra("list");
        Food food = intent.getParcelableExtra("info");

        TextView info = findViewById(R.id.info);

        String info_line;
        if(food.getFoodDescription() == null)
            info_line = "<b>" + getResources().getString(R.string.name) + ": </b> " +food.getFoodName() +
                    "<br><b>" + getResources().getString(R.string.category) + ": </b> " + food.getFoodCategory() +
                    "<br><b>" + getResources().getString(R.string.description) + ": </b> " + getResources().getString(R.string.noDescription);
        else
            info_line = "<b>" + getResources().getString(R.string.name) + ": </b> " + food.getFoodName() +
                    "<br><b>" + getResources().getString(R.string.category) + ": </b> " + food.getFoodCategory() +
                    "<br><b>" + getResources().getString(R.string.description) + ": </b> " + food.getFoodDescription();
        info.setText(Html.fromHtml(info_line));


        Button next = findViewById(R.id.next);
        editText = findViewById(R.id.grammars);
        next.setOnClickListener(view -> {
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
                i.putExtra("favorite",getIntent().getIntExtra("favorite",0));
                intent.putExtra("info", food);
                startActivityForResult(i, 1);
            } else
                Toast.makeText(SecondActivity.this, getResources().getText(R.string.inputNeeded), Toast.LENGTH_SHORT).show();
        });

        Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent i = new Intent();
            i.putParcelableArrayListExtra("list", finalFoods);
            i.putExtra("favorite",getIntent().getIntExtra("favorite",0));
            setResult(-1, i);
            finish();
        });
    }

    /**
     * @param resultCode = 1 -> προσθήκη φαγητού στο μενού
     *                   resultCode = 2 -> νέα μενού
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent1 = new Intent();
        if (resultCode == 1) {
            assert data != null;
            ArrayList<Food> finalFoods = data.getParcelableArrayListExtra("list");
            intent1.putParcelableArrayListExtra("list", finalFoods);
            intent1.putExtra("favorite",intent.getIntExtra("favorite",0));
            setResult(1, intent1);
            finish();
        } else if (resultCode == 2) {
            setResult(2, intent1);
            finish();
        }
        else if (resultCode == 3) {
            setResult(3);
            finish();
        }
    }


}