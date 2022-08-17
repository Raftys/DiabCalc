package com.example.diabcalc;


/**
 * Για τα checkBoxes
 */
public class Scroll {

    String name;
    boolean check;

    public Scroll(String name) {
        this.name=name;
        this.check= false;
    }

    public String getName() {
        return this.name;
    }

    public boolean getCheck() {
        return this.check;
    }

    public void setCheck(boolean bool) {
        this.check = bool;
    }

}
