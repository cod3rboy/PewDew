package com.cod3rboy.pewdew.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class Jukebox {
    private static HashMap<String, Sound> sounds;
    private static HashMap<String, Music> musics;
    static{
        sounds = new HashMap<String, Sound>();
        musics = new HashMap<String, Music>();
    }
    public static void load(String path, String name){
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(name, sound);
    }

    public static void play(String name, float volume){
        sounds.get(name).play(volume);
    }
    public static void play(String name){
        play(name, 1f);
    }
    public static void loop(String name, float volume){
        sounds.get(name).loop(volume);
    }
    public static void loop(String name){
        loop(name, 1f);
    }

    public static void stop(String name){
        sounds.get(name).stop();
    }

    public static void stopAll(){
        for(Sound s: sounds.values()){
            s.stop();
        }
    }

    public static void loadBackgroundMusic(String path, String name){
        Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
        music.setLooping(true);
        musics.put(name, music);
    }
    public static void playBackgroundMusic(String name, float volume){
        Music m = musics.get(name);
        m.setVolume(volume);
        m.play();
    }
    public static void playBackgroundMusic(String name){
        playBackgroundMusic(name, 1f);
    }
    public static void pauseBackgroundMusic(String name){
        musics.get(name).pause();
    }
    public static void stopBackgroundMusic(String name){
        Music music = musics.get(name);
        if(music.isPlaying()) music.stop();
    }
    public static boolean isPlayingBackgroundMusic(String name){
        return musics.get(name).isPlaying();
    }
    public static void stopAllBackgroundMusic(){
        for(Music m : musics.values()){
            if(m.isPlaying()) m.stop();
        }
    }
    public static void dispose(){
        for(Sound s: sounds.values()){
            s.dispose();
        }
        for(Music m: musics.values()){
            m.dispose();
        }
        sounds.clear();
        musics.clear();
    }
}
