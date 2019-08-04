package jhunions.isaiahgao.client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import com.fasterxml.jackson.databind.JsonNode;

import jhunions.isaiahgao.client.Graphics.PracticeRoomButton;
import jhunions.isaiahgao.client.IO;
import jhunions.isaiahgao.client.IO.RequestType;
import jhunions.isaiahgao.client.IO.Response;
import jhunions.isaiahgao.client.Main;
import jhunions.isaiahgao.client.SoundHandler.Sound;
import jhunions.isaiahgao.client.Utils;
import jhunions.isaiahgao.client.data.InputCollector;
import jhunions.isaiahgao.client.listener.PRButtonListener;
import jhunions.isaiahgao.common.ScanResultPacket;
import jhunions.isaiahgao.common.User;

public class GUIBase extends GUI implements ActionListener {
    
    public static void main(String[] args) {
    	Main main = new Main();
        GUIBase base = new GUIBase(main);
    	try {
    		Field f = Main.class.getDeclaredField("base");
    		f.setAccessible(true);
    		f.set(main, base);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        base.buttonPressed = 112;
        base.scanID("6010675001431720");
    }

    private static final long serialVersionUID = 2161473071392557910L;

    public GUIBase(Main instance) {
        super(instance, "JHUnions Practice Rooms v1.1.0", 1280, 1024, JFrame.EXIT_ON_CLOSE, true);
        this.setBackground(new Color(18, 18, 42));
        //this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.synchronize();
    }

    private InputCollector in;

    //private JButton register;
    //private JButton useOnce;

    private Map<Integer, JButton> buttons;
    private int buttonPressed;

    private JLabel textStepOne;
    private JLabel textStepOneInfo;
    private JLabel textStepTwo;
    private JLabel textStepTwoInfo;
    private JLabel isManual;
    private JButton refreshButton;
    
    private long lastSync;
    private String curId;
    
    public void synchronize() {
        // sync with server
        try {
        	Response response = IO.sendRequest(RequestType.PUT, "/rooms");
        	if (response.code != 200) {
        		throw new IllegalStateException();
        	}
        	
        	JsonNode json = Main.getJson().readTree(response.result).get("rooms");
        	for (Iterator<Map.Entry<String, JsonNode>> it = json.fields(); it.hasNext();) {
        		Map.Entry<String, JsonNode> entry = it.next();
        		
        		JsonNode valuenode = entry.getValue();
        		int value = valuenode.get("remaining").asInt();
        		String reason = valuenode.has("reason") ? valuenode.get("reason").asText() : null;
        		this.setTimeForRoom(Integer.parseInt(entry.getKey()), value, reason);
        	}
        } catch (Exception e) {
            //this.instance.sendMessage("Failed to connect to server.<br>Vital functions will not work.");
        	e.printStackTrace();
        }
    }
    
    public JLabel getManualLabel() {
        return this.isManual;
    }
    
    public String getCurrentId() {
        return curId;
    }

    public JButton getButtonByID(int num) {
        return this.buttons.get(num);
    }

    public int getPressedButtonID() {
        return this.buttonPressed;
    }

    public JButton getPressedButton() {
        return this.buttonPressed == 0 ? null : this.buttons.get(this.buttonPressed);
    }

    public void setPressedButton(JButton butt) {
        if (this.buttonPressed != 0) {
            JButton curbutt = this.getPressedButton();
            curbutt.setSelected(false);
            curbutt.validate();
        }
        this.buttonPressed = butt == null ? 0 : Integer.parseInt(butt.getName());
        this.toggleTextVisibility(this.buttonPressed == 0);
    }
    
    public void setButtonEnabled(int button, boolean enabled) {
        this.buttons.get(button).setEnabled(enabled);
    }
    
    public void scanID(String id) {
        System.out.println("Scanned ID: " + id);
        this.curId = id;
        
        // handle special buttons
        // this one is unused
        if (this.getPressedButtonID() == -1) {
            if (IO.getUserData(this.getCurrentId()) == null) {
                this.instance.sendMessage("You can't remove your info from our database\\nbecause you're not registered with JHUnions!");
                this.setPressedButton(null);
                return;
            }
            
            // send confirmation about unregistering
            this.instance.sendConfirm("Are you sure you want to unregister?", null, new Runnable() {
                @Override
                public void run() {
                	IO.removeUserData(GUIBase.this.getCurrentId());
                    GUIBase.this.setPressedButton(null);
                }
            });
            return;
        }
        
        // update user info button
        if (this.getPressedButtonID() == -2) {
            System.out.println("curid: " + this.getCurrentId());
            // update user info
            if (IO.getUserData(this.getCurrentId()) == null) {
                // if they're not in system, prompt to register instead
                new GUIAddInfoRegister(this.instance, this.getCurrentId(), this);
            } else {
                new GUIAddInfoUpdate(this.instance, this.getCurrentId(), this);
            }
            this.setPressedButton(null);
            return;
        }

        // otherwise, try to sign in
        ScanResultPacket resultpacket;
        try {
        	resultpacket = IO.scanId(id, this.buttonPressed + "").get();
        } catch (Exception e) {
        	resultpacket = ScanResultPacket.ERROR;
        }
        
        switch (resultpacket.getResult()) {
	        case NO_SUCH_USER:
	        	new GUIPromptRegister(this.instance, this);
	        	break;
			case CHECKED_OUT:
		        Sound.SIGN_IN.play();
		        this.getButtonByID(this.buttonPressed).setEnabled(false);
	            this.setTimeForRoom(this.buttonPressed, 90);
		        instance.sendDisappearingConfirm("Checked out<br>Practice Room " + this.instance.getBaseGUI().getPressedButtonID() + "!", 115);
	            this.setPressedButton(null);
				break;
			case CHECKED_IN:
	            Sound.SIGN_OUT.play();
	            int room = Utils.parseInt(resultpacket.getData(), -1);
	            if (room == -1) {
		            Sound.ERROR.play();
		            this.instance.sendMessage("An unexpected error occured.<br>Please try again.", 65);
		            return;
	            }
	            
	            this.getButtonByID(room).setEnabled(true);
	            this.setTimeForRoom(room, -1);
	            instance.sendDisappearingConfirm("Returned<br>Practice Room " + room + "!", 115);
				break;
			case ERROR:
	            Sound.ERROR.play();
	            this.instance.sendMessage("An unexpected error occured.<br>Please check internet connection.", 65);
				break;
        }
    }
    
    /**
     * Do the action pressed by the current button.
     * THIS IS CURRENTLY UNUSED.
     * @param usd The user.
     */
    @Deprecated
    public void confirmAction(User usd) {
        if (this.getPressedButtonID() == 0) {
            return;
        }
        
        // handle special buttons
        // this one is unused
        if (this.getPressedButtonID() == -1) {
            if (IO.getUserData(this.getCurrentId()) == null) {
                this.instance.sendMessage("You can't remove your info from our database\\nbecause you're not registered with JHUnions!");
                this.setPressedButton(null);
                return;
            }
            
            // send confirmation about unregistering
            this.instance.sendConfirm("Are you sure you want to unregister?", null, new Runnable() {
                @Override
                public void run() {
                	IO.removeUserData(GUIBase.this.getCurrentId());
                    GUIBase.this.setPressedButton(null);
                }
            });
            return;
        }
        
        if (this.getPressedButtonID() == -2) {
            System.out.println("curid: " + this.getCurrentId());
            // update user info
            if (IO.getUserData(this.getCurrentId()) == null) {
                // if they're not in system, prompt to register instead
                new GUIAddInfoRegister(this.instance, this.getCurrentId(), this);
            } else {
                new GUIAddInfoUpdate(this.instance, this.getCurrentId(), this);
            }
            this.setPressedButton(null);
            return;
        }
        
        // sign in
        this.scanID(usd.getHopkinsID());
        
        // reset button selection
        this.setPressedButton(null);
    }

    @Override
    protected void setup() {
        // construct components
        this.setBackground(Color.WHITE);

        Dimension dim = this.frame.getSize();
        int w = (int) dim.getWidth();
        int h = (int) dim.getHeight();
        // anchor point for buttons
        int bsy = h / 4;

        int buttonHeightOffset = (h - bsy - 50) / 6;
        int buttonWidth = w / 2 - 100;
        int buttonHeight = buttonHeightOffset - 10;
        
        // add the refresh button
        /*try {
            this.refreshButton = new JButton(new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("/refresh.png"))));
            this.refreshButton.setBounds(10, 10, 50, 50);
            this.refreshButton.setBackground(Color.WHITE);
            this.refreshButton.setActionCommand("refresh");
            this.refreshButton.addActionListener(this);
            this.add(this.refreshButton);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
        //x, y, width, height
        this.addPracticeRoomButton(-2, Utils.format("<font color=\"white\">Update My Info</font>", 16, "Corbel"), 80, bsy - buttonHeight / 2, buttonWidth * 2 + 10, buttonHeight / 2 - 5);
        
        try {
            this.addPracticeRoomButton(109, 80, bsy, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(110, 80, bsy + buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(111, 80, bsy + 2 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(112, 80, bsy + 3 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(114, 80, bsy + 4 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(115, buttonWidth + 90, bsy, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(116, buttonWidth + 90, bsy + buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(117, buttonWidth + 90, bsy + 2 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(118, buttonWidth + 90, bsy + 3 * buttonHeightOffset, buttonWidth, buttonHeight);
            this.addPracticeRoomButton(119, buttonWidth + 90, bsy + 4 * buttonHeightOffset, buttonWidth, buttonHeight);
        } catch (Exception e) {
            // shouldnt happen
            throw new RuntimeException(e);
        }

        this.textStepOne = new JLabel(Utils.format("<font color=\"#8796d1\">Step ONE:</font>", 28, "arial black"));
        this.textStepOneInfo = new JLabel(Utils.format("<font color=\"f9faff\">Select Room!</font>", 72, "verdana"));
        this.textStepTwo = new JLabel(Utils.format("<font color=\"#8796d1\">Step TWO:</font>", 28, "arial black"));
        this.textStepTwoInfo = new JLabel(Utils.format("<font color=\"f9faff\">Swipe J-Card!</font>", 72, "verdana"));
        this.isManual = new JLabel("<html><font color=\"white\">MANUAL ID ENTRY</font></html>");

        // adjust size and set layout
        this.setPreferredSize(new Dimension(1280, 1024));
        this.setLayout(null);

        // add components
        //this.add(register);
        //this.add(useOnce);
        this.add(textStepOne);
        this.add(textStepOneInfo);
        this.add(textStepTwo);
        this.add(textStepTwoInfo);
        this.add(this.isManual);
        this.toggleTextVisibility(true);

        // set component bounds(only needed by Absolute Positioning)
        //this.register.setBounds(525, 210, 180, 85);
        //this.useOnce.setBounds(725, 210, 180, 85);
        this.textStepOne.setBounds(w * 3 / 8, 20, 500, 100);
        this.textStepOneInfo.setBounds(w / 4 - 20, 30, 1000, 200);
        
        this.textStepTwo.setBounds(w * 3 / 8, 20, 500, 100);
        this.textStepTwoInfo.setBounds(w / 4 - 40, 30, 1000, 200);
        
        this.isManual.setBounds(w - 150, 0, 150, 20);
        this.isManual.setVisible(false);

        // KeyListener kl = new JCardScanListener(this);
        // this.addKeyListener(kl);
        this.in = new InputCollector(this);
        this.in.setEnabled(true);
        this.setupKeyListener(this);
        this.setVisible(true);
        this.setFocusable(true);
    }
    
    private void toggleTextVisibility(boolean stepOne) {
        this.textStepOne.setVisible(stepOne);
        this.textStepOneInfo.setVisible(stepOne);
        this.textStepTwo.setVisible(!stepOne);
        this.textStepTwoInfo.setVisible(!stepOne);
    }

    private void setupKeyListener(JComponent c) {
        for (int i = 0; i < 10; i++) {
            c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(i + ""), i);
            c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                    .put(KeyStroke.getKeyStroke((int) Utils.getField(KeyEvent.class, null, "VK_NUMPAD" + i), 0), i);

            final int j = i;
            c.getActionMap().put(i, new AbstractAction() {
                private static final long serialVersionUID = 1148616746542133372L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!in.add(j)) {
                        GUIBase.this.scanID(in.toString());
                        in.setCollecting(false);
                    }
                }
            });
        }
        
        // manual
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(11 + ""), 11);
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, 0), 11);
        c.getActionMap().put(11, new AbstractAction() {
            private static final long serialVersionUID = 783045519366944315L;

            @Override
            public void actionPerformed(ActionEvent e) {
                in.toggleManual();
            }
        });

        // console
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(12 + ""), 12);
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 12);
        c.getActionMap().put(12, new AbstractAction() {
            private static final long serialVersionUID = 7830455674444315L;

            @Override
            public void actionPerformed(ActionEvent e) {
                new GUIConsole(instance);
            }
        });

        // dash for grad students
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(13 + ""), 13);
        c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), 13);
        c.getActionMap().put(13, new AbstractAction() {
            private static final long serialVersionUID = 78304556274444315L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!in.add("-")) {
                    GUIBase.this.scanID(in.toString());
                    in.setCollecting(false);
                }
            }
        });
    }
    
    private void addPracticeRoomButton(int id, String title, int x, int y, int width, int height) {
        if (this.buttons == null) {
            this.buttons = new HashMap<>();
        }

        JButton butt = new JButton(title, new ImageIcon(PracticeRoomButton.LONG_NORMAL.getImage()));
        butt.setPressedIcon(new ImageIcon(PracticeRoomButton.LONG_PRESSED.getImage()));
        butt.setSelectedIcon(new ImageIcon(PracticeRoomButton.LONG_SELECTED.getImage()));
        butt.setVerticalTextPosition(JButton.CENTER);
        butt.setHorizontalTextPosition(JButton.CENTER);
        
        butt.setActionCommand("select_" + id);
        butt.addActionListener(new PRButtonListener(this, butt));
        butt.setBounds(x, y, width, height);
        butt.setName(id + "");
        butt.setBorder(BorderFactory.createEmptyBorder());
        butt.setContentAreaFilled(false);
        //butt.setBackground(YELLOW);
        //butt.setBorder(new ButtonBorder(Color.DARK_GRAY, Color.BLACK, Color.GRAY, Color.WHITE));
        this.add(butt);
        this.buttons.put(id, butt);
        butt.setVisible(true);
    }

    private void addPracticeRoomButton(int roomNo, int x, int y, int width, int height) {
        if (this.buttons == null) {
            this.buttons = new HashMap<>();
        }

        JButton butt = new JButton(getTitle(roomNo, null), new ImageIcon(PracticeRoomButton.NORMAL_NORMAL.getImage()));
        butt.setPressedIcon(new ImageIcon(PracticeRoomButton.NORMAL_PRESSED.getImage()));
        butt.setSelectedIcon(new ImageIcon(PracticeRoomButton.NORMAL_SELECTED.getImage()));
        butt.setDisabledIcon(new ImageIcon(PracticeRoomButton.NORMAL_DISABLED.getImage()));
        butt.setVerticalTextPosition(JButton.CENTER);
        butt.setHorizontalTextPosition(JButton.CENTER);
        
        butt.setActionCommand("select_" + roomNo);
        butt.addActionListener(new PRButtonListener(this, butt));
        butt.setBounds(x, y, width, height);
        butt.setName(roomNo + "");
        //butt.setBackground(PRButtonListener.DESELECTED);
        //butt.setBorder(new ButtonBorder(Color.DARK_GRAY, Color.BLACK, Color.GRAY, Color.WHITE));
        butt.setBorder(BorderFactory.createEmptyBorder());
        butt.setContentAreaFilled(false);
        this.add(butt);
        this.buttons.put(roomNo, butt);
        butt.setVisible(true);
    }

    private String getTitle(int roomNo, String time) {
        String s = "" + roomNo;
        if (roomNo > 111)
            s += "\\nPIANO";
        else if (roomNo == 109)
            s += "\\nDRUM";
        
        if (time != null) {
            s += "\\n" + time;
        }
        return Utils.format("<font color=\"white\">" + s + "</font>", 20, "Corbel", true);
    }
    
    public void setTimeForRoom(int room, int minutesLeft) {
    	setTimeForRoom(room, minutesLeft, (String) null);
    }
    
    public void setTimeForRoom(int room, int minutesLeft, String optional) {
        JButton butt = this.getButtonByID(room);
        String amt = "";
        
		switch (minutesLeft) {
		case 0:
			amt = "<strong>TIME EXPIRED</strong>";
			butt.setEnabled(false);
			break;
		case -1:
			butt.setEnabled(true);
			break;
		case 9999:
			amt = "<strong>UNAVAILABLE</strong>" + (optional == null || optional.isEmpty() || optional.equals("null") ? "" : ": " + optional);
			butt.setEnabled(false);
			break;
		default:
	        String s = "<strong>Time remaining:</strong> ";
			amt = s + minutesLeft + " minute" + (minutesLeft > 1 ? "s" : "");
			butt.setEnabled(false);
			break;
		}
        
        butt.setText(this.getTitle(room, amt));
    }
    
    public void setTimeForRoom(int room, int minutesLeft, JsonNode optional) {
    	if (optional != null && optional.has("reason")) {
    		setTimeForRoom(room, minutesLeft, optional.get("reason"));
    		return;
    	}
    	setTimeForRoom(room, minutesLeft, (String) null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("refresh") && System.currentTimeMillis() - this.lastSync > 10000) {
            this.lastSync = System.currentTimeMillis();
            GUIBase.this.synchronize();
        }
    }

}
