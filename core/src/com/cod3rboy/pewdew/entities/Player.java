package com.cod3rboy.pewdew.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.entities.powerups.*;
import com.cod3rboy.pewdew.managers.Jukebox;

import java.util.ArrayList;

public class Player extends SpaceObject implements PowerUp.IPowerable {

    private int MAX_BULLETS = 80;
    private ArrayList<Bullet> bullets;
    private float shootTime =  0.15f;
    private float bulletLifeTime = .8f;

    private float[] flamex;
    private float[] flamey;

    private boolean thrustersOn;

    // Unit pixels per second
    private float maxSpeed;
    private final float MAX_SPEED_PERCENT = 0.9f;


    private boolean hit;
    private boolean dead;

    private Line2D[] hitLines;
    private Vector2[] hitLinesVector;
    private float hitTimer;
    private float hitTime;

    private long score;
    private int extraLives;
    private long requiredScore;


    private float bulletRadians;

    private boolean shield;

    // Timer to initially apply forcefield to player for 2 sec
    private float forceFieldTimer = 0;
    private boolean forceField = true;


    public Player(ArrayList<Bullet> bullets) {
        this.bullets = bullets;

        x = PewDew.WIDTH / 2f;
        y = PewDew.HEIGHT / 2f;

        //maxSpeed = 300;
        maxSpeed = 180;

        shapex = new float[4];
        shapey = new float[4];
        flamex = new float[3];
        flamey = new float[3];
        thrustersOn = false;

        radians = MathUtils.PI / 2;
        bulletRadians = radians;

        rotationSpeed = 3;

        hit = false;
        hitTimer = 0;
        hitTime = 2;

        score = 0;
        extraLives = 3;
        requiredScore = 10000;

        shield = false;
    }

    private void setShape() {
        shapex[0] = x + MathUtils.cos(radians) * 10;
        shapey[0] = y + MathUtils.sin(radians) * 10;

        shapex[1] = x + MathUtils.cos(radians - 4 * MathUtils.PI / 5) * 15;
        shapey[1] = y + MathUtils.sin(radians - 4 * MathUtils.PI / 5) * 15;

        shapex[2] = x + MathUtils.cos(radians + MathUtils.PI) * 5;
        shapey[2] = y + MathUtils.sin(radians + MathUtils.PI) * 5;

        shapex[3] = x + MathUtils.cos(radians + 4 * MathUtils.PI / 5) * 15;
        shapey[3] = y + MathUtils.sin(radians + 4 * MathUtils.PI / 5) * 15;
    }

    private void setFlame() {
        flamex[0] = x + MathUtils.cos(radians - 5 * MathUtils.PI / 6) * 6;
        flamey[0] = y + MathUtils.sin(radians - 5 * MathUtils.PI / 6) * 6;

        flamex[1] = x + MathUtils.cos(radians - MathUtils.PI) * 20;
        flamey[1] = y + MathUtils.sin(radians - MathUtils.PI) * 20;

        flamex[2] = x + MathUtils.cos(radians + 5 * MathUtils.PI / 6) * 6;
        flamey[2] = y + MathUtils.sin(radians + 5 * MathUtils.PI / 6) * 6;
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        if (this.speed > maxSpeed) this.speed = maxSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setRotation(float radians) {
        this.radians = radians;
    }

    public void setBulletRotation(float radians) {
        this.bulletRadians = radians;
    }

    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        setShape();
    }

    public boolean isHit() {
        return hit;
    }

    public boolean isDead() {
        return dead;
    }

    public void reset() {
        x = PewDew.WIDTH / 2;
        y = PewDew.HEIGHT / 2;
        setShape();
        hit = dead = false;
        forceField = true;
        forceFieldTimer = 0;
    }

    public long getScore() {
        return score;
    }

    public int getLives() {
        return extraLives;
    }

    public float getShootTime() { return shootTime; }

    public void loseLife() {
        extraLives--;
    }

    public void incrementScore(long l) {
        score += l;
    }

