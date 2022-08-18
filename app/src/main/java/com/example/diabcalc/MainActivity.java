package com.example.diabcalc;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.widget.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    private static ArrayAdapter<String> adapter;
    private static ArrayList<Food> foods;
    ArrayList<Food> finalFoods;
    ArrayList<String> arrayList;
    String activity;

    ExpandableListView expandableListView;
    @SuppressLint("StaticFieldLeak")
    private static CustomExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    int bool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;


        /*-------------------------------------final Foods---------------------------------------*/
        try {
            finalFoods = getIntent().getParcelableArrayListExtra("list");
            activity = getIntent().getStringExtra("activity");
        } catch (Exception ignored) {}

        if (savedInstanceState != null)
            finalFoods = savedInstanceState.getParcelableArrayList("final");
        else if (finalFoods == null)
            finalFoods = new ArrayList<>();
        /*---------------------------------------------------------------------------------------*/

        if(getIntent().getIntExtra("favorite",0)==1) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("list",finalFoods);
            intent.putExtra("favorite",getIntent().getIntExtra("favorite",0));
            setResult(4,intent);
        }

        /*-------------------------------------Set ListView--------------------------------------*/
        search_foods = findViewById(R.id.search_food);
        foods = getIntent().getParcelableArrayListExtra("foods");
        arrayList = new ArrayList<>();
        for (Food food : foods)
            arrayList.add(food.getFoodName());

        adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                arrayList
        );
        search_foods.setAdapter(adapter);
        /*---------------------------------------------------------------------------------------*/

        search_foods.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent;
            Food food = null;
            for (Food f : foods)
                if (arrayList.get(i).equals(f.getFoodName())) {
                    food = f;
                    break;
                }
            if(Objects.equals(activity, "delete")) {
                intent = new Intent(this, AddActivity.class);
                intent.putExtra("info",food);
                intent.putExtra("delete",1);
                intent.putParcelableArrayListExtra("foods", foods);
            }
            else {
                intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("info", food);
                intent.putExtra("favorite",getIntent().getIntExtra("favorite",0));
                intent.putParcelableArrayListExtra("list", finalFoods);
            }
            intent.putExtra("food",food);
            startActivityForResult(intent, 1);
        });


        /*Φίλτρα**/
        SqlHandler sqlHandler = new SqlHandler(this,null,1);
        expandableListView = findViewById(R.id.expandedListView);
        expandableListDetail = getFilters(sqlHandler);
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this,expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        /*Reset Button**/
        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(view -> {
            expandableListAdapter.reset();
            setAdapter();
        });

    }

    /*Προσαρμόζει το listview αναλόγος με τα Φίλτρα**/
    public static void setAdapter() {
        if(expandableListAdapter != null) {
            HashMap<String, ArrayList<String>> temp = expandableListAdapter.getChecked();
            adapter.clear();
            if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.category))).isEmpty()) {
                if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.brand))).isEmpty())
                    for (Food food : foods)
                        adapter.add(food.getFoodName());
                else
                    for (Food food : foods)
                        if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.brand))).contains(food.getFoodBrand()))
                            adapter.add(food.getFoodName());
            } else {
                if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.brand))).isEmpty()) {
                    for (Food food : foods)
                        if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.category))).contains(food.getFoodCategory()))
                            adapter.add(food.getFoodName());
                } else
                    for (Food food : foods)
                        if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.category))).contains(food.getFoodCategory()) &&
                                Objects.requireNonNull(temp.get(context.getResources().getString(R.string.brand))).contains(food.getFoodBrand()))
                            adapter.add(food.getFoodName());
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * @param sqlHandler βάση δεδομένων
     * @return τις κατηγορίες και τις μάρκες που επιλέχτηκαν
     */
    public HashMap<String, List<String>> getFilters(SqlHandler sqlHandler) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<>();

        List<String> categories = sqlHandler.getInfo(SqlHandler.COLUMN_CATEGORY);

        List<String> brands = sqlHandler.getInfo(SqlHandler.COLUMN_BRAND);

        expandableListDetail.put(getResources().getString(R.string.category), categories);
        expandableListDetail.put(getResources().getString(R.string.brand), brands);
        return expandableListDetail;
    }

    /**
     * @param resultCode = 1 -> προσθήκη φαγητού στο μενού
     *                   resultCode = 1 -> επιστροφή από Activity 2
     *                   resultCode = 2 -> νέα μενού
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setAdapter();

        if (resultCode == 1 || resultCode == -1) {
            assert data != null;
            this.finalFoods = data.getParcelableArrayListExtra("list");
            if(getIntent().getIntExtra("favorite",0)==1) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("list",finalFoods);
                intent.putExtra("favorite",getIntent().getIntExtra("favorite",0));
                setResult(4,intent);
            }
        }
        else if(resultCode == 3) {
            setResult(3);
            finish();
        }
        else
            finalFoods = new ArrayList<>();
    }

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
        MenuItem filters = menu.findItem(R.id.filters);
        filters.setVisible(true);
        filters.setOnMenuItemClickListener(menuItem -> {
            DrawerLayout drawer = findViewById(R.id.drawer);
            if(drawer.isDrawerOpen(GravityCompat.END))
                drawer.closeDrawer(GravityCompat.END);
            else
                drawer.openDrawer(GravityCompat.END);
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }


}