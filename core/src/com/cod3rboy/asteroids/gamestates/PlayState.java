package com.cod3rboy.asteroids.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.cod3rboy.asteroids.Asteroids;
import com.cod3rboy.asteroids.entities.Asteroid;
import com.cod3rboy.asteroids.entities.Bullet;
import com.cod3rboy.asteroids.entities.FlyingSaucer;
import com.cod3rboy.asteroids.entities.Particle;
import com.cod3rboy.asteroids.entities.Player;
import com.cod3rboy.asteroids.managers.GameKeys;
import com.cod3rboy.asteroids.managers.GameStateManager;
import com.cod3rboy.asteroids.managers.Jukebox;
import com.cod3rboy.asteroids.managers.Save;

import java.util.ArrayList;

public class PlayState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private BitmapFont font;
    private BitmapFont pausedFont;

    private Player player;
    private Player hudPlayer;

    private ArrayList<Bullet> bullets;
    private ArrayList<Bullet> enemyBullets;

    private ArrayList<Asteroid> asteroids;

    private FlyingSaucer flyingSaucer;
    private float fsTimer;
    private float fsTime;

    private ArrayList<Particle> particles;

    private int level;
    private int totalAsteroids;
    private int numAsteroidsLeft;

    private float maxDelay;
    private float minDelay;
    private float currentDelay;
    private float bgTimer;
    private boolean playLowPulse;

    private boolean gamePaused;

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        sr = new ShapeRenderer();
        sb = new SpriteBatch();

        gamePaused = false;

        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 16;
        font = gen.generateFont(param);
        param.size = 34;
        pausedFont = gen.generateFont(param);

        bullets = new ArrayList<Bullet>();

        player = new Player(bullets);

        asteroids = new ArrayList<Asteroid>();

        particles = new ArrayList<Particle>();

        level = 1;
        spawnAsteroids();

        hudPlayer = new Player(null);

        fsTimer = 0;
        fsTime = 15; // 15 secs after flying saucer appears
        enemyBullets = new ArrayList<Bullet>();

        // Setup bg music
        maxDelay = 1;
        minDelay = 0.25f;
        currentDelay = maxDelay;
        bgTimer = maxDelay;
        playLowPulse = true;

        // Play background music in low volume
        Jukebox.setBackgroundVolume(0.5f);
        Jukebox.playBackgroundMusic();
    }

    private void createParticles(float x, float y) {
        for (int i = 0; i < 10; i++) {
            particles.add(new Particle(x, y));
        }
    }

    private void splitAsteroids(Asteroid a) {
        createParticles(a.getX(), a.getY());
        numAsteroidsLeft--;
        currentDelay = ((maxDelay - minDelay) * numAsteroidsLeft / totalAsteroids) + minDelay;
        if (a.getType() == Asteroid.LARGE) {
            asteroids.add(new Asteroid(a.getX(), a.getY(), Asteroid.MEDIUM));
            asteroids.add(new Asteroid(a.getX(), a.getY(), Asteroid.MEDIUM));
        }

        if (a.getType() == Asteroid.MEDIUM) {
            asteroids.add(new Asteroid(a.getX(), a.getY(), Asteroid.SMALL));
            asteroids.add(new Asteroid(a.getX(), a.getY(), Asteroid.SMALL));
        }
    }

    private void spawnAsteroids() {
        asteroids.clear();
        int numToSpawn = 4 + level - 1;
        totalAsteroids = numToSpawn * 7;
        numAsteroidsLeft = totalAsteroids;

        currentDelay = maxDelay;

        for (int i = 0; i < numToSpawn; i++) {
            float x = MathUtils.random(Asteroids.WIDTH);
            float y = MathUtils.random(Asteroids.HEIGHT);
            float dx = x - player.getX();
            float dy = y - player.getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            while (dist < 100) {
                x = MathUtils.random(Asteroids.WIDTH);
                y = MathUtils.random(Asteroids.HEIGHT);
                dx = x - player.getX();
                dy = y - player.getY();
                dist = (float) Math.sqrt(dx * dx + dy * dy);
            }

            asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
        }
    }

    @Override
    public void update(float dt) {
        // get user input
        handleInput();

        if(gamePaused) return;

        // next level
        if (asteroids.size() == 0) {
            level++;
            spawnAsteroids();
        }

        // update player
        player.update(dt);
        if (player.isDead()) {
            if(player.getLives() == 0){
                Jukebox.stopAll();
                Save.gd.setTentativeScore(player.getScore());
                gsm.setState(GameStateManager.GAMEOVER);
                return;
            }
            player.reset();
            player.loseLife();
            flyingSaucer = null;
            Jukebox.stop("smallsaucer");
            Jukebox.stop("largesaucer");
            return;
        }

        // update player bullets
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update(dt);
            if (bullets.get(i).shouldRemove()) {
                bullets.remove(i);
                i--;
            }
        }

        // update flying saucer
        if(flyingSaucer == null){
            fsTimer += dt;
            if(fsTimer > fsTime) {
                fsTimer = 0;
                int type = (MathUtils.random() < 0.5) ? FlyingSaucer.SMALL : FlyingSaucer.LARGE;
                int direction = MathUtils.random() < 0.5 ? FlyingSaucer.RIGHT : FlyingSaucer.LEFT;
                flyingSaucer = new FlyingSaucer(type, direction, player, enemyBullets);
            }
        }else{
            // if there is flying saucer already then we try to remove it
            flyingSaucer.update(dt);
            if(flyingSaucer.shouldRemove()) {
                flyingSaucer = null;
                Jukebox.stop("smallsaucer");
                Jukebox.stop("largesaucer");
            }
        }


        // update flying saucer bullets
        for(int i=0; i < enemyBullets.size(); i++){
            enemyBullets.get(i).update(dt);
            if (enemyBullets.get(i).shouldRemove()) {
                enemyBullets.remove(i);
                i--;
            }
        }

        // update asteroids
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).update(dt);
            if (asteroids.get(i).shouldRemove()) {
                asteroids.remove(i);
                i--;
            }
        }

        // update particles
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).update(dt);
            if (particles.get(i).shouldRemove()) {
                particles.remove(i);
                i--;
            }
        }

        // check collision
        checkCollisions();

        // play bg music
        bgTimer += dt;

        if(!player.isHit() && bgTimer >= currentDelay){
            if(playLowPulse) Jukebox.play("pulselow");
            else Jukebox.play("pulsehigh");
            playLowPulse = !playLowPulse;
            bgTimer = 0;
        }
    }

    private void checkCollisions() {
        if (!player.isHit()) {
            // Player-Asteroid collision
            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid a = asteroids.get(i);
                if (a.intersects(player)) {
                    player.hit();
                    asteroids.remove(i);
                    i--;
                    splitAsteroids(a);
                    Jukebox.play("explode");
                    break;
                }
            }

        }

        // Bullet-Asteroid collision
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid a = asteroids.get(j);
                // if asteroid contains bullet
                if (a.contains(b.getX(), b.getY())) {
                    bullets.remove(i);
                    i--;
                    asteroids.remove(j);
                    j--;
                    splitAsteroids(a);
                    // increments player score
                    player.incrementScore(a.getScore());
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // player - flying saucer collision
        if(flyingSaucer != null){
            if(player.intersects(flyingSaucer)){
                player.hit();
                createParticles(player.getX(), player.getY());
                createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                flyingSaucer = null;
                Jukebox.stop("smallsaucer");
                Jukebox.stop("largesaucer");
                Jukebox.play("explode");
            }
        }

        // bullet - flying saucer collision
        if(flyingSaucer != null){
            for(int i=0; i<bullets.size(); i++){
                Bullet b = bullets.get(i);
                if(flyingSaucer.contains(b.getX(), b.getY())){
                    bullets.remove(i);
                    i--;
                    createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                    player.incrementScore(flyingSaucer.getScore());
                    flyingSaucer = null;
                    Jukebox.stop("smallsaucer");
                    Jukebox.stop("largesaucer");
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // player - enemy bullets collision
        if(!player.isHit()){
            for(int i=0; i<enemyBullets.size(); i++){
                Bullet b = enemyBullets.get(i);
                if(player.contains(b.getX(), b.getY())){
                    player.hit();
                    enemyBullets.remove(i);
                    i--;
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // flying saucer - asteroid collision
        if(flyingSaucer != null){
            for(int i=0; i< asteroids.size(); i++){
                Asteroid a = asteroids.get(i);
                if(a.intersects(flyingSaucer)){
                    asteroids.remove(i);
                    i--;
                    splitAsteroids(a);
                    createParticles(a.getX(), a.getY());
                    createParticles(flyingSaucer.getX(), flyingSaucer.getY());
                    flyingSaucer = null;
                    Jukebox.stop("smallsaucer");
                    Jukebox.stop("largesaucer");
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        // asteroids -  enemy bullets collision
        for(int i=0; i < enemyBullets.size(); i++){
            Bullet b = enemyBullets.get(i);
            for(int j = 0; j < asteroids.size(); j++){
                Asteroid a = asteroids.get(j);
                if(a.contains(b.getX(), b.getY())){
                    asteroids.remove(j);
                    j--;
                    splitAsteroids(a);
                    enemyBullets.remove(i);
                    i--;
                    createParticles(a.getX(), a.getY());
                    Jukebox.play("explode");
                    break;
                }
            }
        }

    }


    @Override
    public void draw() {
        sr.setProjectionMatrix(Asteroids.cam.combined);
        sb.setProjectionMatrix(Asteroids.cam.combined);

        // draw player
        player.draw(sr);

        // draw player bullets
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(sr);
        }

        // draw flying saucer
        if(flyingSaucer != null) flyingSaucer.draw(sr);

        // draw flying saucer bullets
        for(int i=0; i<enemyBullets.size(); i++){
            enemyBullets.get(i).draw(sr);
        }

        // draw asteroids
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).draw(sr);
        }

        // draw particles
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).draw(sr);
        }

        // draw score
        sb.setColor(1, 1, 1, 1);
        sb.begin();
        font.draw(sb, String.format("Score : %d",player.getScore()), 40, 390);
        font.draw(sb, "Lives : ", 40, 360);

        // draw game paused text
        if(gamePaused) {
            // Draw Game Paused Text
            pausedFont.setColor(0,1,0,1);
            float width = 240;
            pausedFont.draw(sb, "Game Paused", Asteroids.WIDTH/2 - width/2, Asteroids.HEIGHT/2);
        }
        sb.end();

        // draw lives
        for(int i=0; i<player.getLives(); i++){
            hudPlayer.setPosition(120 + i * 15, 352);
            hudPlayer.draw(sr);
        }
    }

    @Override
    public void handleInput() {
        if(player.isHit()) return;
        player.setLeft(GameKeys.isDown(GameKeys.LEFT));
        player.setRight(GameKeys.isDown(GameKeys.RIGHT));
        player.setUp(GameKeys.isDown(GameKeys.UP));
        if (GameKeys.isPressed(GameKeys.SPACE)) player.shoot();

        // Handle Pause / Resume / Exit input
        if(!gamePaused && GameKeys.isPressed(GameKeys.ESCAPE)) {
            gamePaused = true;
            Jukebox.stopAll();
            Jukebox.pauseBackgroundMusic();
        }else if(gamePaused && GameKeys.isPressed(GameKeys.ENTER)){
            gamePaused = false;
            // Restart previous stopped flying saucer music loop
            if(flyingSaucer != null){
                int type = flyingSaucer.getType();
                if(type == FlyingSaucer.LARGE){
                    Jukebox.loop("largesaucer");
                }else{
                    Jukebox.loop("smallsaucer");
                }
            }
            Jukebox.playBackgroundMusic();
        }else if(gamePaused && GameKeys.isPressed(GameKeys.ESCAPE)){
            gsm.setState(GameStateManager.MENU);
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        font.dispose();
        pausedFont.dispose();
        Jukebox.stopBackgroundMusic();
    }
}