    public void shoot() {
        if (bullets.size() == MAX_BULLETS) return;
        Bullet b = new Bullet(x, y , bulletRadians, true);
        b.setLifeTime(bulletLifeTime);
        bullets.add(b);
        //bullets.add(new Bullet(x,y, radians, true));
        Jukebox.play("shoot", 0.8f);
    }

    public void hit() {
        if (hit) return;
        hit = true;
        dx = 0;
        dy = 0;

        hitLines = new Line2D[4];

        for (int i = 0, j = hitLines.length - 1; i < hitLines.length; j = i++) {
            hitLines[i] = new Line2D(shapex[i], shapey[i], shapex[j], shapey[j]);
        }
        hitLinesVector = new Vector2[4];
        hitLinesVector[0] = new Vector2(
                MathUtils.cos(radians + 1.5f),
                MathUtils.sin(radians + 1.5f)
        );

        hitLinesVector[1] = new Vector2(
                MathUtils.cos(radians - 1.5f),
                MathUtils.sin(radians - 1.5f)
        );
        hitLinesVector[2] = new Vector2(
                MathUtils.cos(radians - 2.8f),
                MathUtils.sin(radians - 2.8f)
        );
        hitLinesVector[3] = new Vector2(
                MathUtils.cos(radians + 2.8f),
                MathUtils.sin(radians + 2.8f)
        );


    }

    public void update(float dt) {

        // Check if hit
        if (hit) {
            hitTimer += dt;
            if (hitTimer > hitTime) {
                dead = true;
                hitTimer = 0;
                // Respawn player
            } else {
                for (int i = 0; i < hitLines.length; i++) {
                    hitLines[i].setLine(
                            hitLines[i].x1() + hitLinesVector[i].x * 10 * dt,
                            hitLines[i].y1() + hitLinesVector[i].y * 10 * dt,
                            hitLines[i].x2() + hitLinesVector[i].x * 10 * dt,
                            hitLines[i].y2() + hitLinesVector[i].y * 10 * dt
                    );
                }
            }
        }

        // Check extra lives
        if (score >= requiredScore) {
            extraLives++;
            requiredScore += 10000;
            Jukebox.play("extralife");
        }

        /*// Turning
        if (left) {
            radians += rotationSpeed * dt;
        } else if (right) {
            radians -= rotationSpeed * dt;
        }*/

        /*// Acceleration
        if (up) {
            dx += MathUtils.cos(radians) * acceleration * dt;
            dy += MathUtils.sin(radians) * acceleration * dt;
            acceleratingTimer += dt;
            if (acceleratingTimer > 0.1f) acceleratingTimer = 0;
        } else {
            acceleratingTimer = 0;
        }

        // Deceleration
        float vec = (float) Math.sqrt(dx * dx + dy * dy);
        if (vec > 0) {
            dx -= (dx / vec) * deceleration * dt;
            dy -= (dy / vec) * deceleration * dt;
        }
        if (vec > maxSpeed) {
            dx = (dx / vec) * maxSpeed;
            dy = (dy / vec) * maxSpeed;
        }*/

        dx = speed * MathUtils.cos(radians);
        dy = speed * MathUtils.sin(radians);

        // set position
        x += dx * dt;
        y += dy * dt;

        // set shape
        setShape();

        // set flame
        float speedPercent = speed / maxSpeed;
        if (speedPercent > MAX_SPEED_PERCENT && !hit) {
            setFlame();
            thrustersOn = true;
        } else {
            thrustersOn = false;
        }

        // update forcefield
        if(forceField){
            forceFieldTimer += dt;
            if(forceFieldTimer > 2){ // exceeds 2 secs
                forceFieldTimer = 0;
                forceField = false;
            }
        }

        // screen wrap
        wrap();
    }

