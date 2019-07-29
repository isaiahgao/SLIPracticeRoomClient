package jhunions.isaiahgao.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jhunions.isaiahgao.client.Main;
import jhunions.isaiahgao.client.Utils;

public class GUIMessage extends GUI implements ActionListener, WindowListener {

    private static final long serialVersionUID = 4064395753L;
    
    public static void main(String[] args) {
        new GUIMessage(new Main(), Utils.format("You cannot use the practice rooms<br>unless you agree to our policies.", 12, "verdana", true), null, null, 50);
    }

    public GUIMessage(Main instance, String info, JFrame todispose, Runnable runafter) {
        super(instance, "Info", 400, 225, JFrame.DISPOSE_ON_CLOSE, true);
        
        this.info = info;
        this.frame.setAlwaysOnTop(true);
        this.frame.addWindowListener(this);
        this.todispose = todispose;
        this.runafter = runafter;
        this.text.setText(this.info);
    }
    
    public GUIMessage(Main instance, String info, JFrame todispose, Runnable runafter, int xbound) {
        this(instance, info, todispose, runafter);
        this.xbound = xbound;
        this.text.setBounds(xbound, 25, 400 - xbound, 100);
    }
    
    private int xbound = 105;
    
    private String info;
    private JButton button;
    private JLabel text;
    private JFrame todispose;
    private Runnable runafter;

    @Override
    protected void setup() {
        //construct components
        button = new JButton("OK");
        button.setBackground(Color.WHITE);
        button.addActionListener(this);
        
        text = new JLabel(info);

        //adjust size and set layout
        setPreferredSize(new Dimension(400, 225));
        setLayout(null);

        //add components
        add(text);
        add(button);
        
        //set component bounds(only needed by Absolute Positioning)
        button.setBounds(110, 165, 185, 50);
        text.setBounds(xbound, 25, 400 - xbound, 100);
        this.frame.setLocation(500, 500);
        this.frame.setResizable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.frame.dispose();
        if (this.runafter != null) {
            this.runafter.run();
        }
        if (this.todispose != null) {
            this.todispose.dispose();
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
        if (this.runafter != null) {
            this.runafter.run();
        }
        if (this.todispose != null) {
            this.todispose.dispose();
        }
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
