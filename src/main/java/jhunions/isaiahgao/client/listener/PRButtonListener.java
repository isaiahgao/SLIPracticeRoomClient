package jhunions.isaiahgao.client.listener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import jhunions.isaiahgao.client.SoundHandler.Sound;
import jhunions.isaiahgao.client.gui.GUIBase;

public class PRButtonListener implements ActionListener {

    public static final Color SELECTED = new Color(159, 255, 165);
    public static final Color DESELECTED = Color.WHITE;

    public PRButtonListener(GUIBase parent, JButton butt) {
        this.parent = parent;
        this.butt = butt;
    }

    private GUIBase parent;
    private JButton butt;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.butt.isSelected()) {
            this.parent.setPressedButton(null);
            this.butt.setSelected(false);
            //this.butt.setBackground(DESELECTED);
            this.butt.validate();
            return;
        }

        this.butt.setSelected(true);
        //this.butt.setBackground(SELECTED);
        this.butt.validate();
        
        try {
            Sound.valueOf("R" + this.butt.getName()).play();
        } catch (Exception ex) {
            // ignore
        }
        this.parent.setPressedButton(this.butt);
    }

}
