package com.solonari.igor.foodscore;


public class Food {
    private String name;
    private int id, score, popularity;

    public Food (int id, String name, int score, int popularity) {
        this.name = name;
        this.id = id;
        this.score = score;
        this.popularity = popularity;
    }

    public int getID(){
        return id;
    }

    public int getScore(){
        return score;
    }

    public int getPopularity(){
        return popularity;
    }

    public String getName(){
        return name;
    }

}
