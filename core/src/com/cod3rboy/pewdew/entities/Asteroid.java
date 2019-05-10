package com.cod3rboy.pewdew.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Asteroid extends SpaceObject {
    private int type;
    public static final int SMALL = 0;
    public static final int MEDIUM = 1;
    public static final int LARGE = 2;

    private int numPoints;
    private float[] dists;

    private boolean remove;

    private int score;

    private float flashTime = 0f;
    private float flashTimer = 0.1f;

    private Color asteroidColor;

    public Asteroid(float x, float y, int type){
        this.x = x;
        this.y = y;
        this.type = type;

        if(type == SMALL){
            numPoints = 8;
            width = height = 30;
            speed = MathUtils.random(70, 100);
            score = 200;
        }else if(type == MEDIUM) {
            numPoints = 10;
            width = height = 45;
            speed = MathUtils.random(50,60);
            score = 150;
        }else if(type == LARGE){
            numPoints = 12;
            width = height = 70;
            speed = MathUtils.random(20, 30);
            score = 100;
        }

        rotationSpeed = MathUtils.random(-1, 1);
        radians = MathUtils.random( 2 * MathUtils.PI);
        dx = MathUtils.cos(radians) * speed;
        dy = MathUtils.sin(radians) * speed;

        shapex = new float[numPoints];
        shapey = new float[numPoints];
        dists = new float[numPoints];

        int radius = width / 2;
        for(int i=0; i<numPoints; i++) dists[i] = MathUtils.random(radius/2, radius);

        setShape();
        asteroidColor = new Color(1,1,1,1);
    }

    private void setShape(){
        float angle = 0;
        for(int i=0; i < numPoints; i++){
            shapex[i] = x + MathUtils.cos(angle + radians) * dists[i];
            shapey[i] = y + MathUtils.sin(angle + radians) * dists[i];
            angle += 2 * MathUtils.PI / numPoints;
        }
    }

    public int getType() {
        return type;
    }

    public int getScore() { return score; }

    public boolean shouldRemove(){
        return remove;
    }

    public void update(float dt){
        x += dx * dt;
        y += dy * dt;

        radians += rotationSpeed * dt;
        setShape();

        if(flashTimer > flashTime) {
            flashTimer = 0;
            asteroidColor.set(MathUtils.random(1f),MathUtils.random(1f),MathUtils.random(1f),1);
        }
        else flashTimer += dt;
        wrap();
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(asteroidColor);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for(int i=0, j=shapex.length-1; i<shapex.length; j = i++){
            sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
        }
        sr.end();
    }
}
