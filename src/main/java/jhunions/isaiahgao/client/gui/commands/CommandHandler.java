package jhunions.isaiahgao.client.gui.commands;

import java.util.HashMap;
import java.util.Map;

import jhunions.isaiahgao.client.IO;
import jhunions.isaiahgao.client.Main;

public class CommandHandler {
    
    public CommandHandler(Main instance) {
        this.instance = instance;
        this.commands = new HashMap<>();
        this.load();
    }

    private Main instance;
    private Map<String, Command> commands;
    
    private void load() {
        Command enable = (args) -> {
            if (args.length < 2) {
                return "Not enough args. > enable [RoomNo]";
            }
            
            try {
                int room = Integer.parseInt(args[1]);
                boolean successful = IO.enableRoom(room).get();
                if (successful) {
	                instance.getBaseGUI().getButtonByID(room).setEnabled(true);
	                instance.getBaseGUI().setTimeForRoom(room, -1);
	                return "Enabled room " + args[1];
                }
                return "ERROR: failed to enable room " + args[1] + ". Maybe the internet is down?";
            } catch (NumberFormatException e) {
                return "Invalid room specified: " + args[1];
            } catch (Exception e) {
                e.printStackTrace();
                return "Unknown error: " + e.getClass().getSimpleName();
            }
        };
        commands.put("enable", enable);
        commands.put("e", enable);
        
        Command disable = (args) -> {
            if (args.length < 2) {
                return "Not enough args. > disable [RoomNo] <reason?>";
            }
            
            try {
                int room = Integer.parseInt(args[1]);
                
                String reason = args.length < 3 ? null : args[2];
                if (args.length < 3) {
                    reason = null;
                } else {
                    reason = args[2];
                    for (int i = 3; i < args.length; i++) {
                        reason += " " + args[i];
                    }
                }
                
                boolean successful = IO.disableRoom(room, reason).get();

                if (successful) {
	                instance.getBaseGUI().getButtonByID(room).setEnabled(false);
	                instance.getBaseGUI().setTimeForRoom(room, 9999, reason);
	                return "Disabled room " + args[1] + " with reason \"" + reason + "\"";
                }
                return "ERROR: failed to disable room " + args[1] + ". Maybe the internet is down?";
            } catch (NumberFormatException e) {
                return "Invalid room specified: " + args[1];
            } catch (Exception e) {
                e.printStackTrace();
                return "Unknown error: " + e.getClass().getSimpleName();
            }
        };
        commands.put("disable", disable);
        commands.put("d", disable);
        
        Command refresh = (args) -> {
        	instance.getBaseGUI().synchronize();
            return "Refreshed rooms";
        };
        commands.put("refresh", refresh);
        commands.put("synchronize", refresh);
        commands.put("r", refresh);
        commands.put("s", refresh);
        
        Command help = (args) -> {
            return "enable [ROOM] - enable a room." + System.lineSeparator() + "disable [ROOM] <reason?> - disable a room." + System.lineSeparator() + "refresh - refreshes checkouts in case of manual checking in of keys.";
        };
        commands.put("help", help);
        commands.put("?", help);
        commands.put("h", help);
    }
    
    public String perform(String cmd) {
        if (!cmd.isEmpty()) {
            String[] args = cmd.split(" ");
            Command lbl = commands.get(args[0]);
            if (lbl != null) {
                return lbl.perform(args);
            }
        }
        return "Invalid command. Use \"help\" for a list of commands.";
    }

}