    public void draw(ShapeRenderer sr) {
        sr.setColor(1, 1, 1, 1);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        if (hit) {
            for (int i = 0; i < hitLines.length; i++) {
                sr.line(
                        hitLines[i].x1(),
                        hitLines[i].y1(),
                        hitLines[i].x2(),
                        hitLines[i].y2()
                );
            }
            sr.end();
            return;
        }

        // draw ship
        for (int i = 0, j = shapex.length - 1; i < shapex.length; j = i++) {
            sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
        }

        // draw flames
        if (thrustersOn) {
            boolean colorRed = MathUtils.random() < 0.5;
            // Set thruster color
            if (colorRed) sr.setColor(1, .21569f, .07451f, 1); // Red flame color
            else sr.setColor(1, .894f, .1412f, 1); // Yellow flame color
            for (int i = 0, j = flamex.length - 1; i < flamex.length; j = i++) {
                sr.line(flamex[i], flamey[i], flamex[j], flamey[j]);
            }
        }

        sr.end();

        // draw shield circle
        if(shield || forceField){
            float maxX = shapex[0];
            float minX = shapex[0];
            for(int i=0; i<shapex.length;i++) {
                if(shapex[i] > maxX) maxX = shapex[i];
                else if(shapex[i] < minX) minX = shapex[i];
            }
            float maxY = shapey[0];
            float minY = shapey[0];
            for(int i=0; i<shapey.length;i++) {
                if(shapey[i] > maxY) maxY = shapey[i];
                else if(shapey[i] < minY) minY = shapey[i];
            }
            float xOffset = (maxX - minX)/2;
            float yOffset = (maxY - minY)/2;
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(MathUtils.random(1f), MathUtils.random(1f), MathUtils.random(1f), 1);
            sr.circle(minX + xOffset, minY + yOffset, (xOffset > yOffset) ? xOffset + 10 : yOffset + 10, 50);
            sr.end();
        }

    }



    public void turnOffShield(){
        shield = false;
        forceField = false;
        forceFieldTimer = 0;
    }

    public boolean isShielded(){ return shield || forceField; }

    @Override
    public void powerApplied(int powerid) {
        switch(powerid){
            case ShieldPowerUp.POWER_UP_SHIELD:
                shield = true;
                break;
            case FrenzyPowerUp.POWER_UP_FRENZY:
                shootTime = 0.05f;
                bulletLifeTime = 2f;
                MAX_BULLETS = 200;
                break;
        }
    }

    @Override
    public void powerReset(int powerid) {
        switch(powerid){
            case ShieldPowerUp.POWER_UP_SHIELD:
                shield = false;
                break;
            case FrenzyPowerUp.POWER_UP_FRENZY:
                shootTime = 0.15f;
                bulletLifeTime = 0.8f;
                MAX_BULLETS = 80;
                break;
        }
    }

    @Override
    public boolean powerTouched(float x, float y, float w, float h) {
        float maxX = shapex[0];
        float minX = shapex[0];
        for(int i=0; i<shapex.length;i++) {
            if(shapex[i] > maxX) maxX = shapex[i];
            else if(shapex[i] < minX) minX = shapex[i];
        }
        float maxY = shapey[0];
        float minY = shapey[0];
        for(int i=0; i<shapey.length;i++) {
            if(shapey[i] > maxY) maxY = shapey[i];
            else if(shapey[i] < minY) minY = shapey[i];
        }

        // Use AABB algorithm to detect player/power collision
        return (minX < x+w && maxX > x) && (minY < y+h && maxY > y) && !hit;
    }

    class Line2D {
        private Vector2 point1;
        private Vector2 point2;

        public Line2D(float x1, float y1, float x2, float y2) {
            point1 = new Vector2();
            point2 = new Vector2();
            point1.x = x1;
            point1.y = y1;
            point2.x = x2;
            point2.y = y2;
        }

        public float x1() {
            return point1.x;
        }

        public float x2() {
            return point2.x;
        }

        public float y1() {
            return point1.y;
        }

        public float y2() {
            return point2.y;
        }

        public void setLine(float x1, float y1, float x2, float y2) {
            point1.x = x1;
            point1.y = y1;
            point2.x = x2;
            point2.y = y2;
        }
    }
}
