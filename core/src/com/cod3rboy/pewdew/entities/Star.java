package com.cod3rboy.pewdew.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.cod3rboy.pewdew.PewDew;

public class Star extends SpaceObject{
    private boolean remove;
    private Color starColor;
    private float radius;
    private float dRadius;

    private boolean incrementRadius;
    private boolean decrementRadius;

    public Star(float centerX, float centerY, float direction){
        this.x = centerX;
        this.y = centerY;
        this.radians = direction;
        this.speed = 150;

        this.dx = speed * MathUtils.cos(this.radians);
        this.dy = speed * MathUtils.sin(this.radians);

        this.remove = false;
        this.starColor = new Color(1,1,1,1);

        this.radius = 0.5f;
        this.dRadius = 0.25f;
        this.incrementRadius = true;
        this.decrementRadius = false;

    }
    public void setColor(float r, float g, float b, float a){
        this.starColor.set(r,g,b,a);
    }

    public void setSpeed(float speed){
        this.speed = speed;
        this.dx = speed * MathUtils.cos(this.radians);
        this.dy = speed * MathUtils.sin(this.radians);
    }

    public void setRadius(float radius){
        this.radius = radius;
    }
    public void setDeltaRadius(float deltaRadius){
        this.dRadius = deltaRadius;
    }
    public void setIncrementRadius(boolean bool){
        this.incrementRadius = bool;
        this.decrementRadius = !bool;
    }
    public void setDecrementRadius(boolean bool){
        this.decrementRadius = bool;
        this.incrementRadius = !bool;
    }

    public boolean shouldRemove(){ return this.remove; }

    public void update(float dt){
        this.x += this.dx * dt;
        this.y += this.dy * dt;

        if(this.incrementRadius && !this.decrementRadius)
            this.radius += dRadius * dt;
        else if(this.decrementRadius && !this.incrementRadius)
            this.radius -= dRadius * dt;

        if(this.x > PewDew.WIDTH
                || this.y > PewDew.HEIGHT
                || this.x < 0
                || this.y < 0)
            this.remove = true;
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(this.starColor);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.circle(this.x, this.y, radius);
        sr.end();
    }
}
