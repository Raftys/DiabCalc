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
public class sqlHandler extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data";
    public static final String TABLE_FOOD = "Food";
    public static final String TABLE_MENU = "Menu";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MENU_NAME = "menu_name";
    public static final String COLUMN_NAME = "food_name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_CAR = "car";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_GRAMMAR = "grammar";
    /*0 = Default
     * 1 = Edited*/
    public static final String COLUMN_EDITED = "edited";
    private final Context context;

    public sqlHandler(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
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


    public void addFood(@NonNull Food food) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, food.getFoodId());
        values.put(COLUMN_NAME, food.getFoodName());
        values.put(COLUMN_CATEGORY,food.getFoodCategory());
        values.put(COLUMN_CAR, food.getFoodCar());
        values.put(COLUMN_FAT, food.getFoodFat());
        values.put(COLUMN_HOUR, food.getFoodHour());
        values.put(COLUMN_GRAMMAR, food.getFoodGrammar());
        values.put(COLUMN_EDITED,food.getFoodEdit());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FOOD, null, values);
        db.close();
    }

    public void addFood(String food_name, double grammar, String menu_name) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,food_name);
        values.put(COLUMN_GRAMMAR,grammar);
        values.put(COLUMN_MENU_NAME,menu_name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_MENU, null, values);
        db.close();
    }

    public void addMenu(@NonNull ArrayList<Food> foods, String menu_name) {
        for(Food food : foods)
            addFood(food.getFoodName(), food.getFoodGrammar(), menu_name);
    }

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
                    Double.parseDouble(cursor.getString(3)),
                    Double.parseDouble(cursor.getString(4)),
                    Double.parseDouble(cursor.getString(5)),
                    Double.parseDouble(cursor.getString(6)),
                    Integer.parseInt(cursor.getString(7)));
        return null;
    }

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

    public void deleteMenu(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MENU,COLUMN_MENU_NAME + " = ?", new String[]{name});
    }

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
                    Double.parseDouble(cursor.getString(3)),
                    Double.parseDouble(cursor.getString(4)),
                    Double.parseDouble(cursor.getString(5)),
                    Double.parseDouble(cursor.getString(6)),
                    Integer.parseInt(cursor.getString(7))));
            cursor.moveToNext();
            size--;
        }
        cursor.close();
        return arrayList;
    }

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
            System.out.println(menu);
            cursor.close();
        } catch (Exception ignored) {}
        return temp ;
    }

    @SuppressLint("Recycle")
    public int getSize(String table_name) {
        String query = "Select * FROM " + table_name;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null).getCount();
    }

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