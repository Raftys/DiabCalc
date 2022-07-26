package com.example.diabcalc;

import androidx.annotation.Nullable;
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

    ListView listview;
    HashMap<String,ArrayList<Food>> favorites;
    sqlHandler sqlHandler;
    List<HashMap<String,String>> list;


    void setList() {
        favorites = sqlHandler.getMenu();
        list = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, list,
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        sqlHandler = new sqlHandler(this, null, 1);
        setList();

        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(FavoriteActivity.this, ThirdActivity.class);
            ArrayList<Food> finalFoods = favorites.get(list.get(i).get("line1"));
            intent.putParcelableArrayListExtra("list", finalFoods);
            intent.putExtra("favorite",1);
            intent.putExtra("menu_name",list.get(i).get("line1"));

            startActivityForResult(intent, 1);
        });
    }


    /*
    1 -> add
    2 -> new
    else -> back
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();
        if (resultCode == 1) {
            assert data != null;
            ArrayList<Food> finalFoods = data.getParcelableArrayListExtra("list");
            intent.putParcelableArrayListExtra("list", finalFoods);
            setResult(1,intent);
            finish();
        }
        else if(resultCode == 2) {
            setResult(2,intent);
            finish();
        }
        else {
            favorites = new HashMap<>();
            setList();
        }
    }
}