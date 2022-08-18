package com.example.diabcalc;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    ArrayList<Food> foods;


    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        getFoods();

        Button start = findViewById(R.id.start);
        Button add = findViewById(R.id.add);
        Button delete = findViewById(R.id.edit);

        start.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            sort(foods);
            intent.putParcelableArrayListExtra("foods", foods);
            intent.putExtra("activity","add");
            startActivityForResult(intent, 1);
        });

        add.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, AddActivity.class);
            startActivityForResult(intent, 1);
        });

        delete.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            sort(foods);
            intent.putParcelableArrayListExtra("foods", foods);
            intent.putExtra("activity","delete");
            startActivityForResult(intent, 1);
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

    public static String stripAccents(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    /**
     * Παίρνει όλα τα φαγητά από την βάση δεδομένων
     */
    private void getFoods() {
        SqlHandler sqlHandler = new SqlHandler(this, null, 1);
        if(!getDatabasePath("data").exists())
            sqlHandler.importDatabase();
        foods = new ArrayList<>();
        foods = sqlHandler.getAll();
        sqlHandler.close();
    }


    public void start(MenuItem item) {
        minimize();
        Intent intent = new Intent(MainPage.this, MainActivity.class);
        sort(foods);
        intent.putExtra("activity","add");
        intent.putParcelableArrayListExtra("foods", foods);
        startActivityForResult(intent, 1);
    }

    public void add(MenuItem item) {
        minimize();
        Intent intent = new Intent(MainPage.this, AddActivity.class);
        startActivityForResult(intent, 1);
    }

    public void delete(MenuItem item) {
        minimize();
        Intent intent = new Intent(MainPage.this, MainActivity.class);
        sort(foods);
        intent.putExtra("activity","delete");
        intent.putParcelableArrayListExtra("foods", foods);
        startActivityForResult(intent, 1);
    }

    public void favorites(MenuItem item) {
        minimize();
        Intent intent = new Intent(MainPage.this, FavoriteActivity.class);
        startActivityForResult(intent, 1);
    }

    public void minimize() {
        DrawerLayout drawer = findViewById(R.id.drawer);
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getFoods();
        //Διαγραφή
        if (resultCode == -1) {
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            assert data != null;
            if(data.getIntExtra("favorite",0)==1) {
                intent = new Intent(MainPage.this, FavoriteActivity.class);
            }
            intent.putParcelableArrayListExtra("foods", foods);
            intent.putExtra("activity","delete");
            startActivityForResult(intent, 1);
        }
        //Εκκίνηση
        else if(resultCode == 1) {
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            sort(foods);
            assert data != null;
            ArrayList<Food> finalFoods = data.getParcelableArrayListExtra("list");
            intent.putParcelableArrayListExtra("foods", foods);
            intent.putParcelableArrayListExtra("list", finalFoods);
            intent.putExtra("activity","add");
            intent.putExtra("favorite",data.getIntExtra("favorite",0));
            startActivityForResult(intent, 1);

        }
        //Προσθήκη Φαγητού
        else if(resultCode == 2) {
            assert data != null;
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            sort(foods);
            intent.putExtra("activity","delete");
            intent.putParcelableArrayListExtra("foods", foods);
            startActivityForResult(intent, 1);
        }
        else if(resultCode == 3) {
            Intent intent = new Intent(MainPage.this, FavoriteActivity.class);
            startActivityForResult(intent, 1);
        }
        else if(resultCode == 4) {
            assert data != null;
            Intent intent = new Intent(MainPage.this, ThirdActivity.class);
            intent.putExtra("favorite",data.getIntExtra("favorite",0));
            intent.putParcelableArrayListExtra("list",data.getParcelableArrayListExtra("list"));
            startActivityForResult(intent, 1);
        }
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
}