package jhunions.isaiahgao.client;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Graphics {
    
    public enum PracticeRoomButton {
        NORMAL_NORMAL("/blubutt.png"),
        NORMAL_PRESSED("/blubutt_pressed.png"),
        NORMAL_SELECTED("/blubutt_selected.png"),
        NORMAL_DISABLED("/blubutt_disabled.png"),
        LONG_NORMAL("/longbutt.png"),
        LONG_PRESSED("/longbutt_pressed.png"),
        LONG_SELECTED("/longbutt_selected.png");
        
        private PracticeRoomButton(String filename) {
            try {
                this.icon = ImageIO.read(this.getClass().getResourceAsStream("/buttons" + filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        private BufferedImage icon;
        
        public BufferedImage getImage() {
            return this.icon;
        }
    }

}
