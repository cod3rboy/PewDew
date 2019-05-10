package com.cod3rboy.pewdew.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.managers.GameKeys;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Jukebox;
import com.cod3rboy.pewdew.managers.Save;

public class HighScoreState extends GameState {

    private SpriteBatch sb;
    private BitmapFont font;

    private long[] highScores;
    private String[] names;

    private GlyphLayout gLayout;

    private String backOption;
    private Rectangle backBounds;
    private Vector3 touchPoint;

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
        param.size = 25;
        font = gen.generateFont(param);

        Save.load();
        highScores = Save.gd.getHighScores();
        names = Save.gd.getNames();

        backOption = "Back";
        backBounds = new Rectangle();
        touchPoint = new Vector3();
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
        float w,h;

        s = "High Scores";
        gLayout.setText(font, s);
        w = gLayout.width;
        h = PewDew.HEIGHT - PewDew.HEIGHT/6f;
        font.draw(sb, gLayout, (PewDew.WIDTH - w) / 2, h);
        h -= gLayout.height + 30;

        for (int i = 0; i < highScores.length; i++) {
            s = String.format(
                    "%3d. %6s %8s", i + 1, names[i],highScores[i]
            );
            gLayout.setText(font, s);
            w = gLayout.width;
            font.draw(sb, gLayout, (PewDew.WIDTH - w) / 2, h);
            h -= (gLayout.height + 10);
        }

        // Draw back button
        gLayout.setText(font, backOption);
        backBounds.set(20, PewDew.HEIGHT - 30, gLayout.width, gLayout.height);
        font.draw(sb,gLayout,backBounds.x, backBounds.y + gLayout.height);

        sb.end();
    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.ENTER) || GameKeys.isPressed(GameKeys.ESCAPE)) {
            gsm.setState(GameStateManager.MENU);
        }

        if(Gdx.input.isTouched()){
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            PewDew.cam.unproject(touchPoint.set(x, y ,0));
            if(backBounds.contains(touchPoint.x, touchPoint.y)){
                Jukebox.play("menuselect");
                gsm.setState(GameStateManager.MENU);
            }
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        font.dispose();
    }
}
