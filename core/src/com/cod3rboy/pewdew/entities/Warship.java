package com.cod3rboy.pewdew.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.managers.Jukebox;

import java.util.ArrayList;

public class Warship extends SpaceObject {
    private Rectangle bone;
    private Rectangle topWing;
    private Rectangle midWing;
    private Rectangle bottomWing;
    private Player player;
    private ArrayList<Bullet> bullets;
    private int direction;
    private boolean shouldRemove;
    public static final int DIRECTION_LEFT = 100;
    public static final int DIRECTION_UP = 101;
    public static final int DIRECTION_RIGHT = 102;
    public static final int DIRECTION_DOWN = 103;
    private float health;
    private float MAX_HEALTH = 100;
    private float shootTimer = 0;
    private float shootTime = 1.5f;
//    private long score = 5000; // 5000 kill points
    public static int damageScore = 100;
    private int score;
    private Color color;
    private Color thrusterColor;
    private float flashTimer = 0;
    private float flashTime = 0.01f;
    public Warship(int direction, float x, float y, Player player, ArrayList<Bullet> bullets){
        this.color = new Color(1, 0,0,1);
        thrusterColor = new Color(1, .21569f, .07451f, 1);
        this.direction = direction;
        this.player = player;
        this.bullets = bullets;
        this.x = x; // Center x
        this.y = y; // Center y

        this.bone = new Rectangle();
        this.topWing = new Rectangle();
        this.midWing = new Rectangle();
        this.bottomWing = new Rectangle();

        shouldRemove = false;

        this.bone.width = 8;
        this.bone.height = 80;
        this.topWing.width = 50;
        this.topWing.height = 8;
        this.midWing.width = 70;
        this.midWing.height = 8;
        this.bottomWing.width = 50;
        this.bottomWing.height = 8;

        switch(direction){
            case DIRECTION_LEFT:
                this.bone.width = 80;
                this.bone.height = 8;
                this.topWing.width = 8;
                this.topWing.height = 50;
                this.midWing.width = 8;
                this.midWing.height = 70;
                this.bottomWing.width = 8;
                this.bottomWing.height = 50;
                radians = MathUtils.PI;

                this.width = (int) bone.width;
                this.height = (int) midWing.height;
                break;
            case DIRECTION_RIGHT:
                this.bone.width = 80;
                this.bone.height = 8;
                this.topWing.width = 8;
                this.topWing.height = 50;
                this.midWing.width = 8;
                this.midWing.height = 70;
                this.bottomWing.width = 8;
                this.bottomWing.height = 50;

                this.width = (int) bone.width;
                this.height = (int) midWing.height;
                radians = MathUtils.PI2;
                break;
            case DIRECTION_UP:

                this.width = (int) midWing.width;
                this.height = (int) bone.height;
                radians = MathUtils.PI/2f;
                break;
            case DIRECTION_DOWN:

                this.width = (int) midWing.width;
                this.height = (int) bone.height;
                radians = 3 * MathUtils.PI/2f;
                break;
        }

        speed = 50;

        this.score = damageScore;

        this.health = MAX_HEALTH;
    }

