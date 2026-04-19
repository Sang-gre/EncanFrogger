package core;

import java.awt.*;
import javax.swing.*;

import characters.Adamus;
import characters.Deia;
import characters.Flamara;
import characters.Paopao;
import characters.Terra;
import gameobjects.Player;

public class CharacterSelect extends Selection {

    private JRadioButton paopao, terra, flammara, adamus, deia;

    public CharacterSelect(GamePanel gamePanel, Runnable onBack) {
        super(gamePanel, onBack);
    }

    @Override
    public JPanel createSelectionButtons() {
        paopao  = new JRadioButton("PaoPao");
        terra = new JRadioButton("Terra");
        flammara = new JRadioButton("Flammara");
        adamus = new JRadioButton("Adamus");
        deia = new JRadioButton("Deia");

        for (JRadioButton btn : new JRadioButton[]{paopao, terra, flammara, adamus, deia}) {
            btn.setOpaque(false);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }

        ButtonGroup group = new ButtonGroup();
        for (JRadioButton btn : new JRadioButton[]{paopao, terra, flammara, adamus, deia}) {
            group.add(btn);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        panel.add(paopao);
        panel.add(Box.createHorizontalStrut(40));
        panel.add(terra);
        panel.add(Box.createHorizontalStrut(40));
        panel.add(flammara);
        panel.add(Box.createHorizontalStrut(40));
        panel.add(adamus);
        panel.add(Box.createHorizontalStrut(40));
        panel.add(deia);

        wrapper.add(panel);
        return wrapper;
    }

    protected Player getSelectedPlayer() {
        if (paopao.isSelected())   return new Paopao(0, 0);
        if (terra.isSelected())    return new Terra(0, 0);
        if (flammara.isSelected()) return new Flamara(0, 0);
        if (adamus.isSelected())   return new Adamus(0, 0);
        if (deia.isSelected())     return new Deia(0, 0);
        return null;
    }

    @Override
    protected void onNext(){
        Player selected = getSelectedPlayer();
        getGamePanel().showMapSelect(selected);
    }

    @Override
    public boolean validateSelection() {
        return paopao.isSelected()   || terra.isSelected()  || flammara.isSelected() || adamus.isSelected() ||deia.isSelected();
    }
}