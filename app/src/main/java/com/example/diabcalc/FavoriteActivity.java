package com.example.diabcalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FavoriteActivity extends AppCompatActivity {

    /**
     * listview: ListView, για την εμφάνιση των μενού
     * adapter: για να μπαίνουν τα μενού στο listview
     * favorites: λίστα με ονόματα όλων των μενού και όλων των φαγητών που βρίσκονται σε αυτά
     * finalFoods: μία λίστα με τα φαγητά που επιλέγει ο χρήστης ότι θα καταναλώσει
     */
    ListView listview;
    SimpleAdapter adapter;
    HashMap<String,ArrayList<Food>> favorites;
    List<HashMap<String,String>> list;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        activity = this;

        setList();

        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(FavoriteActivity.this, ThirdActivity.class);
            ArrayList<Food> finalFoods = favorites.get(list.get(i).get("line1"));
            intent.putParcelableArrayListExtra("list", finalFoods);
            intent.putExtra("activity","favorite");
            intent.putExtra("menu_name",list.get(i).get("line1"));
            startActivity(intent);
        });
    }

    /**
     * Ορίζει την λίστα με τα αγαπημένα
     */
    void setList() {
        SqlHandler sqlHandler = new SqlHandler(this, null, 1);
        favorites = sqlHandler.getMenu();
        sqlHandler.close();
        list = new ArrayList<>();
        adapter = new SimpleAdapter(this, list,
                R.layout.list_item,
                new String[] {"line1","line2"},
                new int[]  {R.id.text1, R.id.text2});

        for(String fav : favorites.keySet()) {
            HashMap<String,String> temp = new HashMap<>();
            temp.put("line1",fav);
            StringBuilder text2 = new StringBuilder();
            for(int i = 0; i< Objects.requireNonNull(favorites.get(fav)).size(); i++)
                text2.append(i + 1).append(".").append(Objects.requireNonNull(favorites.get(fav)).get(i).getFoodName()).
                        append(" (").append(Objects.requireNonNull(favorites.get(fav)).get(i).getFoodGrammar()).
                        append("g)\n");
            temp.put("line2", text2.toString());
            list.add(temp);
        }

        listview = findViewById(R.id.favorites);
        listview.setAdapter(adapter);
    }

    /**
     * αναζήτηση
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
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