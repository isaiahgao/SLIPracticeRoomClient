package jhunions.isaiahgao.client.gui;

import java.awt.event.ActionEvent;

import jhunions.isaiahgao.client.Main;
import jhunions.isaiahgao.client.Utils;
import jhunions.isaiahgao.common.FullName;
import jhunions.isaiahgao.common.User;

public class GUIAddInfoRegister extends GUIAddInfo {
    
    public static void main(String[] args) {
        Main main = new Main();
        new GUIAddInfoRegister(main);
    }

    private static final long serialVersionUID = -8885159911533375810L;

    public GUIAddInfoRegister(Main main) {
        super(main, main.getBaseGUI(), "Register", "123", Utils.format("Register with<br>JHUnions", 24, "Corbel", true), "Confirm");
    }
    
    public GUIAddInfoRegister(Main instance, String userId, GUIBase base) {
        super(instance, base, "Register", userId, Utils.format("Register with<br>JHUnions", 24, "Corbel", true), "Register");
    }
    
    @Override
    protected void setup() {
        super.setup();
        this.textTitle.setBounds(70, 30, 370, 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
            this.frame.dispose();
        } else if (e.getActionCommand().equals("ok")) {
            if (this.argsFilled()) {
                // only accept if they've filled all areas
                if (this.promptPhoneNumber.getText().length() != 10) {
                    this.instance.sendMessage("Invalid phone number.");
                    return;
                }
                
                try {
                    final User usd = new User(userId, new FullName(promptFirstName.getText(), promptLastName.getText()), promptJHED.getText(), Long.parseLong(promptPhoneNumber.getText()));
                    new GUIAcceptPolicy(this.instance, this, usd, true);
                } catch (Exception ex) {
                    this.instance.sendMessage("Invalid info. Please try again.");
                }
                return;
            }
            
            // otherwise send error message
            this.instance.sendMessage("Please fill out all fields.");
        }
    }

}
