package com.example.diabcalc;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
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
     * search_foods: ListView, για την εμφάνιση των φαγητών
     * adapter: για να μπαίνουν τα φαγητά στο listview
     * arrayList: λίστα με ονόματα όλων των φαγητών
     * finalFoods: μία λίστα με τα φαγητά που επιλέγει ο χρήστης ότι θα καταναλώσει
     */
    ListView search_foods;
    private static ArrayAdapter<String> adapter;
    ArrayList<Food> finalFoods;
    ArrayList<String> arrayList;

    /**
     * Φιλτράρισμα
     */
    ExpandableListView expandableListView;
    @SuppressLint("StaticFieldLeak")
    private static CustomExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;
    ArrayList<MenuItem> items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        activity = this;
        items = new ArrayList<>();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /*-------------------------------------final Foods---------------------------------------*/
        try {
            finalFoods = getIntent().getParcelableArrayListExtra("list");
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
        MainPage.getFoods(!getDatabasePath("data").exists());
        arrayList = new ArrayList<>();
        for (Food food : MainPage.foods)
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
            Food food = new Food();
            for (Food f : MainPage.foods)
                if (adapter.getItem(i).equals(f.getFoodName())) {
                    food = f;
                    break;
                }
            if(Objects.equals(getIntent().getStringExtra("activity"), "edit")) {
                intent = new Intent(this, AddActivity.class);
                intent.putExtra("activity","edit");
            }
            else {
                intent = new Intent(MainActivity.this, SecondActivity.class);
                if(Objects.equals(getIntent().getStringExtra("activity"), "favorite"))
                    intent.putExtra("activity",getIntent().getStringExtra("activity"));
                intent.putParcelableArrayListExtra("list", finalFoods);
            }
            intent.putExtra("info",food);
            startActivity(intent);
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

    /**
     * Set ListView
     */
    @Override
    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
        super.onTopResumedActivityChanged(isTopResumedActivity);
        MainPage.getFoods(!getDatabasePath("data").exists());
        adapter.clear();
        for (Food food : MainPage.foods)
            adapter.add(food.getFoodName());
        adapter.notifyDataSetChanged();
    }

    /*Προσαρμόζει το listview αναλόγος με τα Φίλτρα**/
    public static void setAdapter() {
        if(expandableListAdapter != null) {
            HashMap<String, ArrayList<String>> temp = expandableListAdapter.getChecked();
            adapter.clear();
            if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.category))).isEmpty()) {
                if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.brand))).isEmpty())
                    for (Food food : MainPage.foods)
                        adapter.add(food.getFoodName());
                else
                    for (Food food : MainPage.foods)
                        if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.brand))).contains(food.getFoodBrand()))
                            adapter.add(food.getFoodName());
            } else {
                if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.brand))).isEmpty()) {
                    for (Food food : MainPage.foods)
                        if (Objects.requireNonNull(temp.get(context.getResources().getString(R.string.category))).contains(food.getFoodCategory()))
                            adapter.add(food.getFoodName());
                } else
                    for (Food food : MainPage.foods)
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(!items.contains(item)) {
            Intent intent = new Intent(MainActivity.this, MainPage.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Μενου, για αναζήτηση και φιλτράρισμα
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        items.add(menu.getItem(0));
        SearchView searchView = (SearchView) items.get(0).getActionView();
        searchView.setSaveEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        items.add(menu.getItem(1));
        items.get(1).setVisible(true);
        items.get(1).setOnMenuItemClickListener(menuItem -> {
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