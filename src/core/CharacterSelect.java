package core;

import characters.Adamus;
import characters.Deia;
import characters.Flamara;
import characters.Paopao;
import characters.Terra;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ui.CharacterSelectUI;

public class CharacterSelect extends Selection {

    private JRadioButton paopao, terra, flammara, adamus, deia;
    private CharacterSelectUI ui;

    public CharacterSelect(GamePanel gamePanel, Runnable onBack) {
        super(gamePanel, onBack);
    }

    @Override
    public JPanel createSelectionButtons() {
        paopao = createBtn(0);
        terra = createBtn(1);
        flammara = createBtn(2);
        adamus = createBtn(3);
        deia = createBtn(4);

        JRadioButton[] buttons = { paopao, terra, flammara, adamus, deia };
        ButtonGroup group = new ButtonGroup();
        for (JRadioButton btn : buttons)
            group.add(btn);

        ui = new CharacterSelectUI(buttons, null);
        return ui.getPanel();
    }

    private JRadioButton createBtn(int index) {
        JRadioButton btn = new JRadioButton() {
            @Override
            public boolean contains(int x, int y) {
                return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
            }
        };
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);

        btn.addActionListener(e -> ui.setSelectedIndex(index));

        return btn;
    }

    @Override
    public JPanel createBackground() {
        JPanel background = new JPanel(null) {
            private final Image img = new ImageIcon("assets/chooseCharacterBackground.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JPanel selection = createSelectionButtons();
        JPanel nav = super.createNavButtons();

        background.add(selection);
        background.add(nav);

        background.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = background.getWidth();
                int h = background.getHeight();
                selection.setBounds(0, 0, w, h - 100);
                nav.setBounds(0, h - 100, w, 100);
                ui.layoutAll();
            }
        });

        SwingUtilities.invokeLater(() -> {
            int w = background.getWidth();
            int h = background.getHeight();
            if (w > 0 && h > 0) {
                selection.setBounds(0, 0, w, h - 100);
                nav.setBounds(0, h - 100, w, 100);
                ui.layoutAll();
            }
        });

        return background;
    }

    protected Player getSelectedPlayer() {
        if (paopao.isSelected())
            return new Paopao(0, 0);
        if (terra.isSelected())
            return new Terra(0, 0);
        if (flammara.isSelected())
            return new Flamara(0, 0);
        if (adamus.isSelected())
            return new Adamus(0, 0);
        if (deia.isSelected())
            return new Deia(0, 0);
        return null;
    }

    @Override
    protected void onNext() {
        if (ui.isCharacterConfirmed()) {
            getGamePanel().showMapSelect(getSelectedPlayer());
        }
    }

    @Override
    public boolean validateSelection() {
        return ui.isCharacterConfirmed();
    }
}