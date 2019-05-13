package com.cod3rboy.pewdew.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.entities.Asteroid;
import com.cod3rboy.pewdew.entities.Star;
import com.cod3rboy.pewdew.managers.GameKeys;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Jukebox;
import com.cod3rboy.pewdew.managers.Save;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private BitmapFont titleFont;
    private BitmapFont font;

    private GlyphLayout gLayout;

    private final String title = "PewDew";
    private int currentItem;

    private String[] menuItems;
    private HashMap<String, Rectangle> menuBounds;
    private Vector3 touchPoint;

    private ArrayList<Asteroid> asteroids;

    private float flashTimer = 0;
    private float flashTime = 0.05f;

    private ArrayList<Star> starField;
    private float starSpawnTimer;
    private final float starSpawnTime = 0.4f;
    private final int MAX_STARS = 120; // Maximum number of stars allowed on screen at a time
    private final int STARS_PER_TIMEOUT = 20; // Number of stars to spawn at one shot
    private Vector2 starFieldCenter;

    // Music Button
    private BitmapFont musicFont;
    private GlyphLayout musicFontLayout;
    private Rectangle musicBtnBounds;
    private boolean isMusicOn;


    public MenuState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        // Todo : use one spritebatch only
        sr = new ShapeRenderer();
        sb = new SpriteBatch();
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        param.size = 70;
        param.color = Color.WHITE;
        titleFont = gen.generateFont(param);

        param.size = 40;
        font = gen.generateFont(param);

        gLayout = new GlyphLayout();

        menuItems = new String[]{
                "Play",
                "High Scores",
                "Quit"
        };
        menuBounds = new HashMap<String, Rectangle>();
        for (int i = 0; i < menuItems.length; i++) {
            menuBounds.put(menuItems[i], new Rectangle());
        }
        touchPoint = new Vector3();

        Save.load();

        asteroids = new ArrayList<Asteroid>();

        for (int i = 0; i < 8; i++) {
            asteroids.add(new Asteroid(
                    MathUtils.random(PewDew.WIDTH),
                    MathUtils.random(PewDew.HEIGHT),
                    (MathUtils.random() < 0.5) ? Asteroid.SMALL : Asteroid.LARGE
            ));
        }


        starField = new ArrayList<Star>();
        starFieldCenter = new Vector2(PewDew.WIDTH / 2f, PewDew.HEIGHT / 2f);
        starSpawnTimer = 0;
        spawnStars();

        // Initialize Music Button Font and Bounds
        param.size = 25;
        musicFont = gen.generateFont(param);
        musicFontLayout = new GlyphLayout();
        musicBtnBounds = new Rectangle();
        isMusicOn = GameStateManager.getMusicSetting();

        if(GameStateManager.getMusicSetting() && !Jukebox.isPlayingBackgroundMusic("chronos")) {
            // Start background music
            Jukebox.playBackgroundMusic("chronos");
        }
    }

    private void spawnStars() {
        if (starField.size() >= MAX_STARS) return;
        float minRadian, maxRadian;
        for (int j = 0; j < 4; j++) {
            minRadian = MathUtils.PI2 * j / 4;
            maxRadian = MathUtils.PI2 * (j + 1) / 4f;
            for (int i = 0; i < STARS_PER_TIMEOUT / 4; i++) {
                Star s = new Star(starFieldCenter.x, starFieldCenter.y, MathUtils.random(minRadian, maxRadian));
                // Set random Color
                s.setColor(
                        MathUtils.random(1f),
                        MathUtils.random(1f),
                        MathUtils.random(1f),
                        1
                );

                // Set random speed
                s.setSpeed(MathUtils.random(100, 200));
                starField.add(s);
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        if (flashTimer > flashTime) {
            titleFont.setColor(MathUtils.random(1f), MathUtils.random(1f), MathUtils.random(1f), 1);
            flashTimer = 0;
        } else {
            flashTimer += dt;
        }

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

        // Draw title
        gLayout.setText(titleFont, title);
        float width = gLayout.width;
        titleFont.draw(
                sb,
                gLayout,
                (PewDew.WIDTH - width) / 2,
                PewDew.HEIGHT - PewDew.HEIGHT / 6f
        );

        // Draw menu
        for (int i = 0; i < menuItems.length; i++) {
            if (currentItem == i) font.setColor(Color.RED);
            else font.setColor(Color.WHITE);
            gLayout.setText(font, menuItems[i]);
            width = gLayout.width;
            // Update Bounds
            Rectangle bounds = menuBounds.get(menuItems[i]);
            bounds.set((PewDew.WIDTH - width) / 2, 200 - 60 * i, width, gLayout.height);

            // Draw
            font.draw(sb, gLayout, bounds.x, bounds.y + bounds.height);
        }


        if(isMusicOn) {
            musicFont.setColor(0,1,0,1);
            musicFontLayout.setText(musicFont, "MUSIC ON");
        }else{
            musicFont.setColor(1,0,0,1);
            musicFontLayout.setText(musicFont, "MUSIC OFF");
        }
        musicBtnBounds.setWidth(musicFontLayout.width);
        musicBtnBounds.setHeight(musicFontLayout.height);
        musicBtnBounds.x = PewDew.WIDTH - musicBtnBounds.width - 10;
        musicBtnBounds.y = PewDew.HEIGHT - musicBtnBounds.height - 15;
        musicFont.draw(
                sb,
                musicFontLayout,
                musicBtnBounds.x,
                musicBtnBounds.y + musicBtnBounds.height
        );
        sb.end();
    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.UP)) {
            if (currentItem >= 0) currentItem--;
            if (currentItem < 0) currentItem = menuItems.length - 1;
            Jukebox.play("menuselect");
        }
        if (GameKeys.isPressed(GameKeys.DOWN)) {
            if (currentItem < menuItems.length) currentItem++;
            if (currentItem >= menuItems.length) currentItem = 0;
            Jukebox.play("menuselect");
        }
        if (GameKeys.isPressed(GameKeys.ENTER)) select();

        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            PewDew.cam.unproject(touchPoint.set(x, y, 0));
            for (int i = 0; i < menuItems.length; i++) {
                Rectangle bounds = menuBounds.get(menuItems[i]);
                //System.out.println(String.format("Touched at %.2f , %.2f", touchPoint.x, touchPoint.y) + " and Bounds : " + menuItems[i] + " - " + bounds.toString());
                if (bounds.contains(touchPoint.x, touchPoint.y)) {
                    currentItem = i;
                    select();
                    return;
                }
            }

            // Music Button Touch
            if(musicBtnBounds.contains(touchPoint.x, touchPoint.y)){
                Jukebox.play("menuselect");
                isMusicOn = !isMusicOn;
                GameStateManager.setMusicSetting(isMusicOn);
                if(isMusicOn){
                    // Start background music
                    Jukebox.playBackgroundMusic("chronos");
                }else{
                    // Stop background music
                    Jukebox.stopBackgroundMusic("chronos");
                }
                return;
            }
        }
        if (Gdx.input.isTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            PewDew.cam.unproject(touchPoint.set(x, y, 0));
            // No Option is selected by the touch so we change star field center to the touch point
            starFieldCenter.set(touchPoint.x, touchPoint.y);
        } else {
            // Reset starfield center to screen center when screen is not touched
            starFieldCenter.set(PewDew.WIDTH / 2f, PewDew.HEIGHT / 2f);
        }
    }

    private void select() {
        // play
        if (currentItem == 0) {
            Jukebox.play("menuselect");
            //Jukebox.stopAllBackgroundMusic();
            gsm.setState(GameStateManager.PLAY);
        } else if (currentItem == 1) { // High Score state
            Jukebox.play("menuselect");
            gsm.setState(GameStateManager.HIGHSCORE);
        } else if (currentItem == 2) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        titleFont.dispose();
        font.dispose();
        musicFont.dispose();
    }
}
