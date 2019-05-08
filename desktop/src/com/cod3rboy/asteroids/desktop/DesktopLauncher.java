package com.cod3rboy.asteroids.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cod3rboy.asteroids.Asteroids;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Asteroids";
		config.width = 1000;
		config.height = 800;
		config.useGL30 = false;
		config.fullscreen = false;
		config.resizable = false;
		new LwjglApplication(new Asteroids(), config);
	}
}
