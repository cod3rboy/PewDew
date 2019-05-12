package com.cod3rboy.pewdew.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.entities.Asteroid;
import com.cod3rboy.pewdew.entities.Star;
import com.cod3rboy.pewdew.managers.GameKeys;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Jukebox;
import com.cod3rboy.pewdew.managers.Save;

import java.util.ArrayList;

public class HighScoreState extends GameState {

    private SpriteBatch sb;
    private BitmapFont font;
    private ShapeRenderer sr;

    private long[] highScores;
    private String[] names;

    private GlyphLayout gLayout;

    private String backOption;
    private Rectangle backBounds;
    private Vector3 touchPoint;

    private ArrayList<Asteroid> asteroids;

    private ArrayList<Star> starField;
    private float starSpawnTimer;
    private final float starSpawnTime = 0.4f;
    private final int MAX_STARS = 120; // Maximum number of stars allowed on screen at a time
    private final int STARS_PER_TIMEOUT = 20; // Number of stars to spawn at one shot

    public HighScoreState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();
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

        asteroids = new ArrayList<Asteroid>();

        for (int i = 0; i < 8; i++) {
            asteroids.add(new Asteroid(
                    MathUtils.random(PewDew.WIDTH),
                    MathUtils.random(PewDew.HEIGHT),
                    (MathUtils.random() < 0.5) ? Asteroid.SMALL : Asteroid.LARGE
            ));
        }

        starField = new ArrayList<Star>();
        starSpawnTimer = 0;
        spawnStars();
    }

    private void spawnStars() {
        if (starField.size() >= MAX_STARS) return;
        float minRadian, maxRadian;
        for(int j=0;j<4;j++){
            minRadian = MathUtils.PI2 * j/4;
            maxRadian = MathUtils.PI2 * (j+1)/4f;
            for (int i = 0; i < STARS_PER_TIMEOUT/4; i++) {
                Star s = new Star(PewDew.WIDTH / 2f, PewDew.HEIGHT / 2f, MathUtils.random(minRadian, maxRadian));
                // Set random Color
                s.setColor(
                        MathUtils.random(1f),
                        MathUtils.random(1f),
                        MathUtils.random(1f),
                        1
                );

                // Set random speed
                s.setSpeed(MathUtils.random(100,200));
                starField.add(s);
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).update(dt);
        }

        // Add new stars to the star field
        starSpawnTimer += dt;
        if (starSpawnTimer >= starSpawnTime) {
            // Time out
            starSpawnTimer = 0;
            spawnStars();
        }

        // Update stars in star field
        for (int i = 0; i < starField.size(); i++) {
            Star s = starField.get(i);
            s.update(dt);
            if (s.shouldRemove()) {
                starField.remove(i);
                i--;
            }
        }
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(PewDew.cam.combined);
        sr.setProjectionMatrix(PewDew.cam.combined);


        // draw starfield
        for (int i = 0; i < starField.size(); i++) {
            starField.get(i).draw(sr);
        }

        // draw asteroids
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).draw(sr);
        }

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
        sr.dispose();
        font.dispose();
    }
}
