package com.cod3rboy.asteroids.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cod3rboy.asteroids.Asteroids;
import com.cod3rboy.asteroids.managers.GameKeys;
import com.cod3rboy.asteroids.managers.GameStateManager;
import com.cod3rboy.asteroids.managers.Save;

public class GameOverState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;
    private boolean newHighScore;
    private char[] newName;
    private int currentChar;

    public GameOverState(GameStateManager gsm) {
        super(gsm);
    }

    private BitmapFont gameOverFont;
    private BitmapFont font;

    @Override
    public void init() {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();
        newHighScore = Save.gd.isHighScore(Save.gd.getTentativeScore());
        if (newHighScore) {
            newName = new char[]{'A', 'A', 'A'};
            currentChar = 0;
        }

        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        param.size = 32;
        gameOverFont = gen.generateFont(param);
        param.size = 20;
        font = gen.generateFont(param);
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(Asteroids.cam.combined);
        sr.setProjectionMatrix(Asteroids.cam.combined);
        sb.begin();
        String s;
        float w;
        s = "Game Over";
        //w = gameOverFont.getBounds(s).width;
        w = 150;
        gameOverFont.setColor(1,0,0,1);
        gameOverFont.draw(sb, s, (Asteroids.WIDTH - w) / 2, 220);
        if (!newHighScore) {
            sb.end();
            return;
        }
        s = "New High Score : " + Save.gd.getTentativeScore();
        //w = font.getBounds(s).width;
        w = 230;
        font.draw(sb, s, (Asteroids.WIDTH - w) / 2, 180);
        for (int i = 0; i < newName.length; i++) {
            font.draw(sb, Character.toString(newName[i]),
                    230 + 14 * i, 100);
        }
        sb.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(
                230 + 14 * currentChar,
                80,
                244 + 14 * currentChar,
                80
        );
        sr.end();
    }

    @Override
    public void handleInput() {
        if(GameKeys.isPressed(GameKeys.ENTER)){
            if(newHighScore){
                Save.gd.addHighScore(Save.gd.getTentativeScore(), new String(newName));
                Save.save();
            }
            gsm.setState(GameStateManager.MENU);
        }
        if(GameKeys.isPressed(GameKeys.UP)){
            if(newName[currentChar] == ' '){
                newName[currentChar] = 'Z';
            }else{
                newName[currentChar]--;
                if(newName[currentChar] < 'A'){
                    newName[currentChar] = ' ';
                }
            }
        }
        if(GameKeys.isPressed(GameKeys.DOWN)){
            if(newName[currentChar] == ' '){
                newName[currentChar] = 'A';
            }else{
                newName[currentChar]++;
                if(newName[currentChar] > 'Z'){
                    newName[currentChar] = ' ';
                }
            }
        }
        if(GameKeys.isPressed(GameKeys.RIGHT)){
            if(currentChar < newName.length - 1){
                currentChar++;
            }
        }
        if(GameKeys.isPressed(GameKeys.LEFT)){
            if(currentChar > 0){
                currentChar--;
            }
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        gameOverFont.dispose();
        font.dispose();
    }
}
