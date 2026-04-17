package core;

import gameobjects.Player;
import java.awt.*;
import javax.swing.*;

public class CharacterSelect extends JPanel {

    private final GamePanel gamePanel;
    private final Runnable onBack;

    private JRadioButton paopao, terra, flammara, adamus, deia;

    public CharacterSelect(GamePanel gamePanel, Runnable onBack) {
        this.gamePanel = gamePanel;
        this.onBack    = onBack;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600)); // match your window size

        add(createBackground(), BorderLayout.CENTER);
    }

    private JPanel createBackground() {
        JPanel background = new JPanel(new BorderLayout()) {
            private final Image img = new ImageIcon("assets/chooseCharacterBackground.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        background.setOpaque(true);
        background.add(createCharacterButtons(), BorderLayout.CENTER);
        background.add(createNavButtons(), BorderLayout.SOUTH);

        return background;
    }

    private JPanel createCharacterButtons() {
        paopao   = new JRadioButton("PaoPao");
        terra    = new JRadioButton("Terra");
        flammara = new JRadioButton("Flammara");
        adamus   = new JRadioButton("Adamus");
        deia     = new JRadioButton("Deia");

        // Make radio button backgrounds transparent
        for (JRadioButton btn : new JRadioButton[]{paopao, terra, flammara, adamus, deia}) {
            btn.setOpaque(false);
            btn.setForeground(Color.WHITE);
        }

        ButtonGroup group = new ButtonGroup();
        for (JRadioButton btn : new JRadioButton[]{paopao, terra, flammara, adamus, deia}) {
            group.add(btn);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        // Center the buttons vertically in the middle of the screen
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

    private JPanel createNavButtons() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (onBack != null) {
            JButton backBtn = new JButton("Back");
            backBtn.setFocusPainted(false);
            backBtn.addActionListener(e -> onBack.run());
            panel.add(backBtn, BorderLayout.WEST);
        }

        JButton nextBtn = new JButton("Next");
        nextBtn.setFocusPainted(false);
        nextBtn.addActionListener(e -> {
            if (!validateSelection()) {
                JOptionPane.showMessageDialog(this, "Please select a character!");
                return;
            }
            gamePanel.startLevel(getSelectedPlayer());
        });
        panel.add(nextBtn, BorderLayout.EAST);

        return panel;
    }

    private Player getSelectedPlayer() {
        // if (paopao.isSelected())   return new Player("PaoPao");
        // if (terra.isSelected())    return new Player("Terra");
        // if (flammara.isSelected()) return new Player("Flammara");
        // if (adamus.isSelected())   return new Player("Adamus");
        // if (deia.isSelected())     return new Player("Deia");
        return null; // should never happen due to validation
    }

    private boolean validateSelection() {
        return paopao.isSelected()   || terra.isSelected()  ||
               flammara.isSelected() || adamus.isSelected() ||
               deia.isSelected();
    }
}