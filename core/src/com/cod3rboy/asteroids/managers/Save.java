package com.cod3rboy.asteroids.managers;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Save {
    public static GameData gd;

    public static final String saveFileName = "highscores.sav";

    public static void save(){
        ObjectOutputStream out = null;
        try{
            out = new ObjectOutputStream(
                    new FileOutputStream(saveFileName)
            );
            out.writeObject(gd);
        }catch(IOException e){
            e.printStackTrace();
            Gdx.app.exit();
        }finally {
            if(out != null){
                try{
                    out.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                    Gdx.app.exit();
                }
            }
        }
    }
    public static void load(){

        if(!saveFileExists()) {
            init();
            return;
        }

        ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(
                    new FileInputStream(saveFileName)
            );
            gd = (GameData) in.readObject();
        }catch(IOException e){
            e.printStackTrace();
            Gdx.app.exit();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            Gdx.app.exit();
        }finally{
            if(in != null){
                try{
                    in.close();
                }catch(IOException e){
                    e.printStackTrace();
                    Gdx.app.exit();
                }
            }
        }
    }
    public static boolean saveFileExists(){
        File f = new File(saveFileName);
        return f.exists();
    }

    public static void init(){
        gd = new GameData();
        gd.init();
        save();
    }
}
