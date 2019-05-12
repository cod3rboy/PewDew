package com.cod3rboy.pewdew.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.managers.GameInputProcessor;

public class Controller implements GameInputProcessor.TouchListener {
    private Circle stickOuterCircle;
    private Circle stickInnerCircle;

    private Rectangle btnABounds;
    private Rectangle btnBBounds;
    private Texture btnATexture;
    private Texture btnBTexture;

    private float stickDirection;

    private float stickTouchPointer;
    private float btnATouchPointer;
    private float btnBTouchPointer;

    private Vector3 touchPoint;

    private boolean handleTouch =  false;

    private boolean btnAPressed = false;
    private boolean btnBPressed = false;

    private boolean edgeTouched = false;

    public Controller(Vector2 stickCenter, Vector2 btnAPos, Vector2 btnBPos, Texture btnATexture, Texture btnBTexture){

        this.stickOuterCircle = new Circle(stickCenter.x, stickCenter.y, 70);
        this.stickInnerCircle = new Circle(stickCenter.x, stickCenter.y, this.stickOuterCircle.radius/4f);

        this.btnATexture = btnATexture;
        this.btnBTexture = btnBTexture;

        this.btnABounds = new Rectangle(btnAPos.x, btnAPos.y, 80, 80);
        this.btnBBounds = new Rectangle(btnBPos.x, btnBPos.y, 80, 80);

        touchPoint = new Vector3();

        stickDirection = MathUtils.PI/2;

        // Register Touch events
        ((GameInputProcessor) Gdx.input.getInputProcessor()).registerTouchListener(this);
    }

    // Both ShapeRenderer and SpriteBatch must not already begin
    // and Projection Matrix must already be set
    public void drawController(ShapeRenderer sr, SpriteBatch sb){

        // Draw Stick Outer Circle
        sr.setColor(Color.YELLOW);
        sr.getColor().a = 0.5f;
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.circle(stickOuterCircle.x, stickOuterCircle.y, stickOuterCircle.radius, 100);
        sr.end();

        // Draw Stick Inner Circle
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(1,.8863f,.07451f, 0.3f);
        sr.circle(stickInnerCircle.x, stickInnerCircle.y, stickInnerCircle.radius, 100);
        sr.end();

        // Draw Button A and Button B
        sb.begin();
        sb.setColor(1,1,1,0.5f);
        sb.draw(btnATexture, btnABounds.x, btnABounds.y, btnABounds.getWidth(), btnABounds.getHeight());
        sb.draw(btnBTexture, btnBBounds.x, btnBBounds.y, btnBBounds.getWidth(), btnBBounds.getHeight());
        sb.end();
    }

    public void dispose(){
        // unregister Touch events
        ((GameInputProcessor) Gdx.input.getInputProcessor()).unregisterTouchListener();
    }

    public void setHandleTouch(boolean touch){
        this.handleTouch = touch;
    }

    public float getStickDirection(){ return stickDirection; }

    public boolean isPressedButtonA(){ return btnAPressed; }
    public boolean isPressedButtonB(){ return btnBPressed; }

    public boolean isEdgeTouching() {  return edgeTouched; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(!handleTouch) return false;
        PewDew.cam.unproject(touchPoint.set(screenX, screenY, 0));
        if(stickOuterCircle.contains(touchPoint.x, touchPoint.y)){
            stickTouchPointer = pointer;
        }
        if(!btnAPressed && btnABounds.contains(touchPoint.x, touchPoint.y)){
            btnATouchPointer = pointer;
            btnAPressed = true;
        }
        if(!btnBPressed && btnBBounds.contains(touchPoint.x, touchPoint.y)){
            btnBTouchPointer = pointer;
            btnBPressed = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!handleTouch) return false;
        if(pointer == btnATouchPointer){
            btnAPressed = false;
        }
        if(pointer == btnBTouchPointer){
            btnBPressed = false;
        }
        if(pointer == stickTouchPointer){
            // Reset inner circle position
            stickInnerCircle.setX(stickOuterCircle.x);
            stickInnerCircle.setY(stickOuterCircle.y);
            edgeTouched = false;
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(!handleTouch) return false;
        PewDew.cam.unproject(touchPoint.set(screenX, screenY, 0));
        if(pointer == stickTouchPointer && touchPoint.x < PewDew.WIDTH/2){
            float x = touchPoint.x - stickOuterCircle.x;
            float y = touchPoint.y - stickOuterCircle.y;
            float direction = MathUtils.atan2(y, x);
            float distance = (float) Math.sqrt(x*x + y*y);

            distance = Math.min(distance, stickOuterCircle.radius);

            touchPoint.x = stickOuterCircle.x + (distance * MathUtils.cos(direction));
            touchPoint.y = stickOuterCircle.y + (distance * MathUtils.sin(direction));

            stickInnerCircle.setX(touchPoint.x);
            stickInnerCircle.setY(touchPoint.y);
            stickDirection  = direction;

            // Detect edge touching
            x = stickOuterCircle.x - stickInnerCircle.x;
            y = stickOuterCircle.y - stickInnerCircle.y;
            distance = (float) Math.sqrt(x*x + y*y);
            if(distance >= stickOuterCircle.radius * 0.8) edgeTouched = true;
            else edgeTouched = false;
        }
        return true;
    }
}
