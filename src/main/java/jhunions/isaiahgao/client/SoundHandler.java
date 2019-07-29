package jhunions.isaiahgao.client;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundHandler {
    
    public enum Sound {
        SIGN_IN,
        SIGN_OUT,
        ERROR,
        REGISTER_SUCCESSFUL,
        REGISTER_UNSUCCESSFUL,
        R109,
        R110,
        R111,
        R112,
        R114,
        R115,
        R116,
        R117,
        R118,
        R119;
        
        public void play() {
            try {
                InputStream is = new BufferedInputStream(this.getClass().getResourceAsStream("/sounds/" + this.toString() + ".wav"));
                Clip clip = AudioSystem.getClip();
                AudioInputStream ais = AudioSystem.getAudioInputStream(is);
                clip.open(ais);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
