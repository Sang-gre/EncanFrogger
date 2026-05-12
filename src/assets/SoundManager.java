package assets;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class SoundManager {

    private Map<String, Clip> sounds;
    private Clip currentMusic;

    public SoundManager(){
        sounds = new HashMap<> ();

       loadSound("click", "assets/sounds/sfx/click.wav");
       loadSound("coin", "assets/sounds/sfx/coins.wav");
       loadSound("death", "assets/sounds/sfx/died.wav");
       loadSound("move", "assets/sounds/sfx/move.wav");
       loadSound("gameover", "assets/sounds/sfx/gameover.wav");

       loadSound("game", "assets/sounds/bgm/gameBGM.wav");
       loadSound("menu", "assets/sounds/bgm/menuBGM.wav");
    }

    public void loadSound (String name, String path){
       
        try {
            //find resource
            File soundFile = new File(path);

            if (!soundFile.exists()) {
                System.out.println("file not found: " + path);
                return;
            }

            //read audio data
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            sounds.put(name, clip); //adding it to the hashmap

        } catch (Exception e ){
            System.out.println("Error Loading Sound " + name);
            e.printStackTrace();
        }
       
    }

    //------------------ sound effects ------------------//
    public void play(String name){
        Clip clip = sounds.get(name);

        //making sure it exists
        if (clip == null){
            System.out.println("Sound Not Found" + name);
            return;
        }

        //if it's alr playing, stop playbacck
        if (clip.isRunning()){
            clip.stop();
        }

        clip.setFramePosition(0);//rewind 
        clip.start();// play the sound 
    }

    public void stop (String name){
        Clip clip = sounds.get(name);

        if (clip != null){
            clip.stop();
        }
    }


    //----------------------BGM--------------------//
    public void playBGM (String name){
        if (currentMusic != null){
            currentMusic.stop();
        }
        

        currentMusic = sounds.get(name);

        if (currentMusic == null){
            System.out.println("Current BGM does not exist " + name);
            return;
        }

        currentMusic.setFramePosition(0);
        currentMusic.loop(Clip.LOOP_CONTINUOUSLY);

    }


    public void stopBGM (){
        if (currentMusic != null){
            currentMusic.stop();
        }
    }

    public void loop (String name){
        Clip clip = sounds.get(name);

        if (clip == null){
            return;
        }

        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
