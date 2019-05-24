package com.cod3rboy.pewdew.entities.powerups;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cod3rboy.pewdew.PewDew;

public class FrenzyPowerUp extends PowerUp{
    public static final int POWER_UP_FRENZY = 102;
    public FrenzyPowerUp(float x, float y, IPowerable player) {
        super("FRENZY", x, y, player);
        powerid = POWER_UP_FRENZY;
    }
    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb, BitmapFont font, GlyphLayout fontLayout) {
        super.draw(sr, sb, font, fontLayout);
        sb.begin();
        sb.setColor(1,1,1,0.8f);
        if(!applied){
           sb.draw(PewDew.textures.get("icon-frenzy"), bounds.x+5, bounds.y+5, bounds.width-10, bounds.height-10);
        }
        sb.end();
    }
}
