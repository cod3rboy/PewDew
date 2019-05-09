package com.cod3rboy.pewdew.gamestates;

import com.cod3rboy.pewdew.managers.GameStateManager;

public abstract class GameState {
    protected GameStateManager gsm;

    public GameState(GameStateManager gsm){
        this.gsm = gsm;
        init();
    }
    public abstract void init();
    public abstract void update(float dt);
    public abstract void draw();
    public abstract void handleInput();
    public abstract void dispose();
}
