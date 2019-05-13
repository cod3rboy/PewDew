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
import com.cod3rboy.pewdew.entities.Star;
import com.cod3rboy.pewdew.managers.GameKeys;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Jukebox;
import com.cod3rboy.pewdew.managers.Save;

import java.util.ArrayList;

public class GameOverState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;
    private boolean newHighScore;
    private long score;
    private char[] newName;
    private int currentChar;

    public GameOverState(GameStateManager gsm) {
        super(gsm);
    }

    private BitmapFont gameOverFont;
    private BitmapFont font;
    private GlyphLayout gLayout;

    private String continueOption = "continue";
    private Rectangle continueBounds;
    private Vector3 touchPoint;

    // Virtual Keys UI Layout
    private String keys[];
    private Rectangle keyBounds[];
    private BitmapFont keyFont;
    private final int KEYS_PER_ROW = 7;
    private GlyphLayout keyLayout;
    private boolean keyTouched = false;

    private ArrayList<Star> starField;
    private float starSpawnTimer;
    private final float starSpawnTime = 0.20f;
    private final int MAX_STARS = 300; // Maximum number of stars allowed on screen at a time
    private final int STARS_PER_TIMEOUT = 50; // Number of stars to spawn at one shot
    private final float MIN_DIST_CENTER = 20;

    @Override
    public void init() {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();
        score = Save.gd.getTentativeScore();
        newHighScore = Save.gd.isHighScore(score);
        if (newHighScore) {
            newName = new char[]{'A', 'A', 'A', 'A', 'A', 'A'};
            currentChar = 0;
        }

        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        param.size = 50;
        gameOverFont = gen.generateFont(param);
        param.size = 25;
        font = gen.generateFont(param);

        gLayout = new GlyphLayout();

        continueBounds = new Rectangle();
        touchPoint = new Vector3();

        keys = new String[]{
                "a", "b", "c", "d", "e", "f", "g",
                "h", "i", "j", "k", "l", "m", "n",
                "o", "p", "q", "r", "s", "t", "u",
                "v", "w", "x", "y", "z", "Left", "Right"
        };
        keyBounds = new Rectangle[keys.length];
        for(int i=0; i<keyBounds.length; i++) keyBounds[i] = new Rectangle();

        param.size = 15;
        keyFont = gen.generateFont(param);
        keyLayout = new GlyphLayout();

        starField = new ArrayList<Star>();
        starSpawnTimer = 0;
        spawnStars();


        if(GameStateManager.getMusicSetting()) {
            // Play background gameover music
            Jukebox.playBackgroundMusic("gameover");
        }else{
            Jukebox.playBackgroundMusic("deepspace");
        }

    }

    private void spawnStars() {
        if (starField.size() >= MAX_STARS) return;

        int starsPerSide = STARS_PER_TIMEOUT / 4;

        float gap = 40; // For same horizontal gap and vertical gap

        // Spawn stars on bottom side
        for(int i=0; i<starsPerSide; i++){
            float x = MathUtils.random(PewDew.WIDTH);
            float y = MathUtils.random(gap);
            float direction = MathUtils.atan2(PewDew.HEIGHT/2 - y, PewDew.WIDTH/2 - x);

            Star s = new Star(x, y ,direction);
            // Set random Color
            s.setColor(
                    MathUtils.random(1f),
                    MathUtils.random(1f),
                    MathUtils.random(1f),
                    1
            );
            // Set random speed
            s.setSpeed(MathUtils.random(100,200));
            s.setDecrementRadius(true);
            s.setRadius(1f);
            s.setDeltaRadius(0.5f);
            starField.add(s);
        }

        // Spawn stars on left side
        for(int i=0; i<starsPerSide; i++){
            float x = MathUtils.random(gap);
            float y = MathUtils.random(PewDew.HEIGHT);
            float direction = MathUtils.atan2(PewDew.HEIGHT/2 - y, PewDew.WIDTH/2 - x);

            Star s = new Star(x, y ,direction);
            // Set random Color
            s.setColor(
                    MathUtils.random(1f),
                    MathUtils.random(1f),
                    MathUtils.random(1f),
                    1
            );
            // Set random speed
            s.setSpeed(MathUtils.random(100,200));
            s.setDecrementRadius(true);
            s.setRadius(1.5f);
            s.setDeltaRadius(0.3f);
            starField.add(s);
        }

        // Spawn stars on top side
        for(int i=0; i<starsPerSide; i++){
            float x = MathUtils.random(PewDew.WIDTH);
            float y = MathUtils.random(PewDew.HEIGHT-gap, PewDew.HEIGHT);
            float direction = MathUtils.atan2(PewDew.HEIGHT/2 - y, PewDew.WIDTH/2 - x);

            Star s = new Star(x, y ,direction);
            // Set random Color
            s.setColor(
                    MathUtils.random(1f),
                    MathUtils.random(1f),
                    MathUtils.random(1f),
                    1
            );
            // Set random speed
            s.setSpeed(MathUtils.random(100,200));
            s.setDecrementRadius(true);
            s.setRadius(1.5f);
            s.setDeltaRadius(0.3f);
            starField.add(s);
        }

        // Spawn stars on right side
        for(int i=0; i<starsPerSide; i++){
            float x = MathUtils.random(PewDew.WIDTH-gap, PewDew.WIDTH);
            float y = MathUtils.random(PewDew.HEIGHT);
            float direction = MathUtils.atan2(PewDew.HEIGHT/2 - y, PewDew.WIDTH/2 - x);

            Star s = new Star(x, y ,direction);
            // Set random Color
            s.setColor(
                    MathUtils.random(1f),
                    MathUtils.random(1f),
                    MathUtils.random(1f),
                    1
            );
            // Set random speed
            s.setSpeed(MathUtils.random(100,200));
            s.setDecrementRadius(true);
            s.setRadius(1.5f);
            s.setDeltaRadius(0.3f);
            starField.add(s);
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
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
            double xDist = (PewDew.WIDTH/2f) - s.getX();
            double yDist = (PewDew.HEIGHT/2f) - s.getY();
            double dist = Math.sqrt(xDist * xDist + yDist * yDist);
            if(dist < MIN_DIST_CENTER) {
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

        sb.begin();
        String s;
        float w,h;
        s = "Game Over";
        gameOverFont.setColor(1,0,0,1);
        gLayout.setText(gameOverFont, s);
        w = gLayout.width;
        h = PewDew.HEIGHT - PewDew.HEIGHT / 6f;
        gameOverFont.draw(sb, gLayout, (PewDew.WIDTH - w) / 2, h);
        h -= gLayout.height + 10;

        gLayout.setText(font, continueOption);
        continueBounds.set(10,PewDew.HEIGHT - 40,gLayout.width,gLayout.height);
        font.draw(sb,gLayout,continueBounds.x, continueBounds.y + continueBounds.height);

        if (newHighScore) { // If it is a new Highscore

            s = "New High Score : " + Save.gd.getTentativeScore();
            gLayout.setText(font, s);
            w = gLayout.width;
            font.draw(sb, gLayout, (PewDew.WIDTH - w) / 2, h);
            h -= gLayout.height + 20;

            gLayout.setText(font, "ZZZZZZ"); // Set a fixed text width
            w = gLayout.width;
            float xAlign = (PewDew.WIDTH - w) / 2;
            for (int i = 0; i < newName.length; i++) {
                gLayout.setText(font, Character.toString(newName[i]));
                font.draw(sb, gLayout, xAlign + 20 * i, h);
            }
            h -= gLayout.height + 5;
            sb.end();

            sr.setColor(1,1,0,1);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.line(
                    xAlign + 20 * currentChar,
                    h,
                    xAlign + gLayout.width + 20 * currentChar,
                    h
            );
            sr.end();

            sb.begin();
            drawKeys(sb);
            sb.end();

            // Draw key bounds
            sr.begin(ShapeRenderer.ShapeType.Line);
            for(int i=0; i<keyBounds.length; i++){
                sr.setColor(MathUtils.random(.5f, 1f),MathUtils.random(1f),MathUtils.random(1f),1);
                sr.rect(keyBounds[i].x,keyBounds[i].y,keyBounds[i].width,keyBounds[i].height);
            }
            sr.end();
            return;
        }

        // Not new highscore
        gameOverFont.setColor(1,1,1,1);
        gLayout.setText(gameOverFont, String.format("Score : %d", score));
        gameOverFont.draw(sb,gLayout,(PewDew.WIDTH-gLayout.width)/2, (PewDew.HEIGHT-gLayout.height)/2);
        sb.end();
    }

    private void drawKeys(SpriteBatch sb){
        float hPad = 15, vPad = 15, hMargin = 15, vMargin = 5;
        int rows = (keys.length / KEYS_PER_ROW);
        float rowX = 140;
        float rowY = 20;

        int k;
        for(int i=rows-1; i>=0; i--){
            for(int j=0; j<KEYS_PER_ROW;j++){
                k = j + i * KEYS_PER_ROW;
                keyLayout.setText(keyFont, keys[k].toUpperCase());
                // Update Bounds for current key
                keyBounds[k].set(rowX + hMargin, rowY + vMargin, (2 * hPad + keyLayout.width), (2 * vPad + keyLayout.height));
                keyFont.draw(sb,keyLayout, keyBounds[k].x + hPad, keyBounds[k].y + vPad + keyLayout.height);
                rowX += (2 * hPad + 2 * hMargin + keyLayout.width);
            }
            rowX = 140;
            rowY += (2 * vPad + 2 * vMargin + keyLayout.height);
        }
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

        if(Gdx.input.isTouched()){
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            PewDew.cam.unproject(touchPoint.set(x, y ,0));
            if(continueBounds.contains(touchPoint.x, touchPoint.y)){
                Jukebox.play("menuselect");
                if(newHighScore){
                    Save.gd.addHighScore(Save.gd.getTentativeScore(), new String(newName));
                    Save.save();
                }
                gsm.setState(GameStateManager.MENU);
            }

            if(keyTouched) return;
            keyTouched = true;
            // Check which key is touched by user on virtual keyboard
            for(int i=0; i<keyBounds.length-2; i++){
                if(keyBounds[i].contains(touchPoint.x, touchPoint.y)){
                    newName[currentChar] = keys[i].charAt(0);
                    Jukebox.play("keypress");
                    currentChar++;
                    if(currentChar >= newName.length) currentChar = 0;
                    break;
                }
            }

            // Check left key press
            if(keyBounds[keyBounds.length-2].contains(touchPoint.x, touchPoint.y)){
                Jukebox.play("keypress");
                currentChar--;
                if(currentChar < 0) currentChar = newName.length-1;
            }

            // Check right key press
            if(keyBounds[keyBounds.length-1].contains(touchPoint.x, touchPoint.y)){
                Jukebox.play("keypress");
                currentChar++;
                if(currentChar >= newName.length) currentChar = 0;
            }
        }else{ // When not touching
            keyTouched = false;
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        gameOverFont.dispose();
        font.dispose();
        Jukebox.stopAllBackgroundMusic();
    }
}