    public void setCenter(float x, float y){
        this.x = x;
        this.y = y;
        calculateBounds();
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void reduceHealth(float amount){
        if(direction == DIRECTION_RIGHT || direction == DIRECTION_LEFT){
            health -= amount/2;
        }else{
            health -= amount;
        }

    }
    public void setColor(float r, float g, float b){
        this.color.set(r,g,b,1);
    }

    public float getHealth() { return health; }
    public float getMaxHealth() { return MAX_HEALTH; }
//    public long getScore() { return score; }
    public void setDamageScore(int score) { this.score = score; }
    public int getDamageScore() { return this.score; }

    public float getSpeed() { return this.speed; }

    public Color getColor() { return new Color(color); }

    private void calculateBounds(){
        // Mathematically calculate bounds of the warship shape depending upon direction type
        if(direction == DIRECTION_UP) {
            // BONE of Warship
            bone.x = x - bone.width/2;
            bone.y = y - bone.height/2;
            // TOP WING of Warship
            topWing.x = x - topWing.width/2;
            topWing.y = y + bone.height/4 - topWing.height/2;
            // MID WING of Warship
            midWing.x = x - midWing.width/2;
            midWing.y = y - midWing.height/2;
            // BOTTOM WING of Warship
            bottomWing.x = x - bottomWing.width/2;
            bottomWing.y = y - bone.height/4 - bottomWing.height/2;

        }else if(direction == DIRECTION_RIGHT){
            // BONE of Warship
            bone.x = x - bone.width/2;
            bone.y = y - bone.height/2;
            // TOP WING of Warship
            topWing.x = x + bone.width/4 - topWing.width/2;
            topWing.y = y  - topWing.height/2;
            // MID WING of Warship
            midWing.x = x - midWing.width/2;
            midWing.y = y - midWing.height/2;
            // BOTTOM WING of Warship
            bottomWing.x = x -  bone.width/4 - bottomWing.width/2;
            bottomWing.y = y - bottomWing.height/2;
        }else if(direction == DIRECTION_DOWN){
            // BONE of Warship
            bone.x = x - bone.width/2;
            bone.y = y - bone.height/2;
            // TOP WING of Warship
            topWing.x = x - topWing.width/2;
            topWing.y = y - bone.height/4 - topWing.height/2;
            // MID WING of Warship
            midWing.x = x - midWing.width/2;
            midWing.y = y - midWing.height/2;
            // BOTTOM WING of Warship
            bottomWing.x = x - bottomWing.width/2;
            bottomWing.y = y + bone.height/4 - bottomWing.height/2;

        }else if(direction == DIRECTION_LEFT){
            // BONE of Warship
            bone.x = x - bone.width/2;
            bone.y = y - bone.height/2;
            // TOP WING of Warship
            topWing.x = x - bone.width/4 - topWing.width/2;
            topWing.y = y  - topWing.height/2;
            // MID WING of Warship
            midWing.x = x - midWing.width/2;
            midWing.y = y - midWing.height/2;
            // BOTTOM WING of Warship
            bottomWing.x = x +  bone.width/4 - bottomWing.width/2;
            bottomWing.y = y - bottomWing.height/2;
        }
    }

    @Override
    public boolean intersects(SpaceObject other) {
        float sx[] = other.getShapex();
        float sy[] = other.getShapey();
        for(int i=0; i<sx.length; i++){
            if(contains(sx[i], sy[i])) return true;
        }
        return false;
    }

    @Override
    public boolean contains(float x, float y) {
        return bone.contains(x, y) || topWing.contains(x, y) || midWing.contains(x, y) || bottomWing.contains(x, y);
    }

    public boolean shouldRemove(){
        return shouldRemove;
    }

    public void update(float dt){

        flashTimer += dt;
        if(flashTimer > flashTime){
            flashTimer = 0;
            // Choose fire color
            if(MathUtils.random() < 0.5) thrusterColor.set(1, .21569f, .07451f, 1);
            else thrusterColor.set(1, .894f, .1412f, 1);
        }

        dx = speed * MathUtils.cos(radians) * dt;
        dy = speed * MathUtils.sin(radians) * dt;
        x += dx;
        y += dy;

        calculateBounds();
        switch (direction){
            case DIRECTION_LEFT:
                if(bone.x + bone.width < 0) shouldRemove = true;
                break;
            case DIRECTION_RIGHT:
                if(bone.x > PewDew.WIDTH) shouldRemove = true;
                break;
            case DIRECTION_UP:
                if(bone.y > PewDew.HEIGHT) shouldRemove = true;
                break;
            case DIRECTION_DOWN:
                if(bone.y+bone.height < 0) shouldRemove = true;
                break;
        }


        if(player.isHit()) return;
        // add enemy bullets
        shootTimer += dt;
        if(shootTimer > shootTime){
            shootTimer = 0;
            // Add bullets for each end of all wings
            Bullet b1, b2, b3, b4, b5, b6;
            switch (direction){
                case DIRECTION_LEFT:
                case DIRECTION_RIGHT:
                    // Top Wing Bullets
                    b1 = new Bullet(topWing.x + topWing.width/2, topWing.y, 3 * MathUtils.PI /2, false);
                    b2 = new Bullet(topWing.x + topWing.width/2, topWing.y + topWing.height, MathUtils.PI/2, false);
                    // Mid Wing Bullets
                    b3 = new Bullet(midWing.x + midWing.width/2, midWing.y, 3 * MathUtils.PI /2, false);
                    b4 = new Bullet(midWing.x + midWing.width/2, midWing.y + midWing.height, MathUtils.PI /2, false);
                    // Bottom Wing Bullets
                    b5 = new Bullet(bottomWing.x + bottomWing.width/2, bottomWing.y, 3 * MathUtils.PI /2, false);
                    b6 = new Bullet(bottomWing.x + bottomWing.width/2, bottomWing.y + bottomWing.height, MathUtils.PI /2, false);
                    break;
                default: // For Top and Bottom direction
                    // Top Wing Bullets
                    b1 = new Bullet(topWing.x, topWing.y + topWing.height/2, MathUtils.PI, false);
                    b2 = new Bullet(topWing.x + topWing.width, topWing.y + topWing.height/2, 0, false);
                    // Mid Wing Bullets
                    b3 = new Bullet(midWing.x,  midWing.y + midWing.height/2, MathUtils.PI, false);
                    b4 = new Bullet(midWing.x + midWing.width, midWing.y + midWing.height/2, 0, false);
                    // Bottom Wing Bullets
                    b5 = new Bullet(bottomWing.x, bottomWing.y + bottomWing.height/2, MathUtils.PI, false);
                    b6 = new Bullet(bottomWing.x + bottomWing.width, bottomWing.y + bottomWing.height/2, 0, false);
            }
            // Set Bullets color
            b1.setBulletColor(this.color);
            b2.setBulletColor(this.color);
            b3.setBulletColor(this.color);
            b4.setBulletColor(this.color);
            b5.setBulletColor(this.color);
            b6.setBulletColor(this.color);

            // bullets can go out of screen
            b1.setWrap(false);
            b2.setWrap(false);
            b3.setWrap(false);
            b4.setWrap(false);
            b5.setWrap(false);
            b6.setWrap(false);

            // set 1.2sec lifetime of bullets
            b1.setLifeTime(1.2f);
            b2.setLifeTime(1.2f);
            b3.setLifeTime(1.2f);
            b4.setLifeTime(1.2f);
            b5.setLifeTime(1.2f);
            b6.setLifeTime(1.2f);

            bullets.add(b1);
            bullets.add(b2);
            bullets.add(b3);
            bullets.add(b4);
            bullets.add(b5);
            bullets.add(b6);
            Jukebox.play("warship-shoot");
        }
    }

    public void draw(ShapeRenderer sr){

        // Draw Wings and Health bar here here
        sr.setColor(this.color);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        // Draw Top Wing
        sr.rect(topWing.x, topWing.y, topWing.width, topWing.height);
        // Draw Middle Wing
        sr.rect(midWing.x, midWing.y, midWing.width, midWing.height);
        // Draw Bottom Wing
        sr.rect(bottomWing.x, bottomWing.y, bottomWing.width, bottomWing.height);
        // Draw health bar
        float fillX=0, fillY=0, fillW=0, fillH=0;
        switch (direction){
            case DIRECTION_UP:
                fillX = bone.x;
                fillY = bone.y;
                fillW = bone.width;
                fillH = bone.height * (health * 1f /MAX_HEALTH);
                break;
            case DIRECTION_DOWN:
                fillX = bone.x;
                fillY = bone.y;
                fillW = bone.width;
                fillH = bone.height * (health * 1f /MAX_HEALTH);
                fillY += bone.height - fillH;
                break;
            case DIRECTION_RIGHT:
                fillX = bone.x;
                fillY = bone.y;
                fillW = bone.width * (health * 1f /MAX_HEALTH);
                fillH = bone.height;
                break;
            case DIRECTION_LEFT:
                fillX = bone.x;
                fillY = bone.y;
                fillW = bone.width * (health * 1f /MAX_HEALTH);
                fillH = bone.height;
                fillX += bone.width - fillW;
        }
        sr.rect(fillX, fillY, fillW, fillH);
        sr.end();

        // Draw Bone and Fire thruster
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.rect(bone.x, bone.y, bone.width, bone.height);
        float vOuter[] = new float[6];
        float vInner[] = new float[6];
        switch(direction){
            case DIRECTION_RIGHT:
                vInner[0] = vOuter[0] = bone.x;
                vInner[1] = vOuter[1] = bone.y;
                vInner[2] = vOuter[2] = bone.x;
                vInner[3] = vOuter[3] = bone.y + bone.height;
                vInner[4] = vOuter[4] = vOuter[0] - 15;
                vInner[5] = vOuter[5] = bone.y + bone.height/2;
                vInner[4] = vInner[4] + 8;
                break;
            case DIRECTION_LEFT:
                vInner[0] = vOuter[0] = bone.x + bone.width;
                vInner[1] = vOuter[1] = bone.y;
                vInner[2] = vOuter[2] = bone.x + bone.width;
                vInner[3] = vOuter[3] = bone.y + bone.height;
                vInner[4] = vOuter[4] = vOuter[0] + 15;
                vInner[5] = vOuter[5] = bone.y + bone.height/2;
                vInner[4] = vInner[4] - 8;
                break;
            case DIRECTION_UP:
                vInner[0] = vOuter[0] = bone.x;
                vInner[1] = vOuter[1] = bone.y;
                vInner[2] = vOuter[2] = bone.x + bone.width;
                vInner[3] = vOuter[3] = bone.y;
                vInner[4] = vOuter[4] = bone.x + bone.width/2;
                vInner[5] = vOuter[5] = vOuter[1] - 15;
                vInner[5] = vInner[5] + 8;
                break;
            case DIRECTION_DOWN:
                vInner[0] = vOuter[0] = bone.x;
                vInner[1] = vOuter[1] = bone.y + bone.height;
                vInner[2] = vOuter[2] = bone.x + bone.width;
                vInner[3] = vOuter[3] = bone.y + bone.height;
                vInner[4] = vOuter[4] = bone.x + bone.width/2;
                vInner[5] = vOuter[5] = vOuter[1] + 15;
                vInner[5] = vInner[5] - 8;
                break;
        }
        sr.setColor(thrusterColor.r, thrusterColor.g, thrusterColor.b , 1);
        sr.polygon(vOuter);
        sr.polygon(vInner);
        sr.end();
    }
}
