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
    private static final String DATABASE_NAME = "data.db";
    public static final String TABLE_FOOD = "Food";
    public static final String TABLE_MENU = "Menu";
    public static final String TABLE_FOODS = "Foods";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MENU_NAME = "Menu_name";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CAR = "car";
    public static final String COLUMN_FAT = "fat";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_GRAMMAR = "grammar";
    private final Context context;

    public sqlHandler(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
        DATABASE_VERSION = version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FOOD_TABLE = "CREATE TABLE " +
                TABLE_FOOD + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT UNIQUE," +
                COLUMN_CAR + " DECIMAL," +
                COLUMN_FAT + " DECIMAL," +
                COLUMN_HOUR + " DECIMAL," +
                COLUMN_GRAMMAR + " DECIMAL" + ")";

        String CREATE_MENU_TABLE = "CREATE TABLE " +
                TABLE_MENU + "(" +
                COLUMN_ID +" INTEGER NOT NULL ," +
                COLUMN_NAME + " TEXT," +
                "PRIMARY KEY(id))";

        String CREATE_FOODS_TABLE = "CREATE TABLE " +
                TABLE_FOODS  + "(" +
                COLUMN_ID + " INTEGER," +
                COLUMN_NAME + " TEXT," +
                COLUMN_CAR + " DECIMAL," +
                COLUMN_FAT + " DECIMAL," +
                COLUMN_HOUR + " DECIMAL," +
                COLUMN_GRAMMAR + " DECIMAL," +
                COLUMN_MENU_NAME + " TEXT" + ")";

        db.execSQL(CREATE_FOOD_TABLE);
        db.execSQL(CREATE_MENU_TABLE);
        db.execSQL(CREATE_FOODS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        onCreate(db);
    }

    public void addFood(@NonNull Food food) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, food.getFoodId());
        values.put(COLUMN_NAME, food.getFoodName());
        values.put(COLUMN_CAR, food.getFoodCar());
        values.put(COLUMN_FAT, food.getFoodFat());
        values.put(COLUMN_HOUR, food.getFoodHour());
        values.put(COLUMN_GRAMMAR, food.getFoodGrammar());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_FOOD, null, values);
        db.close();
    }

    public void addFood(@NonNull Food food, String menu_name) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, food.getFoodId());
        values.put(COLUMN_NAME, food.getFoodName());
        values.put(COLUMN_CAR, food.getFoodCar());
        values.put(COLUMN_FAT, food.getFoodFat());
        values.put(COLUMN_HOUR, food.getFoodHour());
        values.put(COLUMN_GRAMMAR, food.getFoodGrammar());
        values.put(COLUMN_MENU_NAME,menu_name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("Foods", null, values);
        db.close();
    }

    public void addMenu(String name, @NonNull ArrayList<Food> foods) {
        ContentValues values = new ContentValues();
        int pos = getSize("Menu");
        values.put("id",pos);
        values.put("name",name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("Menu",null,values);
        for(Food f : foods){
            addFood(f,name);
        }
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

    public HashMap<String,ArrayList<Food>> getMenu() {
        String query = "Select * FROM " + TABLE_FOODS + " ORDER BY " + COLUMN_MENU_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int size = cursor.getCount() - 1;
        cursor.moveToFirst();
        HashMap<String,ArrayList<Food>> temp = new HashMap<>();
        ArrayList<Food> arrayList = new ArrayList<>();

        String menu = cursor.getString(6);
        while (size >= 0) {
            if(menu.equals(cursor.getString(6))) {
                arrayList.add(new Food(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Double.parseDouble(cursor.getString(2)),
                        Double.parseDouble(cursor.getString(3)),
                        Double.parseDouble(cursor.getString(4)),
                        Double.parseDouble(cursor.getString(5))));
                cursor.moveToNext();
                size --;
            }
            else {
                temp.put(menu,arrayList);
                arrayList = new ArrayList<>();
                menu = cursor.getString(6);
            }
        }
        temp.put(menu,arrayList);
        cursor.close();
        return temp ;
    }

    @SuppressLint("Recycle")
    public int getSize(String table_name) {
        String query = "Select * FROM " + table_name;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(query, null).getCount();
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

        try {
            InputStream inputStream = this.context.getAssets().open("favorites.txt");
            BufferedReader insertReader = new BufferedReader(new InputStreamReader(inputStream));
            int pos = 0;
            String menu_name = "";
            ArrayList<Food> foods = new ArrayList<>();
            while (insertReader.ready()) {
                if(pos==0) {
                    menu_name = insertReader.readLine();
                    foods = new ArrayList<>();
                    pos = 1;
                }
                else {
                    String temp = insertReader.readLine();
                    if(temp.equals("------")) {
                        pos = 2;
                    }
                    else {
                        String[] line = temp.split("/");
                        foods.add(new Food(Integer.parseInt(line[0]), line[1], Double.parseDouble(line[2]), Double.parseDouble(line[3]), Double.parseDouble(line[4]), Double.parseDouble(line[5])));
                    }
                }
                if(pos==2) {
                    addMenu(menu_name,foods);
                    pos = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
