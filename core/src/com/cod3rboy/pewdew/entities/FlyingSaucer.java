package com.cod3rboy.pewdew.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.managers.Jukebox;

import java.util.ArrayList;

public class FlyingSaucer extends SpaceObject {
    private ArrayList<Bullet> bullets;
    private int type;
    public static final int LARGE = 0;
    public static final int SMALL = 1;

    private int score;

    private float fireTimer;
    private float fireTime;

    private Player player;

    private float pathTimer;
    private float pathTime1;
    private float pathTime2;

    private int direction;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    private boolean remove;

    private Color color;

    public FlyingSaucer(int type, int direction, Player player, ArrayList<Bullet> bullets){
        this.type = type;
        this.direction = direction;
        this.player = player;
        this.bullets = bullets;

        speed = 70;
        if(direction == LEFT) {
            dx = -speed;
            x = PewDew.WIDTH;
        }else if(direction == RIGHT){
            dx = speed;
            x = 0;
        }

        y = MathUtils.random(PewDew.HEIGHT);
        shapex = new float[6];
        shapey = new float[6];
        setShape();

        if(type == LARGE){
            score = 500;
            Jukebox.loop("largesaucer");
        }else if(type == SMALL){
            score = 1000;
            Jukebox.loop("smallsaucer");
        }
        fireTimer = 0;
        fireTime = 1;

        pathTimer = 0;
        pathTime1 = 2;
        pathTime2 = pathTime1 + 2;

        color = new Color(1,MathUtils.random(1f),MathUtils.random(1f),1);

    }
    private void setShape(){
        if(type == LARGE){
            shapex[0] = x - 10;
            shapey[0] = y;

            shapex[1] = x - 3;
            shapey[1] = y - 5;

            shapex[2] = x + 3;
            shapey[2] = y - 5;

            shapex[3] = x + 10;
            shapey[3] = y;

            shapex[4] = x + 3;
            shapey[4] = y + 5;

            shapex[5] = x - 3;
            shapey[5] = y + 5;

        }else if(type == SMALL){
            shapex[0] = x - 6;
            shapey[0] = y;

            shapex[1] = x - 2;
            shapey[1] = y - 3;

            shapex[2] = x + 2;
            shapey[2] = y - 3;

            shapex[3] = x + 6;
            shapey[3] = y;

            shapex[4] = x + 2;
            shapey[4] = y + 3;

            shapex[5] = x - 2;
            shapey[5] = y + 3;

        }
    }

    public int getScore() { return score; }
    public boolean shouldRemove() { return remove; }
    public Color getColor() { return color; }

    public int getType(){ return this.type; }

    public void update(float dt){
        // fire
        if(!player.isHit()){
            fireTimer += dt;
            if(fireTimer > fireTime){
                fireTimer = 0;
                if(type == LARGE){
                    radians = MathUtils.random(2 * MathUtils.PI);
                }else if(type == SMALL){
                    radians = MathUtils.atan2(player.getY() - y, player.getX() - x);
                }
                Bullet b = new Bullet(x, y, radians, false);
                b.setBulletColor(this.getColor());
                bullets.add(b);
                Jukebox.play("saucershoot");
            }
        }

        // move along path
        pathTimer += dt;
        if(pathTimer < pathTime1){
            dy = 0;
        }
        // move downward
        if(pathTimer > pathTime1 && pathTime1 < pathTime2){
            dy = -speed;
        }

        // move to the end of the screen
        if(pathTimer > pathTime1 + pathTime2){
            dy = 0;
        }

        x += dx * dt;
        y += dy * dt;

        // Screen wrap
        if(y < 0) y = PewDew.HEIGHT;

        // Set shape
        setShape();


        // Check if remove
        if((direction == RIGHT && x > PewDew.WIDTH) || (direction == LEFT && x < 0)) {
            remove = true;
        }

    }

    public void draw(ShapeRenderer sr){
        sr.setColor(color);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for(int i=0, j=shapex.length -1 ; i< shapex.length; j = i++){
            sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
        }
        sr.line(shapex[0], shapey[0], shapex[3], shapey[3]);
        sr.end();
    }
}