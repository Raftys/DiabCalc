package com.example.diabcalc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Αυτό είναι το αρχίκο activity, το οποίο ανοίγει με το που ανοίξει ο χρήστης την εφαρμογή.
 * Από εδώ μπορεί να επιλέξει αν θέλει να προσθέσει ή να διαγράψει κάποιο φαγητό απο την Βάση
 * Δεδομένων. Καθώς και να ξεκινήσει τον υπολογισμό για το επόμενο γεύμα του.
 */
public class MainPage extends AppCompatActivity {

    /**
     * foods: όλα τα φαγητά από την βάση δεδομένων
     */
    static ArrayList<Food> foods;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    /**
     * Κουμπί πάνω αριστερά
     */
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @SuppressLint("UseSupportActionBar")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        context = this;

        getFoods(!getDatabasePath("data").exists());

        /*Εκκίνηση**/
        Button start = findViewById(R.id.start);
        start.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            intent.putParcelableArrayListExtra("foods", foods);
            startActivity(intent);
        });

        /*Προσθήκη Φαγητού**/
        Button add = findViewById(R.id.add);
        add.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, AddActivity.class);
            startActivity(intent);
        });

        /*Επεξεργασία Φαγητού**/
        Button edit = findViewById(R.id.edit);
        edit.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            intent.putParcelableArrayListExtra("foods", foods);
            intent.putExtra("activity","edit");
            startActivity(intent);
        });

        //Κουμπί πάνω αριστερά
        drawerLayout = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    public static void sort(ArrayList<Food> arrayList) {
        Arrays.sort(new ArrayList[]{arrayList});
        for (int i = 0; i < arrayList.size() - 1; i++) {
            for (int j = i + 1; j < arrayList.size(); j++) {
                if (arrayList.get(i).getFoodName().compareTo(arrayList.get(j).getFoodName()) > 0) {
                    Food f = arrayList.get(i);
                    arrayList.set(i, arrayList.get(j));
                    arrayList.set(j, f);
                }
            }
        }
    }

    /**
     * Αφαιρεί τόνους
     */
    public static String stripAccents(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    /**
     * Παίρνει όλα τα φαγητά από την βάση δεδομένων
     */
    public static void getFoods(Boolean bool) {
        SqlHandler sqlHandler = new SqlHandler(context, null, 1);
        if(bool)
            sqlHandler.importDatabase();
        foods = new ArrayList<>();
        foods = sqlHandler.getAll();
        sort(foods);
        sqlHandler.close();
    }


    public void start(MenuItem item) {
        minimize();
        Intent intent = new Intent(MainPage.this, MainActivity.class);
        intent.putParcelableArrayListExtra("foods", foods);
        startActivity(intent);
    }

    public void add(MenuItem item) {
        minimize();
        Intent intent = new Intent(MainPage.this, AddActivity.class);
        startActivity(intent);
    }

    public void edit(MenuItem item) {
        minimize();
        Intent intent = new Intent(MainPage.this, MainActivity.class);
        intent.putExtra("activity","edit");
        intent.putParcelableArrayListExtra("foods", foods);
        startActivity(intent);
    }

    public void favorites(MenuItem item) {
        minimize();
        Intent intent = new Intent(MainPage.this, FavoriteActivity.class);
        startActivity(intent);
    }

    public void minimize() {
        DrawerLayout drawer = findViewById(R.id.drawer);
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.setGroupVisible(0,false);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Κουμπί πάνω αριστερά
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(MainActivity.activity != null)
            MainActivity.activity.finish();

        if(SecondActivity.activity != null)
            SecondActivity.activity.finish();

        if(ThirdActivity.activity != null)
            ThirdActivity.activity.finish();

        if(FavoriteActivity.activity != null)
            FavoriteActivity.activity.finish();

        if(AddActivity.activity != null)
            AddActivity.activity.finish();
    }
}