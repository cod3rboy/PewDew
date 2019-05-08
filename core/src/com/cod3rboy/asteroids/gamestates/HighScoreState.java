package com.cod3rboy.asteroids.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.cod3rboy.asteroids.Asteroids;
import com.cod3rboy.asteroids.managers.GameKeys;
import com.cod3rboy.asteroids.managers.GameStateManager;
import com.cod3rboy.asteroids.managers.Save;

public class HighScoreState extends GameState {

    private SpriteBatch sb;
    private BitmapFont font;

    private long[] highScores;
    private String[] names;

    public HighScoreState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        sb = new SpriteBatch();
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Hyperspace Bold.ttf")
        );
        param.size = 20;
        font = gen.generateFont(param);

        Save.load();
        highScores = Save.gd.getHighScores();
        names = Save.gd.getNames();
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(Asteroids.cam.combined);

        sb.begin();

        String s;
        float w;

        s = "High Scores";

        //w = font.getBounds(s).width;
        w = 140;
        font.draw(sb, s, (Asteroids.WIDTH - w) / 2, 300);

        for (int i = 0; i < highScores.length; i++) {
            s = String.format(
                    "%3d. %7s %s", i + 1, highScores[i], names[i]
            );
            //w = font.getBounds(s).width;
            w = 220;
            font.draw(sb, s, (Asteroids.WIDTH - w) / 2, 270 - 20 * i);
        }
        sb.end();
    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.ENTER) || GameKeys.isPressed(GameKeys.ESCAPE)) {
            gsm.setState(GameStateManager.MENU);
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        font.dispose();
    }
}
