package com.solonari.igor.foodscore;


import android.support.annotation.NonNull;

public class Food implements Comparable<Food> {
    private final int id, score;
    private final String name;

    Food (int id, @NonNull String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public int compareTo(@NonNull Food other) {
        return name.compareTo(other.name);
    }

    int getID(){
        return id;
    }

    int getScore(){
        return score;
    }

    String getName() {
        return name;
    }

}
