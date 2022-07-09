package com.example.diabcalc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FavoriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        @SuppressWarnings("unchecked")
        HashMap<String,ArrayList<Food>> favorites = (HashMap<String, ArrayList<Food>>) getIntent().getSerializableExtra("favorites");
        System.out.println(Objects.requireNonNull(favorites.get("fav1")).get(0).getFoodName());

        List<HashMap<String,String>> list = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.list_item,
                new String[] {"line1","line2"},
                new int[]  {R.id.text1, R.id.text2});

        for(String fav : favorites.keySet()) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put("line1",fav);

            StringBuilder foods = new StringBuilder();
            for(int i = 0; i< Objects.requireNonNull(favorites.get(fav)).size(); i++)
                foods.append(i+1).append(".").append(Objects.requireNonNull(favorites.get(fav)).get(i).getFoodName()).append("\n");
            temp.put("line2", foods.toString());
            list.add(temp);
        }

        ListView listview = findViewById(R.id.favorites);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(FavoriteActivity.this, ThirdActivity.class);
            ArrayList<Food> finalFoods = favorites.get(list.get(i).get("line1"));
            intent.putParcelableArrayListExtra("list", finalFoods);
            startActivityForResult(intent, 1);

        });



    }
}