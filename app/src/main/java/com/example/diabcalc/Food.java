package com.example.diabcalc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Μια κλάση με 6 μεταβλητές, όπου αναφέρονται στις πληροφορίες του κάθε φαγητού. επίσης διαθέτει
 * την μέθοδο calculate, η οποία υπολογίζει τις τελικές μονάδες.
 */
public class Food implements Parcelable {
    private final int id;
    private final String name;
    private double car;
    private double fat;
    private final double hour;
    private double grammar;

    public Food(int id, String name, double car, double fat, double hour) {
        this.id = id;
        this.name = name;
        this.car = car;
        this.fat = fat;
        this.hour = hour;
        this.grammar = 1;
    }

    public void calculate() {
        this.car = (this.car * this.grammar) / 100;
        this.fat = (this.fat * this.grammar) / 100;
    }

    protected Food(Parcel in) {
        id = in.readInt();
        name = in.readString();
        car = in.readDouble();
        fat = in.readDouble();
        grammar = in.readDouble();
        hour = in.readDouble();
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

    public double getFoodCar() {
        return this.car;
    }

    public double getFoodFat() {
        return this.fat;
    }

    public double getFoodHour() {
        return this.hour;
    }

    public void setFoodGrammar(double grammar) {
        this.grammar = grammar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(car);
        parcel.writeDouble(fat);
        parcel.writeDouble(grammar);
        parcel.writeDouble(hour);
    }
}
