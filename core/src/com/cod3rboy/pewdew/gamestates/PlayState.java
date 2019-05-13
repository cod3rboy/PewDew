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
import com.cod3rboy.pewdew.entities.Bullet;
import com.cod3rboy.pewdew.entities.FlyingSaucer;
import com.cod3rboy.pewdew.entities.Particle;
import com.cod3rboy.pewdew.entities.Player;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Jukebox;
import com.cod3rboy.pewdew.managers.Save;
import com.cod3rboy.pewdew.ui.DualStickController;

import java.util.ArrayList;

public class PlayState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private BitmapFont font;
    private BitmapFont pausedFont;
    private BitmapFont exitFont;
    private Rectangle exitBounds;
    private GlyphLayout gLayout;

    // Cannon button bounds
    private Rectangle cannonBtnBounds;
    private BitmapFont cannonBtnFont;

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
    private boolean firstTouched;

    private Vector3 touchPoint;

    private float slomoFactor;

    // Game Controller UI
    private DualStickController controller;

    private float shootTimer = 0;
    private final float shootTime = 0.15f;

    public PlayState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        sr = new ShapeRenderer();
        sb = new SpriteBatch();

        controller = new DualStickController(
                new Vector2(120, 120),
                new Vector2(PewDew.WIDTH-120, 120)
        );

        controller.setHandleTouch(true);
        controller.setRightStickFixed(GameStateManager.getCannonFixed());

        gamePaused = false;
        firstTouched = false;

        touchPoint = new Vector3();

        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;
        font = gen.generateFont(param);
        param.size = 45;
        pausedFont = gen.generateFont(param);
        param.size = 25;
        param.color = Color.RED;
        exitFont = gen.generateFont(param);
        param.size = 25;
        param.color = Color.YELLOW;
        cannonBtnFont = gen.generateFont(param);

        gLayout = new GlyphLayout();

        exitBounds = new Rectangle();

        cannonBtnBounds = new Rectangle();

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

        slomoFactor = 1;


        if(!GameStateManager.getMusicSetting()) {
            // Play background music deepspace
            Jukebox.playBackgroundMusic("deepspace");
        }
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
            float x = MathUtils.random(PewDew.WIDTH);
            float y = MathUtils.random(PewDew.HEIGHT);
            float dx = x - player.getX();
            float dy = y - player.getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            while (dist < 100) {
                x = MathUtils.random(PewDew.WIDTH);
                y = MathUtils.random(PewDew.HEIGHT);
                dx = x - player.getX();
                dy = y - player.getY();
                dist = (float) Math.sqrt(dx * dx + dy * dy);
            }
            asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
        }
    }

    @Override
    public void update(float dt) {
        dt = dt * slomoFactor;

        // get user input
        handleInput();

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
                Jukebox.stopAllBackgroundMusic();
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
                if(!isGamePaused()) {
                    if (flyingSaucer.getType() == FlyingSaucer.LARGE) {
                        Jukebox.loop("largesaucer", 0.7f);
                    } else if (flyingSaucer.getType() == FlyingSaucer.SMALL) {
                        Jukebox.loop("smallsaucer", 0.7f);
                    }
                }
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

        if(!player.isHit() && bgTimer >= currentDelay && !GameStateManager.getMusicSetting() && !isGamePaused()){
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
        sr.setProjectionMatrix(PewDew.cam.combined);
        sb.setProjectionMatrix(PewDew.cam.combined);

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
        font.setColor(1, 1, 1, 1);
        sb.begin();
        font.draw(sb, String.format("Score : %d",player.getScore()), 40, 420);
        font.draw(sb, "Lives : ", 40, 390);

        // draw game paused text
        if(isGamePaused()) {
            // Draw Game Paused Text
            pausedFont.setColor(0,1,0,1);
            gLayout.setText(pausedFont, (!firstTouched) ? "Tap to Play" : "Play to Resume");
            pausedFont.draw(sb, gLayout, (PewDew.WIDTH - gLayout.width)/2, PewDew.HEIGHT * .7f);

            if(firstTouched){
                // Draw Exit Button
                gLayout.setText(exitFont, "Exit");
                exitBounds.width = gLayout.width;
                exitBounds.height = gLayout.height;
                exitBounds.x = (PewDew.WIDTH - exitBounds.width)/2;
                exitBounds.y = 20;
                exitBounds.x += 20;
                exitBounds.y += 20;
                exitFont.draw(sb,gLayout,exitBounds.x, exitBounds.y + exitBounds.height);
                // Update bounds without paddings
                exitBounds.x -= 20;
                exitBounds.y -= 20;
                exitBounds.width += 40;
                exitBounds.height += 40;
            }else{
                // Draw Cannon Button
                gLayout.setText(cannonBtnFont,
                        (
                                controller.isRightStickFixed() ?
                                String.format("%-16s","Cannon : TIP") : "Cannon : R-Stick"
                        )
                );
                cannonBtnBounds.width = gLayout.width;
                cannonBtnBounds.height = gLayout.height;
                cannonBtnBounds.x = PewDew.WIDTH - cannonBtnBounds.width - 20;
                cannonBtnBounds.y = PewDew.HEIGHT - cannonBtnBounds.height - 20;
                cannonBtnFont.draw(sb, gLayout, cannonBtnBounds.x , cannonBtnBounds.y + cannonBtnBounds.height);
                // Set Touch padding
                cannonBtnBounds.x -= 10;
                cannonBtnBounds.y -= 10;
                cannonBtnBounds.width += 20;
                cannonBtnBounds.height += 20;
            }
        }

        sb.end();

        // Draw Exit button bounds
        if(isGamePaused() && firstTouched) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(1,0,0,1);
            sr.rect(exitBounds.x, exitBounds.y, exitBounds.width, exitBounds.height);
            sr.end();
        }

        // draw lives
        for(int i=0; i<player.getLives(); i++){
            hudPlayer.setPosition(140 + i * 20, 380);
            hudPlayer.draw(sr);
        }

        // draw controller
        controller.drawController(sr);
    }

    @Override
    public void handleInput() {
        if(player.isHit() && !isGamePaused()) return;

        if(!isGamePaused() && !Gdx.input.isTouched()){
            pauseGame();
            controller.startSticksAnimation();
        }else if(isGamePaused() && Gdx.input.isTouched()){
            PewDew.cam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if(firstTouched){
                if(exitBounds.contains(touchPoint.x, touchPoint.y)){
                    Jukebox.play("menuselect");
                    gsm.setState(GameStateManager.MENU); // Go to main menu
                    return;
                }
            }
            if(!firstTouched && cannonBtnBounds.contains(touchPoint.x, touchPoint.y)){
                if(Gdx.input.justTouched()) {
                    Jukebox.play("menuselect");
                    controller.setRightStickFixed(!controller.isRightStickFixed());
                    GameStateManager.saveCannonFixed(controller.isRightStickFixed());
                }
                return;
            }
            firstTouched = true; // touched for first time
            resumeGame();
            controller.stopSticksAnimation();
        }

        player.setRotation(controller.getLeftStickDirection());
        if(controller.isLeftStickTouching()){
            float percent = controller.getLeftStickDistance()/controller.getLeftStickMaxDistance();
            if(percent > 0.2f) player.setSpeed( percent * player.getMaxSpeed());
            else player.setSpeed(0);
        }else{
            player.setSpeed(0);
        }
        if(controller.isRightStickTouching()) { // Shoot Button Pressed
            shootTimer += Gdx.graphics.getDeltaTime();
            if(shootTimer > shootTime) {
                shootTimer = 0;
                if(!controller.isRightStickFixed()) player.setBulletRotation(controller.getRightStickDirection());
                else player.setBulletRotation(player.getRadians());
                player.shoot();
            }
        }
    }

    private void pauseGame(){
        gamePaused = true;
        slomoFactor = 0.05f;
        Jukebox.stopAll();
    }
    private void resumeGame(){
        gamePaused = false;
        slomoFactor = 1;
        // Restart previous stopped flying saucer music loop
        if(flyingSaucer != null){
            int type = flyingSaucer.getType();
            if(type == FlyingSaucer.LARGE){
                Jukebox.loop("largesaucer");
            }else{
                Jukebox.loop("smallsaucer");
            }
        }
    }
    private boolean isGamePaused() {
        return gamePaused;
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        controller.dispose();
        font.dispose();
        pausedFont.dispose();
        cannonBtnFont.dispose();
    }
}
