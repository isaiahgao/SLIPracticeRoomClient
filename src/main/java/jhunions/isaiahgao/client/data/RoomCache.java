package jhunions.isaiahgao.client.data;

import java.time.Month;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import jhunions.isaiahgao.client.Main;
import jhunions.isaiahgao.client.SoundHandler.Sound;
import jhunions.isaiahgao.common.User;
import jhunions.isaiahgao.common.UserInstance;

/**
 * Cache current state client side 
 * @author Me
 *
 */
public class RoomCache {
	
	// time limit in minutes
	private static final int TIME_LIMIT = 90;

    public enum ActionResult {
        LOG_OUT,
        LOG_IN;
    }

    public RoomCache(Main instance) {
        this.instance = instance;
        this.disabledRooms = new HashMap<>();
        
        Main.TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RoomCache.this.currentUsers.forEach((s, u) -> {
                    RoomCache.this.instance.getBaseGUI().setTimeForRoom(u.getRoom(), u.getTimeRemaining());
                });
            }
        }, 1l, 30000l);
    }

    private Main instance;
    
    // user string id : userinstance
    private Map<String, UserInstance> currentUsers;
    // room : reason for disabling
    private Map<Integer, String> disabledRooms;
    private volatile int logsize;
    private Month month;
    
    private synchronized void incLog() {
        this.logsize++;
    }
    
    /**
     * Handle an ID scan.
     * @param id The user's string ID.
     * @param room The room the user is checking out, or 0 if none.
     */
    public synchronized void scan(User usd, int room) {
        UserInstance inst = this.currentUsers.get(usd.getHopkinsID());
        if (inst != null) {
            Sound.SIGN_OUT.play();
            instance.getBaseGUI().getButtonByID(inst.getRoom()).setEnabled(true);
            instance.getBaseGUI().setTimeForRoom(inst.getRoom(), null);
            instance.sendDisappearingConfirm("Returned<br>Practice Room " + inst.getRoom() + "!", 115);
            this.logout(inst);
            return;
        }

        Sound.SIGN_IN.play();
        instance.getBaseGUI().getButtonByID(room).setEnabled(false);
        instance.sendDisappearingConfirm("Checked out<br>Practice Room " + this.instance.getBaseGUI().getPressedButtonID() + "!", 115);
        this.login(usd, room);
        return;
    }
    
    public synchronized UserInstance getUserInstance(String id) {
        return this.currentUsers.get(id);
    }
    
    public synchronized boolean usingRoom(String id) {
        return this.currentUsers.containsKey(id);
    }
    
    public void disableRoom(int room, String reason) {
        this.disabledRooms.put(room, reason);
    }
    
    public synchronized void removeRoom(int room) {
        this.disabledRooms.remove(room);
        for (Iterator<UserInstance> it = currentUsers.values().iterator(); it.hasNext();) {
            if (it.next().getRoom() == room) {
                it.remove();
                break;
            }
        }
    }
    // log in
    private void login(User usd, int room) {
        UserInstance inst = new UserInstance(usd, room, TIME_LIMIT);
        currentUsers.put(usd.getHopkinsID(), inst);
        instance.getBaseGUI().setTimeForRoom(room, inst.getTimeRemaining());
    }
    
    // log out
    private void logout(UserInstance inst) {
        currentUsers.remove(inst.getUser().getHopkinsID());
    }
    
}
