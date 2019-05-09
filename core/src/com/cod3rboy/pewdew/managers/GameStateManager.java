package com.cod3rboy.pewdew.managers;

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
}
