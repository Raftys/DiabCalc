package com.example.diabcalc;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Διαχείριση Βάσης δεδομένων
 */
public class SqlHandler extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data";
    public static final String TABLE_FOOD = "Food";
    public static final String TABLE_MENU = "Menu";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MENU_NAME = "menu_name";
    public static final String COLUMN_NAME = "food_name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CAR = "car";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_GRAMMAR = "grammar";
    /*0 = Default
     * 1 = Edited*/
    public static final String COLUMN_EDITED = "edited";
    private final Context context;

    public SqlHandler(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
        DATABASE_VERSION = version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        onCreate(db);
    }

    /**
     * ΑDD
     * Στην βάση δεδομένων
     * @param food φαγητό
     */
    public void addFood(@NonNull Food food) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, food.getFoodId());
        values.put(COLUMN_NAME, food.getFoodName());
        values.put(COLUMN_CATEGORY,food.getFoodCategory());
        values.put(COLUMN_BRAND,food.getFoodBrand());
        values.put(COLUMN_DESCRIPTION,food.getFoodDescription());
        values.put(COLUMN_CAR, food.getFoodCar());
        values.put(COLUMN_FAT, food.getFoodFat());
        values.put(COLUMN_HOUR, food.getFoodHour());
        values.put(COLUMN_GRAMMAR, food.getFoodGrammar());
        values.put(COLUMN_EDITED,food.getFoodEdit());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FOOD, null, values);
        db.close();
    }

    /**
     * ΑDD
     * Στην βάση δεδομένων για τα αγαπημένα
     * @param food_name όνομα φαγητού
     * @param grammar γραμμάρια
     * @param menu_name όνομα μενού
     */
    public void addFood(String food_name, double grammar, String menu_name) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,food_name);
        values.put(COLUMN_GRAMMAR,grammar);
        values.put(COLUMN_MENU_NAME,menu_name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_MENU, null, values);
        db.close();
    }

    /**
     * Προσθήκη μενού στα αγαπημένα
     * @param foods φαγητά
     * @param menu_name όνομα μενού
     */
    public void addMenu(@NonNull ArrayList<Food> foods, String menu_name) {
        for(Food food : foods)
            addFood(food.getFoodName(), food.getFoodGrammar(), menu_name);
    }

    /**
     * @return όλα τα φαγητά που βρίσκονται στην βάση δεδομένων
     */
    public ArrayList<Food> getAll() {
        String query = "Select * FROM " + TABLE_FOOD;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Food> arrayList = new ArrayList<>();

        int size = cursor.getCount() - 1;
        cursor.moveToFirst();

        while (size >= 0) {
            arrayList.add(new Food(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    Double.parseDouble(cursor.getString(5)),
                    Double.parseDouble(cursor.getString(6)),
                    Double.parseDouble(cursor.getString(7)),
                    Double.parseDouble(cursor.getString(8)),
                    Integer.parseInt(cursor.getString(9))));
            cursor.moveToNext();
            size--;
        }
        cursor.close();
        return arrayList;
    }

    /**
     * @return όλα τα μενού που βρίσκονται στην βάση δεδομένων
     */
    public HashMap<String,ArrayList<Food>> getMenu() {
        String query = "Select * FROM " + TABLE_MENU + " ORDER BY " + COLUMN_MENU_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int size = cursor.getCount() - 1;
        cursor.moveToFirst();
        HashMap<String,ArrayList<Food>> temp = new HashMap<>();
        ArrayList<Food> arrayList = new ArrayList<>();
        try {
            String menu = cursor.getString(2);
            while (size >= 0) {
                if (menu.equals(cursor.getString(2))) {
                    Food f = findFood(cursor.getString(0));
                    f.setFoodGrammar(Double.parseDouble(cursor.getString(1)));
                    f.calculate();
                    arrayList.add(f);
                    cursor.moveToNext();
                    size--;
                } else {
                    temp.put(menu, arrayList);
                    arrayList = new ArrayList<>();
                    menu = cursor.getString(2);
                }
            }
            temp.put(menu, arrayList);
            cursor.close();
        } catch (Exception ignored) {}
        return temp ;
    }

    /**
     * @param column στήλη (κατηγορία ή μάρκα)
     * @return τα φαγητά που είναι στην στήλη που δίνεται
     */
    public ArrayList<String> getInfo(String column) {
        String query = "Select * FROM " + TABLE_FOOD;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int size = cursor.getCount() - 1;
        cursor.moveToFirst();
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            if(column.equals(COLUMN_CATEGORY))
                while (size >= 0) {
                    if (cursor.getString(2)!=null && !arrayList.contains(cursor.getString(2)))
                        arrayList.add(cursor.getString(2));
                cursor.moveToNext();
                size--;
            }
            else if(column.equals(COLUMN_BRAND))
                while (size >= 0) {
                    if (cursor.getString(3)!=null && !arrayList.contains(cursor.getString(3)))
                        arrayList.add(cursor.getString(3));
                    cursor.moveToNext();
                    size--;
                }
            cursor.close();
        } catch (Exception ignored) {}
        for (int i = 0; i < arrayList.size() - 1; i++) {
            for (int j = i + 1; j < arrayList.size(); j++) {
                if (MainPage.stripAccents(arrayList.get(i)).compareTo(MainPage.stripAccents(arrayList.get(j))) > 0) {
                    String line = arrayList.get(i);
                    arrayList.set(i, arrayList.get(j));
                    arrayList.set(j, line);
                }
            }
        }
        return arrayList ;

    }

    /**
     * Βρίσκει ένα φαγητό στην βάση δεδομένων
     * @param name όνομα φαγητού
     * @return επιστρέφει το φαγητό
     */
    public Food findFood(String name) {
        String query = "SELECT * FROM " + TABLE_FOOD + " WHERE " +
                COLUMN_NAME + " = '" + name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0)
            return new Food(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    Double.parseDouble(cursor.getString(5)),
                    Double.parseDouble(cursor.getString(6)),
                    Double.parseDouble(cursor.getString(7)),
                    Double.parseDouble(cursor.getString(8)),
                    Integer.parseInt(cursor.getString(9)));
        return null;
    }

    /**
     * Διαργαφή φαγητού
     * @param name όνομα φαγητού
     * @return true αν διαγράφηκε, false αν δεν υπάρχει
     */
    public boolean deleteFood(String name) {
        Food food = findFood(name);
        if (food != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_FOOD, COLUMN_NAME + " = ?",
                    new String[]{String.valueOf(food.getFoodName())});
            db.close();
            return true;
        }
        return false;

    }

    /**
     * Διαγραφή μενού
     * @param name όνομα μενού
     */
    public void deleteMenu(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MENU,COLUMN_MENU_NAME + " = ?", new String[]{name});
    }


    /**
     * Import DataBase
     */
    public void importDatabase() {
        this.getReadableDatabase();
        try {
            OutputStream myOutput = new FileOutputStream("data/data/com.example.diabcalc/databases/" + DATABASE_NAME);
            assert this.context != null;
            InputStream myInput = this.context.getAssets().open("temp.db");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myInput.close();
            myOutput.flush();
            myOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}