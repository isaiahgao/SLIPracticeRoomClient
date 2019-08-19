package jhunions.isaiahgao.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jhunions.isaiahgao.client.IO;
import jhunions.isaiahgao.client.Main;
import jhunions.isaiahgao.client.SoundHandler.Sound;
import jhunions.isaiahgao.common.User;

public class GUIAcceptPolicy extends GUI implements ActionListener, WindowListener {

    private static final long serialVersionUID = -8715194535625598073L;
    private static ImageIcon imageicon;
    
    static {
        try {
            imageicon = new ImageIcon(ImageIO.read(
                    GUIAcceptPolicy.class.getResourceAsStream("/agreement.png")
                    ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GUIAcceptPolicy(Main instance, GUIAddInfo parent, User user, boolean saveToDB) {
        super(instance, "Practice Room Policy", 713, 1000, JFrame.DISPOSE_ON_CLOSE, true);
        this.saveToDB = saveToDB;
        this.usd = user;
        this.parent = parent;
        this.parent.frame.dispose();
        this.frame.setAlwaysOnTop(true);
        this.frame.setBackground(Color.WHITE);
        this.frame.addWindowListener(this);
    }
    
    protected User usd;
    protected GUIAddInfo parent;
    protected JLabel image;
    protected JButton accept, decline;
    protected boolean saveToDB;

    private boolean accepted;
    
    @Override
    protected void setup() {
        try {
            image = new JLabel(imageicon); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        accept = new JButton("I have read and agree to abide by these policies");
        accept.addActionListener(this);
        accept.setActionCommand("ok");
        accept.setBackground(Color.WHITE);

        decline = new JButton("No way");
        decline.addActionListener(this);
        decline.setActionCommand("no");
        decline.setBackground(Color.WHITE);
        
        setPreferredSize(new Dimension(713, 1000));
        setLayout(null);
        
        add(accept);
        add(decline);
        add(image);
        
        accept.setBounds(10, 945, 450, 45);
        decline.setBounds(470, 945, 200, 45);
        image.setBounds(0, 0, 713, 1000);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("no")) {
            Sound.REGISTER_UNSUCCESSFUL.play();
            this.frame.dispose();
        } else if (e.getActionCommand().equals("ok")) {
            try {
                if (this.saveToDB) {
                	if (!IO.push(usd).get()) {
                        this.instance.sendDisappearingConfirm("Failed to check out practice room.<br>Maybe the internet is down?", 40);
                        Sound.REGISTER_UNSUCCESSFUL.play();
                        return;
                	}
                    Sound.REGISTER_SUCCESSFUL.play();
                }
                this.accepted = true;
                this.frame.dispose();
                this.instance.getBaseGUI().scanID(usd, usd.getHopkinsID());
            } catch (Exception ex) {
            	ex.printStackTrace();
                this.instance.sendMessage("Invalid info. Please try again.");
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (!accepted)
            this.instance.sendMessage("You cannot use the practice rooms<br>unless you agree to our policies.", 40);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        
    }

    @Override
    public void windowActivated(WindowEvent e) {
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        
    }

}
