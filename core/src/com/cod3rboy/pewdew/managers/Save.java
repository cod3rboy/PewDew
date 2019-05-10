package com.cod3rboy.pewdew.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Save {
    public static GameData gd;

    public static final String saveFileName = "highscores.sav";

    public static void save(){
        FileHandle saveFileHandle = Gdx.files.local(saveFileName);
        ObjectOutputStream out = null;
        try{
            out = new ObjectOutputStream(
                    new FileOutputStream(saveFileHandle.file())
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

        FileHandle saveFileHandle = Gdx.files.local(saveFileName);
        ObjectInputStream in = null;
        try{
            in = new ObjectInputStream(
                    new FileInputStream(saveFileHandle.file())
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
        FileHandle saveFileHandle = Gdx.files.local(saveFileName);
        return saveFileHandle.exists();
    }

    public static void init(){
        gd = new GameData();
        gd.init();
        save();
    }
}
