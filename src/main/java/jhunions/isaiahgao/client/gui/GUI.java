package jhunions.isaiahgao.client.gui;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jhunions.isaiahgao.client.Main;

public abstract class GUI extends JPanel {

    private static final long serialVersionUID = -1801320926619229597L;

    public GUI(Main instance, String title, int width, int height, int exitOp, boolean visible) {
        this.instance = instance;
        
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(exitOp);
        this.frame.setTitle(title);
        this.frame.setSize(width, height);
        this.setup();
        
        try {
            this.frame.setIconImage(ImageIO.read(this.getClass().getResourceAsStream("/logo.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.frame.setVisible(visible);
        this.frame.getContentPane().add(this);
        this.frame.pack();
        this.displayGUI();
    }

    protected JFrame frame;
    protected boolean isDisplayed;
    protected Main instance;

    protected abstract void setup();
    
    public JFrame getFrame() {
        return this.frame;
    }

    public void displayGUI() {
        this.isDisplayed = true;
        this.frame.setVisible(true);
    }

    public void hideGUI() {
        this.frame.setVisible(false);
        this.isDisplayed = false;
    }

}
