package com.cod3rboy.pewdew.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Bullet extends SpaceObject {
    private float lifeTime;
    private float lifeTimer;
    private boolean remove;
    private boolean wrap;
    private float flashTimer = 0f;
    private float flashTime = 0.05f;

    private Color bulletColor;

    private boolean isFlashingBullet;

    public Bullet(float x, float y, float radians, boolean isFlashing){
        this.x = x;
        this.y = y;
        this.radians = radians;

        this.speed = 350;

        width = height = 3;

        lifeTimer = 0;
        lifeTime = .8f;

        bulletColor = new Color(1,1,1,1);
        isFlashingBullet = isFlashing;
        wrap = true;
    }

    public void setRadius(int radius){
        this.width = this.height = radius;
    }
    public boolean shouldRemove(){ return remove; }
    public void setBulletColor(Color color){ this.bulletColor = color; }
    public void setWrap(boolean doWrap){
        wrap = doWrap;
    }
    public void update(float dt){
        dx = MathUtils.cos(radians) * speed;
        dy = MathUtils.sin(radians) * speed;
        x += dx * dt;
        y += dy * dt;
        if(wrap) wrap();
        lifeTimer += dt;
        if(lifeTimer > lifeTime){
            remove = true;
        }

        if(isFlashingBullet) {
            flashTimer += dt;
            if (flashTimer > flashTime) {
                flashTimer = 0;
                bulletColor.set(MathUtils.random(1f), MathUtils.random(1f), MathUtils.random(1f), 1);
            }
        }
    }

    public void setLifeTime(float time){
        this.lifeTime = time;
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(bulletColor);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.circle(x - width/2, y - height/2, width);
        sr.end();
    }
}
