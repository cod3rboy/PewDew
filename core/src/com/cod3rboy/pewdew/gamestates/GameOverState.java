package com.cod3rboy.pewdew.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.managers.GameKeys;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Save;

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
    private GlyphLayout gLayout;

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
        gLayout = new GlyphLayout();
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(PewDew.cam.combined);
        sr.setProjectionMatrix(PewDew.cam.combined);
        sb.begin();
        String s;
        float w;
        s = "Game Over";
        gameOverFont.setColor(1,0,0,1);
        gLayout.setText(gameOverFont, s);
        w = gLayout.width;
        gameOverFont.draw(sb, gLayout, (PewDew.WIDTH - w) / 2, 220);
        if (!newHighScore) {
            sb.end();
            return;
        }
        s = "New High Score : " + Save.gd.getTentativeScore();
        gLayout.setText(font, s);
        w = gLayout.width;
        font.draw(sb, gLayout, (PewDew.WIDTH - w) / 2, 180);
        gLayout.setText(font, "ZZZ"); // Set a fixed text width
        w = gLayout.width;
        float xAlign = (PewDew.WIDTH - w) / 2;
        for (int i = 0; i < newName.length; i++) {
            gLayout.setText(font, Character.toString(newName[i]));
            font.draw(sb, gLayout, xAlign + 14 * i, 150);
        }
        sb.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(
                xAlign + 14 * currentChar,
                130,
                xAlign + gLayout.width + 14 * currentChar,
                130
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
