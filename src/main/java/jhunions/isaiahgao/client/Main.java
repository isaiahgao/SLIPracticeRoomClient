package jhunions.isaiahgao.client;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import com.fasterxml.jackson.databind.ObjectMapper;

import jhunions.isaiahgao.client.gui.GUI;
import jhunions.isaiahgao.client.gui.GUIBase;
import jhunions.isaiahgao.client.gui.GUIConfirm;
import jhunions.isaiahgao.client.gui.GUIMessage;
import jhunions.isaiahgao.client.gui.commands.CommandHandler;

public final class Main {

    private static CommandHandler handlerCommands;
    private static Main instance;
    
    public static final Timer TIMER = new Timer();

    public static void main(String[] args) {
        instance = new Main();
        instance.base = new GUIBase(instance);

        handlerCommands = new CommandHandler(instance);

        Main.TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                instance.getBaseGUI().synchronize();
            }
        }, 10000l, 10000l);
    }
    
    public static CommandHandler getCommandHandler() {
        return handlerCommands;
    }
    
    public Main() {
    	
    }
    
    private static AuthHandler auth = new AuthHandler();
    private GUIBase base;
    
    public static AuthHandler getAuthHandler() {
    	return auth;
    }
    
    public GUI sendMessage(String s, int xbound) {
        return new GUIMessage(this, Utils.format(s, 12, "verdana", true), null, null, xbound);
    }
    
    public GUI sendMessage(String s) {
        return this.sendMessage(s, null);
    }
    
    public GUI sendMessage(String s, JFrame todispose) {
        return this.sendMessage(s, todispose, null);
    }
    
    public GUI sendMessage(String s, JFrame todispose, Runnable runafter) {
        return new GUIMessage(this, Utils.format(s, 12, "verdana", true), todispose, runafter);
    }
    
    public GUI sendConfirm(String s, JFrame todispose, Runnable runafter) {
        return new GUIConfirm(this, Utils.format(s, 12, "verdana", true), todispose, runafter);
    }
    
    public void sendDisappearingConfirm(String msg, int xbound) {
        final GUI popup = this.sendMessage(msg, xbound);
        
        Main.TIMER.schedule(new TimerTask() {
            @Override
            public void run() {
                popup.getFrame().dispose();
            }
        }, 3000L);
    }
    
    public GUIBase getBaseGUI() {
        return base;
    }

    private static ObjectMapper json = new ObjectMapper();
    public static ObjectMapper getJson() {
        return json;
    }

}
