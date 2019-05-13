package com.cod3rboy.pewdew.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.managers.GameInputProcessor;

import java.util.ArrayList;

public class DualStickController implements GameInputProcessor.TouchListener {

    private Circle leftStickOuterCircle;
    private Circle leftStickInnerCircle;
    private float leftStickDirection;
    private float leftStickTouchPointer;
    private boolean leftStickEdgeTouched = false;
    private boolean leftStickTouching = false;
    private float leftStickDistance = 0f;

    private Circle rightStickOuterCircle;
    private Circle rightStickInnerCircle;
    private float rightStickDirection;
    private float rightStickTouchPointer;
    private boolean rightStickEdgeTouched = false;
    private boolean rightStickTouching = false;
    private float rightStickDistance = 0f;

    private boolean rightStickFixed = false;

    private Vector3 touchPoint;

    private boolean handleTouch = false;

    // Controller Stick radius animation
    private boolean animateSticks = false;
    private final float dRadius = 20f; // Change in radius per second
    private ArrayList<Circle> circles;
    private float animTimer = 0;
    private final float animTime = 0.4f;
    private final int MAX_CIRCLES = 4;
    private final float fixedStickRadiusPercent = 0.3f;

    /**
     * Constructor
     *
     * @param centerLS Center for left stick of controller
     * @param centerRS Center for right stick of controller
     */
    public DualStickController(Vector2 centerLS, Vector2 centerRS) {

        this.leftStickOuterCircle = new Circle(centerLS.x, centerLS.y, 70);
        this.leftStickInnerCircle = new Circle(centerLS.x, centerLS.y, this.leftStickOuterCircle.radius / 3f);

        this.rightStickOuterCircle = new Circle(centerRS.x, centerRS.y, 70);
        this.rightStickInnerCircle = new Circle(centerRS.x, centerRS.y, this.rightStickOuterCircle.radius / 3f);

        touchPoint = new Vector3();

        leftStickDirection = MathUtils.PI / 2;
        rightStickDirection = MathUtils.PI / 2;

        circles = new ArrayList<Circle>();

        // Register Touch events
        ((GameInputProcessor) Gdx.input.getInputProcessor()).registerTouchListener(this);
    }

    private void animateSticks(float dt, ShapeRenderer sr){
        // Animate
        float r = MathUtils.random(1f);
        float g = MathUtils.random(0.5f, 1f);
        float b = MathUtils.random(1f);
        for(Circle c : circles){
            c.radius += dRadius * dt;
            if(c.radius > leftStickOuterCircle.radius) c.radius = leftStickInnerCircle.radius;
        }
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(r, g, b,1);
        for(Circle c : circles){
            sr.circle(leftStickOuterCircle.x,leftStickOuterCircle.y, c.radius, 50);
            sr.circle(rightStickOuterCircle.x, rightStickOuterCircle.y, c.radius, 50);
        }
        sr.end();

        animTimer += dt;
        if(animTimer < animTime) return;
        animTimer = 0;
        if(circles.size() < MAX_CIRCLES) circles.add(new Circle(0, 0, leftStickInnerCircle.radius));

    }

    // Both ShapeRenderer and SpriteBatch must not already begin
    // and Projection Matrix must already be set
    public void drawController(ShapeRenderer sr) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw Outer Circles of both sticks
        sr.getColor().a = 0.5f;
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(0,.8157f,1,.5f);
        sr.circle(leftStickOuterCircle.x, leftStickOuterCircle.y, leftStickInnerCircle.radius * 1.5f, 100);
        sr.setColor(.75f, .22f, 1f, .5f);
        sr.circle(rightStickOuterCircle.x, rightStickOuterCircle.y, rightStickInnerCircle.radius * 1.5f, 100);
        sr.end();

