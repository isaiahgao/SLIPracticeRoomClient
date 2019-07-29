package jhunions.isaiahgao.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicBorders.ButtonBorder;

import jhunions.isaiahgao.client.Main;

public class GUIPromptRegister extends GUI implements ActionListener {
    
    public static void main(String[] args) {
        new GUIPromptRegister(new Main(), null).frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static final long serialVersionUID = 40679393671642133L;

    public GUIPromptRegister(Main instance, GUIBase base) {
        super(instance, "No Data Stored", 800, 1000, JFrame.DISPOSE_ON_CLOSE, true);
        this.frame.setAlwaysOnTop(true);
        this.base = base;
    }
    
    private GUIBase base;
    private JButton buttonRegister;
    private JButton buttonUseOnce;
    private JLabel textUpper;
    private JLabel textRegister;
    private JLabel textUseOnce;

    @Override
    protected void setup() {
        //construct components
        buttonRegister = new JButton ("<html><strong><font size=\"24\" face=\"arial rounded mt bold\">Register</font></strong></html>");
        buttonRegister.setBackground(Color.WHITE);
        buttonRegister.setBorder(new ButtonBorder(Color.DARK_GRAY, Color.BLACK, Color.GRAY, Color.WHITE));
        buttonRegister.setActionCommand("register");
        buttonRegister.addActionListener(this);
        
        buttonUseOnce = new JButton ("<html><strong><font size=\"24\" face=\"arial rounded mt bold\">Use Once</font></strong></html>");
        buttonUseOnce.setBackground(Color.WHITE);
        buttonUseOnce.setBorder(new ButtonBorder(Color.DARK_GRAY, Color.BLACK, Color.GRAY, Color.WHITE));
        buttonUseOnce.setActionCommand("useonce");
        buttonUseOnce.addActionListener(this);
        
        textUpper = new JLabel ("<html><center><font size=\"16\" face=\"verdana\">You have not yet registered with JHUnions.<br>Would you like to register?</font></center></html>");
        textRegister = new JLabel ("<html><font size=\"5\" face=\"verdana\">Registering saves your JCard number and info in our database, and allows you to simply <i>click, swipe, n' go</i> each time you want to use a practice room!</font></html>");
        textUseOnce = new JLabel ("<html><font size=\"5\" face=\"verdana\">If you don't want to register right now, YOU WILL STILL HAVE TO FILL OUT YOUR INFORMATION. Your information will just not be stored in our database with this option, and you will need to re-input your information for future visits.</font></html>");

        //adjust size and set layout
        setPreferredSize (new Dimension (1100, 800));
        setLayout (null);

        //add components
        add(buttonRegister);
        add(buttonUseOnce);
        add(textUpper);
        add(textRegister);
        add(textUseOnce);

        //set component bounds (only needed by Absolute Positioning)
        buttonRegister.setBounds (30, 385, 405, 150);
        buttonUseOnce.setBounds (30, 555, 405, 150);
        textUpper.setBounds (100, 80, 1000, 200);
        textRegister.setBounds (475, 385, 570, 150);
        textUseOnce.setBounds (475, 555, 575, 150);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("register")) {
            this.frame.dispose();
            // open register prompt
            new GUIAddInfoRegister(this.instance, this.base.getCurrentId(), this.base);
        } else if (s.equals("useonce")) {
            this.frame.dispose();
            // open use once prompt
            new GUIAddInfoUseOnce(this.instance, this.base.getCurrentId(), this.base);
        }
    }

}
