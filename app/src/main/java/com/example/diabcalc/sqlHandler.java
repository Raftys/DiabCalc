package com.example.diabcalc;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.*;
import java.util.ArrayList;

/**
 * Διαχείριση Βάσης δεδομένων
 */
public class sqlHandler extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.db";
    public static final String TABLE_FOOD = "food";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CAR = "car";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_HOUR = "hour";
    private final Context context;

    public sqlHandler(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
        DATABASE_VERSION = version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_FOOD + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT UNIQUE," +
                COLUMN_CAR + " DECIMAL," +
                COLUMN_FAT + " DECIMAL," +
                COLUMN_HOUR + " DECIMAL" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        onCreate(db);
    }

    public void addFood(Food food) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, food.getFoodId());
        values.put(COLUMN_NAME, food.getFoodName());
        values.put(COLUMN_CAR, food.getFoodCar());
        values.put(COLUMN_FAT, food.getFoodFat());
        values.put(COLUMN_HOUR, food.getFoodHour());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FOOD, null, values);
        db.close();
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
                    Double.parseDouble(cursor.getString(2)),
                    Double.parseDouble(cursor.getString(3)),
                    Double.parseDouble(cursor.getString(4)));
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
                    Double.parseDouble(cursor.getString(2)),
                    Double.parseDouble(cursor.getString(3)),
                    Double.parseDouble(cursor.getString(4))));
            cursor.moveToNext();
            size--;
        }
        cursor.close();
        return arrayList;
    }


    /**
     * διαβάζει από ένα αρχείο τιμές για κάποια φαγητά, ώστε να προσθεθούν στη λίστα
     * το αρχείο είναι της μορφής, name/car/fat/hour, για ευνόητους λόγους.
     */
    public void insertFromFile() {
        try {
            assert this.context != null;
            InputStream inputStream = this.context.getAssets().open("food.txt");
            BufferedReader insertReader = new BufferedReader(new InputStreamReader(inputStream));
            int k = 0;
            while (insertReader.ready()) {
                String[] line = insertReader.readLine().split("/");
                addFood(new Food(k, line[0], Double.parseDouble(line[1]), Double.parseDouble(line[2]), Double.parseDouble(line[3])));
                k++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int size() {
        String query = "Select COUNT(*) FROM " + TABLE_FOOD;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
