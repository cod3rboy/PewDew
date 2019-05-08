package com.cod3rboy.asteroids.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class Jukebox {
    private static HashMap<String, Sound> sounds;
    private static Music bgMusic;
    static{
        sounds = new HashMap<String, Sound>();
    }
    public static void load(String path, String name){
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(name, sound);
    }

    public static void play(String name){
        sounds.get(name).play();
    }
    public static void loop(String name){
        sounds.get(name).loop();
    }
    public static void stop(String name){
        sounds.get(name).stop();
    }

    public static void stopAll(){
        for(Sound s: sounds.values()){
            s.stop();
        }
    }

    public static void loadBackgroundMusic(String path){
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        bgMusic.setLooping(true);
    }
    public static void playBackgroundMusic(){
        if(bgMusic != null) bgMusic.play();
    }
    public static void pauseBackgroundMusic(){
        if(bgMusic != null) bgMusic.pause();
    }
    public static void stopBackgroundMusic(){
        if(bgMusic != null && bgMusic.isPlaying()) bgMusic.stop();
    }
    public static void setBackgroundVolume(float vol){
        if(bgMusic != null) bgMusic.setVolume(vol);
    }

    public static void dispose(){
        for(Sound s: sounds.values()){
            s.dispose();
        }
        sounds.clear();
        bgMusic.dispose();
    }
}
