package com.cod3rboy.pewdew.entities;

import com.cod3rboy.pewdew.PewDew;

public class SpaceObject {

    protected float x;
    protected float y;

    protected float dx;
    protected float dy;

    protected float radians;
    protected float speed;
    protected float rotationSpeed;

    protected int width;
    protected int height;

    protected float[] shapex;
    protected float[] shapey;

    protected void wrap() {
        if (x < 0) x = PewDew.WIDTH;
        if (x > PewDew.WIDTH) x = 0;
        if (y < 0) y = PewDew.HEIGHT;
        if (y > PewDew.HEIGHT) y = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float[] getShapex() {
        return shapex;
    }

    public float[] getShapey() {
        return shapey;
    }

    public void setPosition(float x, float y) {
       this.x = x;
       this.y = y;
    }

    public void setSpeed(float speed){
        this.speed = speed;
    }

    public float getRadians() { return radians; }

    public boolean intersects(SpaceObject other){
        float[] sx = other.getShapex();
        float[] sy = other.getShapey();
        for(int i=0; i<sx.length; i++){
            if(contains(sx[i], sy[i])) return true;
        }
        return false;
    }

    public boolean contains(float x, float y) {
        boolean b = false;
        for (int i = 0, j = shapex.length - 1; i < shapex.length; j = i++) {
            if ((shapey[i] > y) != (shapey[j] > y)
                    && (x < (shapex[j] - shapex[i])
                    * (y - shapey[i]) / (shapey[j] - shapey[i])
                    + shapex[i])) {
                b = !b;
            }
        }
        return b;
    }
}
