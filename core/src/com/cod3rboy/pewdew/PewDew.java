package com.cod3rboy.pewdew;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.cod3rboy.pewdew.managers.GameInputProcessor;
import com.cod3rboy.pewdew.managers.GameKeys;
import com.cod3rboy.pewdew.managers.GameStateManager;
import com.cod3rboy.pewdew.managers.Jukebox;

import java.util.HashMap;

public class PewDew extends ApplicationAdapter {
	public static int WIDTH;
	public static int HEIGHT;
	public static OrthographicCamera cam;
	private static ExtendViewport viewport;
	private GameStateManager gsm;
	public static HashMap<String,Texture> textures;
	static{
		textures = new HashMap<String, Texture>();
	}
	private static void loadTextures(){
		// Load all textures here
		textures.put("icon-shield", new Texture(Gdx.files.internal("textures/shield.png")));
		textures.put("icon-frenzy", new Texture(Gdx.files.internal("textures/frenzy.png")));
	}

	@Override
	public void create() {
	    // Keep aspect ratio in mind while setting width and height
		WIDTH = 800;
		HEIGHT = 450;
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		viewport = new ExtendViewport(WIDTH, HEIGHT,cam);
		viewport.apply();
		cam.translate(WIDTH/2, HEIGHT/2);
		cam.update();
		Gdx.input.setInputProcessor(new GameInputProcessor());

		// Load sounds
		Jukebox.load("sounds/keypress.ogg", "keypress");
		Jukebox.load("sounds/explode.ogg", "explode");
		Jukebox.load("sounds/extralife.ogg", "extralife");
		Jukebox.load("sounds/largesaucer.ogg", "largesaucer");
		Jukebox.load("sounds/pulsehigh.ogg", "pulsehigh");
		Jukebox.load("sounds/pulselow.ogg", "pulselow");
		Jukebox.load("sounds/saucershoot.ogg", "saucershoot");
		Jukebox.load("sounds/shoot.ogg", "shoot");
		Jukebox.load("sounds/smallsaucer.ogg", "smallsaucer");
		Jukebox.load("sounds/menuselect.ogg", "menuselect");
		Jukebox.load("sounds/powerup.ogg", "powerup");
		Jukebox.load("sounds/warship_entry.ogg", "warship-entry");
		Jukebox.load("sounds/warship_damage.ogg", "warship-damage");
		Jukebox.load("sounds/warship_shoot.ogg", "warship-shoot");
		Jukebox.load("sounds/warship_blast.ogg", "warship-blast");

		// load background music
		Jukebox.loadBackgroundMusic("sounds/deepspace.ogg", "deepspace");
		Jukebox.loadBackgroundMusic("sounds/chronos.ogg", "chronos");
		Jukebox.loadBackgroundMusic("sounds/gameover.ogg", "gameover");

		gsm = new GameStateManager();

		// Load textures
		loadTextures();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());

		gsm.draw();
		GameKeys.update();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		Jukebox.dispose();
		for(Texture t : textures.values()) t.dispose(); // Dispose all textures
	}
}
