package com.cod3rboy.pewdew.managers;

import java.io.Serializable;

public class GameData implements Serializable {
    private static final long serialVersionUID = 1;
    private final int MAX_SCORES = 10;
    private long[] highScores;
    private String[] names;
    private int[] levels;

    private long tentativeScore;
    private int level;

    public GameData(){
        highScores = new long[MAX_SCORES];
        names = new String[MAX_SCORES];
        levels = new int[MAX_SCORES];
    }

    // Sets up an empty high scores table
    public void init(){
        for(int i=0; i<MAX_SCORES; i++){
            highScores[i] = 0;
            names[i] = "------";
            levels[i] = 0;
        }
    }

    public long[] getHighScores() { return highScores; }
    public String[] getNames() { return names; }
    public int[] getLevels() { return levels; }
    public long getTentativeScore() { return tentativeScore; }
    public void  setTentativeScore(long i){ tentativeScore = i; }

    public void setLevel(int level){ this.level = level; }
    public int getLevel(){ return this.level; }

    public boolean isHighScore(long score){
        return score > highScores[0];
    }

    public void addHighScore(long newScore, String name, int level){
        if(isHighScore(newScore)){
            highScores[MAX_SCORES - 1] = newScore;
            names[MAX_SCORES - 1] = name;
            levels[MAX_SCORES - 1] = level;
            sortHighScores();
        }
    }
    public void sortHighScores(){
        for(int i=0; i<MAX_SCORES; i++){
            long score = highScores[i];
            String name = names[i];
            int level = levels[i];
            int j;
            for(j=i-1;
                j>=0 && highScores[j] < score;
                j-- ) {
                highScores[j+1] = highScores[j];
                names[j+1] = names[j];
                levels[j+1] = levels[j];
            }
            highScores[j+1] = score;
            names[j+1] = name;
            levels[j+1] = level;
        }
    }
}
