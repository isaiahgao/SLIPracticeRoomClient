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

public class GUIConfirm extends GUI implements ActionListener, WindowListener {

    private static final long serialVersionUID = 406439573353L;

    public GUIConfirm(Main instance, String info, JFrame todispose, Runnable runafter) {
        super(instance, "Confirm", 400, 225, JFrame.DISPOSE_ON_CLOSE, true);
        
        this.info = info;
        this.frame.setAlwaysOnTop(true);
        this.frame.addWindowListener(this);
        this.todispose = todispose;
        this.runafter = runafter;
    }
    
    private String info;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel text;
    private JFrame todispose;
    private Runnable runafter;

    @Override
    protected void setup() {
        //construct components
        buttonOK = new JButton("OK");
        buttonOK.setBackground(Color.WHITE);
        buttonOK.addActionListener(this);
        buttonOK.setActionCommand("ok");

        buttonCancel = new JButton("Cancel");
        buttonCancel.setBackground(Color.WHITE);
        buttonCancel.addActionListener(this);
        
        text = new JLabel(info);

        //adjust size and set layout
        setPreferredSize(new Dimension(400, 225));
        setLayout(null);

        //add components
        add(text);
        add(buttonCancel);
        add(buttonOK);
        
        //set component bounds(only needed by Absolute Positioning)
        buttonOK.setBounds(50, 165, 90, 50);
        buttonCancel.setBounds(160, 165, 90, 50);
        text.setBounds(0, 45, 400, 25);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.frame.dispose();
        if (e.getActionCommand().equals("ok") && this.runafter != null) {
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
