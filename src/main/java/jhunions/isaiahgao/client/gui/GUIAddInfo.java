package jhunions.isaiahgao.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jhunions.isaiahgao.client.Main;

public abstract class GUIAddInfo extends GUI implements ActionListener {
    
    private static final long serialVersionUID = 4067939367164395753L;

    /**
     * Constructor.
     * @param instance Instance of program.
     * @param base Base GUI pointer.
     * @param name Title on top of the GUI.
     * @param userId ID of the user opening the menu.
     * @param line What the text description line should say.
     * @param confirm What the "OK" button should say.
     */
    public GUIAddInfo(Main instance, GUIBase base, String name, String userId, String line, String confirm) {
        super(instance, name, 800, 1000, JFrame.DISPOSE_ON_CLOSE, true);
        this.frame.setAlwaysOnTop(true);
        this.line = line;
        this.confirm = confirm;
        this.userId = userId;
        this.base = base;
        
        this.buttonOK.setText(confirm);
        this.textTitle.setText(line);
    }
    
    protected String line = "Register with JHUnions";
    protected String confirm = "Register";
    protected String userId;
    
    protected GUIBase base;
    
    protected JButton buttonCancel;
    protected JLabel textTitle;
    protected JLabel textFirstName;
    protected JLabel textLastName;
    protected JLabel textJHED;
    protected JLabel textPhoneNumber;
    protected JTextField promptFirstName;
    protected JTextField promptLastName;
    protected JTextField promptJHED;
    protected JTextField promptPhoneNumber;
    protected JButton buttonOK;

    @Override
    protected void setup() {
        //construct components
        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(this);
        buttonCancel.setActionCommand("cancel");
        buttonCancel.setBackground(Color.WHITE);
        
        textTitle = new JLabel(line);
        textFirstName = new JLabel("First Name");
        textLastName = new JLabel("Last Name");
        textJHED = new JLabel("JHED (e.g. jsmith41)");
        textPhoneNumber = new JLabel("Phone Number (no dashes)");
        
        promptFirstName = new JTextField(5);
        promptLastName = new JTextField(5);
        promptJHED = new JTextField(5);
        promptPhoneNumber = new JTextField(5);
        
        buttonOK = new JButton(confirm);
        buttonOK.addActionListener(this);
        buttonOK.setActionCommand("ok");
        buttonOK.setBackground(Color.WHITE);

        //adjust size and set layout
        setPreferredSize(new Dimension(369, 382));
        setLayout(null);

        //add components
        add(buttonCancel);
        add(textTitle);
        add(textFirstName);
        add(textLastName);
        add(textJHED);
        add(textPhoneNumber);
        add(promptFirstName);
        add(promptLastName);
        add(promptJHED);
        add(promptPhoneNumber);
        add(buttonOK);

        //set component bounds(only needed by Absolute Positioning)
        textTitle.setBounds(0, 20, 370, 100);
        textFirstName.setBounds(30, 180, 100, 25);
        textLastName.setBounds(30, 215, 100, 25);
        textJHED.setBounds(30, 255, 120, 25);
        textPhoneNumber.setBounds(30, 290, 175, 25);
        
        promptFirstName.setBounds(100, 180, 255, 25);
        promptLastName.setBounds(100, 215, 255, 25);
        promptJHED.setBounds(150, 255, 205, 25);
        promptPhoneNumber.setBounds(195, 290, 160, 25);
        
        buttonCancel.setBounds(230, 325, 125, 45);
        buttonOK.setBounds(10, 325, 215, 45);
    }
    
    protected boolean argsFilled() {
        return !promptFirstName.getText().isEmpty()
                && !promptLastName.getText().isEmpty()
                && !promptJHED.getText().isEmpty()
                && !promptPhoneNumber.getText().isEmpty();
    }

}
