package com.example.diabcalc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Μια κλάση με 6 μεταβλητές, όπου αναφέρονται στις πληροφορίες του κάθε φαγητού. επίσης διαθέτει
 * την μέθοδο calculate, η οποία υπολογίζει τις τελικές μονάδες.
 */
public class Food implements Parcelable {
    private int id;
    private String name;
    private String category;
    private String brand;
    private String description;
    private double car;
    private double fat;
    private double car_per_meal;
    private double fat_per_meal;
    private double hour;
    private double grammar;
    private int edit;

    public Food() {
    }

    public Food(int id, String name,
                String category,
                String brand,
                String description,
                double car,
                double fat,
                double hour,
                double grammar,
                int edit,
                double car_per_meal,
                double fat_per_meal) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.brand = brand;
        this.car = car;
        this.fat = fat;
        this.car_per_meal=car_per_meal;
        this.fat_per_meal=fat_per_meal;
        this.hour = hour;
        this.grammar = grammar;
        this.edit = edit;
    }

    public void calculate() {
        this.car = (this.car * this.grammar) / 100;
        this.fat = (this.fat * this.grammar) / 100;
    }

    public void calculatePerMeal(double quantity) {
        this.car = this.car_per_meal*quantity;
        this.fat = this.fat_per_meal*quantity;
        grammar = this.car_per_meal*15*quantity;
    }

    protected Food(Parcel in) {
        id = in.readInt();
        name = in.readString();
        category = in.readString();
        brand = in.readString();
        description = in.readString();
        car = in.readDouble();
        fat = in.readDouble();
        car_per_meal = in.readDouble();
        fat_per_meal = in.readDouble();
        grammar = in.readDouble();
        hour = in.readDouble();
        edit = in.readInt();
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public int getFoodId() {
        return this.id;
    }

    public String getFoodName() {
        return this.name;
    }

    public String getFoodCategory() {
        return this.category;
    }

    public String getFoodBrand() {
        return this.brand;
    }

    public String getFoodDescription() {
        return this.description;
    }

    public double getFoodCar() {
        return this.car;
    }

    public double getFoodFat() {
        return this.fat;
    }

    public double getFoodCarPerMeal() {
        return this.car_per_meal;
    }

    public double getFoodFatPerMeal() {
        return this.fat_per_meal;
    }

    public double getFoodHour() {
        return this.hour;
    }

    public double getFoodGrammar() {
        return this.grammar;
    }

    public int getFoodEdit() {
        return this.edit;
    }

    public void setFoodName(String name) {
        this.name = name;
    }

    public void setFoodGrammar(double grammar) {
        this.grammar = grammar;
    }

    public void setFoodCategory(String category) {
        this.category = category;
    }

    public void setFoodBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(category);
        parcel.writeString(brand);
        parcel.writeString(description);
        parcel.writeDouble(car);
        parcel.writeDouble(fat);
        parcel.writeDouble(car_per_meal);
        parcel.writeDouble(fat_per_meal);
        parcel.writeDouble(grammar);
        parcel.writeDouble(hour);
        parcel.writeInt(edit);
    }
}
