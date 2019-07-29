package jhunions.isaiahgao.client;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Date;

public class Utils {
    
    private static final DecimalFormat DF = new DecimalFormat("00");

    public static Object getField(Class<?> clazz, Object o, String field) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String format(String msg) {
        return format(msg, 16, "verdana", false);
    }
    
    public static String format(String msg, int fontSize, String fontStyle) {
        return format(msg, fontSize, fontStyle, false);
    }
    //<span style='font-size:20px'
    public static String format(String msg, int fontSize, String fontStyle, boolean center) {
        return "<html>" + (center ? "<center>" : "") + "<font face=\"" + fontStyle + "\"><span style='font-size:" + fontSize + "px'>" + msg.replace("\\n", "<br>") + "</span></font>" + (center ? "</center>" : "") + "</html>";
    }
    
    public static String capitalizeFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    public static String getTime(Date d) {
        String suffix = " AM";
        int hours = d.getHours();
        int mins = d.getMinutes();
        int secs = d.getSeconds();
        if (hours > 11) {
            suffix = " PM";
            hours -= 12;
        }
        
        if (hours == 0) {
            hours = 12;
        }
        
        return hours + ":" + DF.format(mins) + ":" + DF.format(secs) + suffix;
    }
    
    public static boolean isValidPhone(long arg) {
        String strl = "" + arg;
        // make sure length i sproper
        if (strl.length() != 10) {
            return false;
        }
        
        // must not be a string of 1 character
        for (int i = 1; i < 10; i++) {
            if (strl.charAt(i) != strl.charAt(i - 1)) {
                return true;
            }
        }
        return false;
    }

}
