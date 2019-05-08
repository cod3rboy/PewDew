package com.cod3rboy.asteroids.managers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class GameInputProcessor extends InputAdapter {
    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.UP){
            GameKeys.setKey(GameKeys.UP, true);
        }
        if(keycode == Input.Keys.LEFT){
            GameKeys.setKey(GameKeys.LEFT, true);
        }
        if(keycode == Input.Keys.DOWN){
            GameKeys.setKey(GameKeys.DOWN, true);
        }
        if(keycode == Input.Keys.RIGHT){
            GameKeys.setKey(GameKeys.RIGHT, true);
        }
        if(keycode == Input.Keys.ENTER){
            GameKeys.setKey(GameKeys.ENTER, true);
        }
        if(keycode == Input.Keys.ESCAPE){
            GameKeys.setKey(GameKeys.ESCAPE, true);
        }
        if(keycode == Input.Keys.SPACE){
            GameKeys.setKey(GameKeys.SPACE, true);
        }
        if(keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT){
            GameKeys.setKey(GameKeys.SHIFT, true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.UP){
            GameKeys.setKey(GameKeys.UP, false);
        }
        if(keycode == Input.Keys.LEFT){
            GameKeys.setKey(GameKeys.LEFT, false);
        }
        if(keycode == Input.Keys.DOWN){
            GameKeys.setKey(GameKeys.DOWN, false);
        }
        if(keycode == Input.Keys.RIGHT){
            GameKeys.setKey(GameKeys.RIGHT, false);
        }
        if(keycode == Input.Keys.ENTER){
            GameKeys.setKey(GameKeys.ENTER, false);
        }
        if(keycode == Input.Keys.ESCAPE){
            GameKeys.setKey(GameKeys.ESCAPE, false);
        }
        if(keycode == Input.Keys.SPACE){
            GameKeys.setKey(GameKeys.SPACE, false);
        }
        if(keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT){
            GameKeys.setKey(GameKeys.SHIFT, false);
        }
        return true;
    }
}
