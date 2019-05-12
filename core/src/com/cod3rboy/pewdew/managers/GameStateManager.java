package com.cod3rboy.pewdew.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.cod3rboy.pewdew.gamestates.GameOverState;
import com.cod3rboy.pewdew.gamestates.GameState;
import com.cod3rboy.pewdew.gamestates.HighScoreState;
import com.cod3rboy.pewdew.gamestates.MenuState;
import com.cod3rboy.pewdew.gamestates.PlayState;

public class GameStateManager {
    // Current game state;
    private GameState gameState;

    public static final int MENU = 0;
    public static final int PLAY = 53253;
    public static final int HIGHSCORE = 353;
    public static final int GAMEOVER = 25352;

    private static boolean musicSetting; // True = Music ON and False = Music OFF
    private static final String PREFERENCES = "settings";
    private static final String KEY_MUSIC = "music_status";
    static {
        loadMusicSetting();
    }

    public GameStateManager(){
        setState(MENU);
    }

    public void setState(int state){
        if(gameState != null) gameState.dispose();
        if(state == MENU){
            // Switch to menu state
            gameState = new MenuState(this);
        }
        if(state == PLAY){
            // Switch to play state
            gameState = new PlayState(this);
        }
        if(state == HIGHSCORE){
            gameState = new HighScoreState(this);
        }
        if(state == GAMEOVER) {
            gameState = new GameOverState(this);
        }
    }

    public void update(float dt){
        gameState.update(dt);
    }
    public void draw() {
        gameState.draw();
    }

    public static boolean getMusicSetting() { return musicSetting; }
    public static void setMusicSetting(boolean setting){ musicSetting = setting; saveMusicSetting(); }


    private static void saveMusicSetting(){
        // Save Music Setting to Preferences
        Preferences prefs = Gdx.app.getPreferences(PREFERENCES);
        prefs.putBoolean(KEY_MUSIC, musicSetting);
        prefs.flush();
    }
    private static void loadMusicSetting(){
        // Load Music Setting from Preferences
        musicSetting = Gdx.app.getPreferences(PREFERENCES).getBoolean(KEY_MUSIC, true);
    }
}
