package jhunions.isaiahgao.client.gui;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import jhunions.isaiahgao.client.IO;
import jhunions.isaiahgao.client.Main;
import jhunions.isaiahgao.client.Utils;
import jhunions.isaiahgao.client.SoundHandler.Sound;
import jhunions.isaiahgao.common.FullName;
import jhunions.isaiahgao.common.User;

public class GUIAddInfoUpdate extends GUIAddInfo {

    private static final long serialVersionUID = -888515991153375810L;

    public GUIAddInfoUpdate(Main instance, String userId, GUIBase base) {
        super(instance, base, "Update my Information", userId, Utils.format("Update my Information", 24, "Corbel", true), "OK");
        this.postSetup();
    }
    
    @Override
    protected void setup() {
        super.setup();
        this.textTitle.setBounds(70, 30, 370, 100);
    }
    
    private void postSetup() {
        User usd;
		try {
			usd = IO.getUserData(this.userId).get();
	        this.promptFirstName.setText(usd.getName().getFirstName());
	        this.promptLastName.setText(usd.getName().getLastName());
	        this.promptJHED.setText(usd.getJhed());
	        this.promptPhoneNumber.setText(Long.toString(usd.getPhone()));
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
            this.frame.dispose();
        } else if (e.getActionCommand().equals("ok")) {
            if (this.argsFilled()) {
                // only accept if they've filled all areas
                try {
                    User usd = new User(userId, new FullName(promptFirstName.getText(), promptLastName.getText()), promptJHED.getText(), Long.parseLong(promptPhoneNumber.getText()));
                    String error = usd.checkForErrors();
                    if (error != null) {
                        this.instance.sendMessage("<strong>Registration failed:</strong><br>" + error, 100);
                        return;
                    }

                	if (!IO.push(usd).get()) {
                        this.instance.sendDisappearingConfirm("Failed to update information.<br>Maybe the internet is down?", 40);
                        Sound.ERROR.play();
                        return;
                	}
                	
                    // TODO call add function from base
                    this.instance.sendMessage("You have successfully updated your information!", this.frame);
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
