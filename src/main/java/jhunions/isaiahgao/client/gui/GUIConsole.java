package jhunions.isaiahgao.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import jhunions.isaiahgao.client.Main;

public class GUIConsole extends GUI implements ActionListener {
    
    private static final long serialVersionUID = 40679393613215753L;
    private static final String PASSWORD = "B4conand3ggs";
    
    public static void main(String[] args) {
        new GUIConsole(new Main());
    }

    /**
     * Constructor.
     * @param instance Instance of program.
     * @param base Base GUI pointer.
     * @param name Title on top of the GUI.
     * @param userId ID of the user opening the menu.
     * @param line What the text description line should say.
     * @param confirm What the "OK" button should say.
     */
    public GUIConsole(Main instance) {
        super(instance, "Console", 300, 400, JFrame.DISPOSE_ON_CLOSE, true);
        
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 99999);
        this.getActionMap().put(99999, new AbstractAction() {
            private static final long serialVersionUID = 7830455644315L;

            @Override
            public void actionPerformed(ActionEvent e) {
                handleCommand(consoleLine.getText());
            }
        });
    }
    
    private boolean auth = false;
    
    protected String line = "Register with JHUnions";
    protected String confirm = "Register";
    protected String userId;
    
    protected GUIBase base;
    
    protected JButton buttonSend;
    protected JTextArea consoleHistory;
    protected JTextField consoleLine;

    @Override
    protected void setup() {
        //construct components
        buttonSend = new JButton(">>");
        buttonSend.addActionListener(this);
        buttonSend.setActionCommand("send");
        buttonSend.setBackground(Color.WHITE);
        
        consoleHistory = new JTextArea(5, 5);
        consoleHistory.setText("Please enter password to unlock console.");
        consoleHistory.setEditable(false);
        consoleLine = new JTextField(5);
        
        //adjust size and set layout
        setPreferredSize(new Dimension(400, 300));
        setLayout(null);

        //add components
        add(buttonSend);
        add(consoleHistory);
        add(consoleLine);

        //set component bounds(only needed by Absolute Positioning)
        consoleHistory.setBounds(5, 5, 390, 260);
        consoleLine.setBounds(5, 270, 330, 25);
        consoleLine.setEditable(true);
        consoleLine.requestFocus();
        buttonSend.setBounds(340, 270, 50, 25);
    }
    
    protected void handleCommand(String cmd) {
        if (!this.auth) {
            consoleHistory.append(System.lineSeparator() + "> ****");
            if (cmd.equals(PASSWORD)) {
                this.auth = true;
                consoleHistory.append(System.lineSeparator() + "Password accepted! You may now use console.");
            } else {
                consoleHistory.append(System.lineSeparator() + "Password incorrect. It is the same as the monitor laptop's.");
            }
            this.consoleLine.setText("");
            return;
        }
        
        consoleHistory.append(System.lineSeparator() + "> " + cmd);
        String result = Main.getCommandHandler().perform(cmd);
        consoleHistory.append(System.lineSeparator() + result);
        this.consoleLine.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("send")) {
            // send command
            this.handleCommand(this.consoleLine.getText());
        }
    }

}
