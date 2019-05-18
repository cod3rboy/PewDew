package com.cod3rboy.pewdew.entities.powerups;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.cod3rboy.pewdew.PewDew;
import com.cod3rboy.pewdew.managers.Jukebox;

public abstract class PowerUp {
    protected Rectangle bounds;
    protected String powertitle;
    protected float screentimeout;
    protected float screentimer;
    protected boolean applied;
    protected float powertimeout;
    protected float powertimer;
    protected boolean removed;
    private IPowerable player;
    protected int powerid;

    // Power animation
    private boolean animate = true;
    private final float MAX_SIDE = 45;
    private float animSide; // Since power up has square shape

    public PowerUp(String title, float x, float y, IPowerable player){
        powertitle = title;

        bounds = new Rectangle();
        bounds.x = x;
        bounds.y = y;

        // set default square bounds
        bounds.width = 30; // 5 for margin
        bounds.height = 30;  // 5 for margin

        screentimeout = 4;
        screentimer = 0;
        applied = false;
        powertimeout = 6;
        powertimer = 0;
        removed = false;
        this.player = player;

        animSide = bounds.width;
    }

    private void animatePower(float dt){
        if(!animate) return;
        animSide += dt * MAX_SIDE;
        if(animSide > MAX_SIDE) animSide = bounds.width;
    }

    private void setAnimation(boolean animate){
        this.animate = animate;
    }

    public void setLocation(float x, float y){
        bounds.x = x;
        bounds.y = y;
    }

    public void setScreenTimeout(float time){
        screentimer = 0;
        screentimeout = time;
    }

    public void setPowerTimeout(float time){
        powertimer = 0;
        powertimeout = time;
    }

    public void setPlayer(IPowerable player){
        this.player = player;
    }

    public void applyPower(){
        applied = true;
        powertimer = 0;
        Jukebox.play("powerup");
        // Apply power here to player
        player.powerApplied(powerid);
    }

    private void resetPlayer(){
        // Reset power applied to player
        player.powerReset(powerid);
    }

    public boolean isPowerApplied(){
        return applied;
    }

    public boolean shouldRemove(){
        return removed;
    }

    public void update(float dt){
        if(removed) return;

        // Try to apply power
        if(!applied && player.powerTouched(bounds.x, bounds.y, bounds.width, bounds.height)){
            // Apply power
            applyPower();
        }

        // Update timers here
        if(!applied) {
            animatePower(dt);
            screentimer += dt;
            if (screentimer >= screentimeout) removed = true;
        }else {
            powertimer += dt;
            if (powertimer >= powertimeout) {
                removed = true;
                resetPlayer();
            }
        }
    }

    public void draw(ShapeRenderer sr, SpriteBatch sb, BitmapFont font, GlyphLayout fontLayout){
        if(!applied) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(1, 1, 1, 1);
            sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            sr.setColor(MathUtils.random(1f), MathUtils.random(1f), MathUtils.random(1f), 1);
            sr.rect(
                    bounds.x - (animSide-bounds.width)/2,
                    bounds.y - (animSide-bounds.height)/2,
                    animSide,
                    animSide
            );
            sr.end();
        }else{
            // Set font layout
            fontLayout.setText(font, powertitle);

            // Draw and fill loading bar
            sr.setColor(1,1,1,1);
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.rect(PewDew.WIDTH - 120, PewDew.HEIGHT - 40, 100, fontLayout.height);
            sr.end();
            sr.begin(ShapeRenderer.ShapeType.Filled);
            float fillWidth = 100 - 100 * (powertimer/powertimeout);
            sr.rect((PewDew.WIDTH - 120), PewDew.HEIGHT - 40, fillWidth, fontLayout.height);
            sr.end();

            // Draw Power Title Text
            font.setColor(1,1,1,1);
            sb.begin();
            font.draw(sb,fontLayout,PewDew.WIDTH-120-fontLayout.width-5, PewDew.HEIGHT - 40 + fontLayout.height);
            sb.end();

        }
    }

    public interface IPowerable{
        void powerApplied(int powerid);
        void powerReset(int powerid);
        boolean powerTouched(float x, float y, float w, float h);
    }
}
