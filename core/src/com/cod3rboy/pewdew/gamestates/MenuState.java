package com.cod3rboy.pewdew.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.entities.Asteroid;
import com.cod3rboy.pewdew.managers.GameKeys;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Jukebox;
import com.cod3rboy.pewdew.managers.Save;

import java.util.ArrayList;

public class MenuState extends GameState{

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private BitmapFont titleFont;
    private BitmapFont font;

    private GlyphLayout gLayout;

    private final String title = "PewDew";
    private int currentItem;
    private String[] menuItems;

    private ArrayList<Asteroid> asteroids;

    private float flashTimer = 0;
    private float flashTime = 0.05f;

    public MenuState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        // Todo : use only spritebatch only
        sr = new ShapeRenderer();
        sb = new SpriteBatch();
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        param.size = 56;
        param.color = Color.WHITE;
        titleFont = gen.generateFont(param);

        param.size = 20;
        font = gen.generateFont(param);

        gLayout = new GlyphLayout();

        menuItems = new String[]{
                "Play",
                "Highscores",
                "Quit"
        };

        Save.load();

        asteroids = new ArrayList<Asteroid>();

        for(int i=0; i < 6; i++){
            asteroids.add(new Asteroid(
                    MathUtils.random(PewDew.WIDTH),
                    MathUtils.random(PewDew.HEIGHT),
                    (MathUtils.random() < 0.5) ? Asteroid.SMALL : Asteroid.LARGE
            ));
        }

        // Start background music
        Jukebox.setBackgroundVolume(1);
        Jukebox.playBackgroundMusic();
    }

    @Override
    public void update(float dt) {
        handleInput();
        if(flashTimer > flashTime){
            titleFont.setColor(MathUtils.random(1f), MathUtils.random(1f), MathUtils.random(1f), 1);
            flashTimer = 0;
        }else{
            flashTimer += dt;
        }

        for(int i=0; i<asteroids.size(); i++){
            asteroids.get(i).update(dt);
        }
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(PewDew.cam.combined);
        sr.setProjectionMatrix(PewDew.cam.combined);

        // draw asteroids
        for(int i=0; i<asteroids.size(); i++){
            asteroids.get(i).draw(sr);
        }

        sb.begin();

        // Draw title
        gLayout.setText(titleFont, title);
        float width = gLayout.width;
        titleFont.draw(
                sb,
                gLayout,
                (PewDew.WIDTH - width) /2,
                300
        );

        // Draw menu
        for(int i=0; i<menuItems.length; i++){
            if(currentItem == i) font.setColor(Color.RED);
            else font.setColor(Color.WHITE);
            gLayout.setText(font, menuItems[i]);
            width = gLayout.width;
            font.draw(
                    sb,
                    gLayout,
                    (PewDew.WIDTH - width) /2,
                    180 - 35 * i
            );
        }
        sb.end();
    }

    @Override
    public void handleInput() {
        if(GameKeys.isPressed(GameKeys.UP)){
            if(currentItem > 0) currentItem --;
            Jukebox.play("menuselect");
        }
        if(GameKeys.isPressed(GameKeys.DOWN)){
            if(currentItem < menuItems.length - 1){
                currentItem++;
            }
            Jukebox.play("menuselect");
        }
        if(GameKeys.isPressed(GameKeys.ENTER)) select();
    }

    private void select(){
        // play
        if(currentItem == 0){
            gsm.setState(GameStateManager.PLAY);
        }else if(currentItem == 1){ // High Score state
            gsm.setState(GameStateManager.HIGHSCORE);
        }else if(currentItem == 2){
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        Jukebox.stopBackgroundMusic();
        sb.dispose();
        sr.dispose();
        titleFont.dispose();
        font.dispose();
    }
}
