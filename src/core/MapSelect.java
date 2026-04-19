package core;

import java.awt.*;
import javax.swing.*;
import gameobjects.Player;

public class MapSelect extends Selection {

    private GamePanel gamePanel;
    private Player selectedPlayer;
    private JRadioButton lireo, hathoria, adamya, sapiro, mineave;

    public MapSelect(GamePanel gamePanel, Runnable onBack, Player selectedPlayer) {
        super(gamePanel, onBack);
        this.selectedPlayer = selectedPlayer;
    }

    @Override
    public JPanel createSelectionButtons() {
        lireo  = new JRadioButton("Lireo");
        hathoria = new JRadioButton("Hathoria");
        adamya = new JRadioButton("Adamya");
        sapiro = new JRadioButton("Sapiro");
        mineave = new JRadioButton("Mineave");

        for (JRadioButton btn : new JRadioButton[]{lireo, hathoria, adamya, sapiro, mineave}) {
            btn.setOpaque(false);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        ButtonGroup group = new ButtonGroup();
        for (JRadioButton btn : new JRadioButton[]{lireo, hathoria, adamya, sapiro, mineave}) {
            group.add(btn);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        panel.add(lireo);
        panel.add(Box.createHorizontalStrut(40));
        panel.add(hathoria);
        panel.add(Box.createHorizontalStrut(40));
        panel.add(adamya);
        panel.add(Box.createHorizontalStrut(40));
        panel.add(sapiro);
        panel.add(Box.createHorizontalStrut(40));
        panel.add(mineave);

        wrapper.add(panel);
        return wrapper;
    }

    public void onNext(){
        getGamePanel().startLevel(selectedPlayer);
    }



    public boolean validateSelection() {
        return lireo.isSelected()   || hathoria.isSelected()  || adamya.isSelected() || sapiro.isSelected() ||mineave.isSelected();
    }
}