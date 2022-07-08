package com.example.diabcalc;


import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.os.Bundle;
import android.widget.*;
import java.util.ArrayList;

/**
 * Σε αυτό το activity, εμφανίζεται μία λίστα (foods) με όλα τα φαγητά τα οποία υπάρχουν στη βάση
 * δεδομένων. Επιλέγοντας ένα ο χρήστης προωθείται στο επόμενο activity.
 * Επίσης, υπάρχει η δυνατότητα να αναζητήσεις φαγητό.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * foods: μία λίστα με όλα τα φαγητά που βρίσκονται στη βάση δεδομένων
     * finalFoods: μία λίστα με τα φαγητά που επιλέγει ο χρήστης ότι θα καταναλώσει
     */
    ListView search_foods;
    ArrayAdapter<String> adapter;
    ArrayList<Food> foods;
    ArrayList<Food> finalFoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
            finalFoods = savedInstanceState.getParcelableArrayList("final");
        else if (finalFoods == null)
            finalFoods = new ArrayList<>();

        search_foods = findViewById(R.id.search_food);

        foods = getIntent().getParcelableArrayListExtra("foods");

        ArrayList<String> arrayList = new ArrayList<>();
        for (Food food : foods) {
            arrayList.add(food.getFoodName());
        }

        adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                arrayList
        );
        search_foods.setAdapter(adapter);


        search_foods.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            Food food = null;
            for (Food f : foods)
                if (arrayList.get(i).equals(f.getFoodName())) {
                    food = f;
                    break;
                }
            intent.putExtra("info", food);
            intent.putParcelableArrayListExtra("list", finalFoods);
            startActivityForResult(intent, 1);
        });

    }

    /**
     * @param resultCode = 1 -> προσθήκη φαγητού στο μενού
     *                   resultCode = 1 -> επιστροφή από Activity 2
     *                   resultCode = 0 -> νέα μενού
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 || resultCode == -1) {
            assert data != null;
            this.finalFoods = data.getParcelableArrayListExtra("list");
        } else
            finalFoods = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_food);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setSaveEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}