        // Draw Inner circles of both sticks
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0,.8157f,1,.5f);
        sr.circle(leftStickInnerCircle.x, leftStickInnerCircle.y, leftStickInnerCircle.radius, 100);
        sr.setColor(.75f, .22f, 1f, .5f);
        sr.circle(rightStickInnerCircle.x, rightStickInnerCircle.y, rightStickInnerCircle.radius, 100);
        sr.end();

        // Animate Sticks if animation is enabled
        if(animateSticks) animateSticks(Gdx.graphics.getDeltaTime(), sr);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        // unregister Touch events
        ((GameInputProcessor) Gdx.input.getInputProcessor()).unregisterTouchListener();
    }

    public void setHandleTouch(boolean touch) {
        this.handleTouch = touch;
    }

    public float getLeftStickDirection() {
        return leftStickDirection;
    }

    public float getRightStickDirection() {
        return rightStickDirection;
    }


    public boolean leftStickEdgeTouching() {
        return leftStickEdgeTouched;
    }

    public boolean rightStickEdgeTouching() {
        return rightStickEdgeTouched;
    }

    public boolean isLeftStickTouching() {
        return leftStickTouching;
    }

    public boolean isRightStickTouching() {
        return rightStickTouching;
    }

    public float getLeftStickDistance() {
        return leftStickDistance;
    }

    public float getRightStickDistance() {
        return rightStickDistance;
    }

    public float getLeftStickMaxDistance(){
        return leftStickOuterCircle.radius;
    }
    public float getRightStickMaxDistance(){
        return rightStickOuterCircle.radius;
    }

    public void startSticksAnimation(){
        animateSticks = true;
    }

    public void setRightStickFixed(boolean fixed){
        if(!rightStickFixed && fixed) rightStickOuterCircle.radius *= fixedStickRadiusPercent;
        else if(rightStickFixed && !fixed) rightStickOuterCircle.radius /= fixedStickRadiusPercent;
        rightStickFixed = fixed;
    }

    public boolean isRightStickFixed() { return rightStickFixed; }

    public void stopSticksAnimation(){
        circles.clear();
        animTimer = 0;
        animateSticks = false;
    }
    public boolean sticksAnimating() { return animateSticks; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!handleTouch) return false;
        PewDew.cam.unproject(touchPoint.set(screenX, screenY, 0));

        if (leftStickOuterCircle.contains(touchPoint.x, touchPoint.y)) {
            leftStickTouchPointer = pointer;
            leftStickTouching = true;
        }
        if (rightStickOuterCircle.contains(touchPoint.x, touchPoint.y)) {
            rightStickTouchPointer = pointer;
            rightStickTouching = true;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!handleTouch) return false;

        if (pointer == leftStickTouchPointer) {
            // Reset inner circle position
            leftStickInnerCircle.setX(leftStickOuterCircle.x);
            leftStickInnerCircle.setY(leftStickOuterCircle.y);
            leftStickEdgeTouched = false;
            leftStickTouching = false;
            leftStickDistance = 0;
        }

        if (pointer == rightStickTouchPointer) {
            // Reset inner circle position
            rightStickInnerCircle.setX(rightStickOuterCircle.x);
            rightStickInnerCircle.setY(rightStickOuterCircle.y);
            rightStickEdgeTouched = false;
            rightStickTouching = false;
            rightStickDistance = 0;
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!handleTouch) return false;
        PewDew.cam.unproject(touchPoint.set(screenX, screenY, 0));
        if (pointer == leftStickTouchPointer
                && leftStickTouching
                && touchPoint.x < PewDew.WIDTH / 2
                && touchPoint.y < PewDew.HEIGHT / 2) {
            float x = touchPoint.x - leftStickOuterCircle.x;
            float y = touchPoint.y - leftStickOuterCircle.y;
            float direction = MathUtils.atan2(y, x);
            float distance = (float) Math.sqrt(x * x + y * y);

            distance = Math.min(distance, leftStickOuterCircle.radius);

            touchPoint.x = leftStickOuterCircle.x + (distance * MathUtils.cos(direction));
            touchPoint.y = leftStickOuterCircle.y + (distance * MathUtils.sin(direction));

            leftStickInnerCircle.setX(touchPoint.x);
            leftStickInnerCircle.setY(touchPoint.y);
            leftStickDirection = direction;

            // Detect edge touching
            x = leftStickOuterCircle.x - leftStickInnerCircle.x;
            y = leftStickOuterCircle.y - leftStickInnerCircle.y;
            distance = (float) Math.sqrt(x * x + y * y);
            leftStickDistance = distance;
            if (distance >= leftStickOuterCircle.radius * 0.8) leftStickEdgeTouched = true;
            else leftStickEdgeTouched = false;
        }
        if (pointer == rightStickTouchPointer
                && rightStickTouching
                && touchPoint.x > PewDew.WIDTH / 2
                && touchPoint.y < PewDew.HEIGHT / 2) {
            float x = touchPoint.x - rightStickOuterCircle.x;
            float y = touchPoint.y - rightStickOuterCircle.y;
            float distance = (float) Math.sqrt(x * x + y * y);
            distance = Math.min(distance, rightStickOuterCircle.radius);

            float direction = MathUtils.atan2(y, x);
            touchPoint.x = rightStickOuterCircle.x + (distance * MathUtils.cos(direction));
            touchPoint.y = rightStickOuterCircle.y + (distance * MathUtils.sin(direction));

            rightStickInnerCircle.setX(touchPoint.x);
            rightStickInnerCircle.setY(touchPoint.y);
            rightStickDirection = direction;

            // Detect edge touching
            x = rightStickOuterCircle.x - rightStickInnerCircle.x;
            y = rightStickOuterCircle.y - rightStickInnerCircle.y;
            distance = (float) Math.sqrt(x * x + y * y);
            rightStickDistance = distance;
            if (distance >= rightStickOuterCircle.radius * 0.8) rightStickEdgeTouched = true;
            else rightStickEdgeTouched = false;
        }
        return true;
    }
}
