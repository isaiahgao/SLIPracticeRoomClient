package jhunions.isaiahgao.client.data;

import jhunions.isaiahgao.client.gui.GUIBase;

public class InputCollector {
    
	// used to know when to stop collecting input
    private static final int ID_LENGTH = 15;
    private static final int GRAD_ID_LENGTH = 11;

    public InputCollector(GUIBase base) {
        this.empty();
        this.base = base;
    }

    private boolean collecting;
    private boolean enabled;
    private boolean isGradStudent;
    
    private StringBuilder buf;
    private long lastCollected = -1;
    private long lastKeystroke = -1;
    
    private GUIBase base;
    private boolean manual;

    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void toggleManual() {
        this.manual = !this.manual;
        this.base.getManualLabel().setVisible(this.manual);
    }

    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    public boolean isCollecting() {
        return this.collecting;
    }

    public void setCollecting(boolean b) {
        this.collecting = b;
        if (b) {
            this.empty();
            this.lastCollected = System.currentTimeMillis();
        } else {
            this.lastCollected = -1;
        }
    }

    /**
     * @return whether or not the collector can hold more input.
     */
    public boolean add(String s) {
        if (!enabled)
            return true;
        
        // start collecting
        if (!collecting || (!this.manual && System.currentTimeMillis() - this.lastCollected > 100))
            this.setCollecting(true);

        // only append if the entry came very quickly; otherwise we know its someone trying to type it in
        if (this.manual && System.currentTimeMillis() - lastKeystroke < 10) {
            // cancel manual entry if ID is swiped
            this.manual = false;
            this.base.getManualLabel().setVisible(this.manual);
            this.empty();
            this.lastCollected = System.currentTimeMillis();
        }
        
        // check if grad student id
        if (buf.length() == 4 && s.equals("-")) {
            this.isGradStudent = true;
        } else {
            buf.append(s);
        }
        lastKeystroke = System.currentTimeMillis();
        
        // for some reason grad students need the last letter removed
        if (this.isGradStudent && buf.length() == GRAD_ID_LENGTH) {
            this.buf.delete(GRAD_ID_LENGTH - 1, GRAD_ID_LENGTH);
            this.isGradStudent = false;
            return false;
        }
        
        // append a 0 for legacy compatibility
        // normally the last digit is the version of the ID;
        // it changes every time u ask for a new one, but we dont
        // care so set it to 0 arbitrarily.
        if (buf.length() == ID_LENGTH) {
            buf.append("0");
            this.isGradStudent = false;
            return false;
        }
        return true;
    }

    public boolean add(char c) {
        return this.add("" + c);
    }

    public boolean add(int i) {
        return this.add("" + i);
    }

    // reset the buffer
    public void empty() {
        this.buf = new StringBuilder(30);
        this.isGradStudent = false;
        this.lastCollected = -1;
    }

    @Override
    public String toString() {
        return this.buf.toString();
    }

    public boolean isEmpty() {
        return this.buf.length() == 0;
    }

}
