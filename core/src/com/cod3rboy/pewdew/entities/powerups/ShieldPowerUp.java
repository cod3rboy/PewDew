package com.cod3rboy.pewdew.entities.powerups;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cod3rboy.pewdew.PewDew;

public class ShieldPowerUp extends PowerUp {
    public static final int POWER_UP_SHIELD = 101;
    public ShieldPowerUp(float x, float y, IPowerable player) {
        super("Shield", x, y, player);
        powerid = POWER_UP_SHIELD;
    }
    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb, BitmapFont font, GlyphLayout fontLayout) {
        super.draw(sr, sb, font, fontLayout);
        sb.begin();
        if(!applied){
            sb.draw(PewDew.textures.get("icon-shield"), bounds.x+5, bounds.y+5, bounds.width-10, bounds.height-10);
        }
        sb.end();
    }
}
