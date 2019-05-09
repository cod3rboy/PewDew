package com.cod3rboy.pewdew.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.managers.GameKeys;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Save;

public class HighScoreState extends GameState {

    private SpriteBatch sb;
    private BitmapFont font;

    private long[] highScores;
    private String[] names;

    private GlyphLayout gLayout;

    public HighScoreState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        sb = new SpriteBatch();
        gLayout = new GlyphLayout();
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
        sb.setProjectionMatrix(PewDew.cam.combined);

        sb.begin();

        String s;
        float w;

        s = "High Scores";
        gLayout.setText(font, s);
        w = gLayout.width;
        font.draw(sb, gLayout, (PewDew.WIDTH - w) / 2, 300);

        for (int i = 0; i < highScores.length; i++) {
            s = String.format(
                    "%3d. %7s %s", i + 1, highScores[i], names[i]
            );
            gLayout.setText(font, s);
            w = gLayout.width;
            font.draw(sb, gLayout, (PewDew.WIDTH - w) / 2, 270 - 20 * i);
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
