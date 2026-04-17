package core;

import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CharacterSelect extends JPanel {

    private final GamePanel gamePanel;
    private final Runnable onBack;

    private JRadioButton paopao, terra, flammara, adamus, deia;

    public CharacterSelect(GamePanel gamePanel, Runnable onBack) {
        this.gamePanel = gamePanel;
        this.onBack    = onBack;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

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

    private JPanel createNavButtons() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(800, 100));

        int btnWidth = 140;
        int btnHeight = 60;
        int margin = 20;

        JButton backBtn = null;

        if (onBack != null) {
            backBtn = createImageButton("assets/backButton.png", btnWidth, btnHeight);
            backBtn.addActionListener(e -> onBack.run());
            panel.add(backBtn);
        }

        final JButton nextBtn = createImageButton("assets/nextButton.png", btnWidth, btnHeight);
        nextBtn.addActionListener((ActionEvent e) -> {
            if (!validateSelection()) {
                JOptionPane.showMessageDialog(CharacterSelect.this, "Please select a character!");
                return;
            }
            gamePanel.startLevel(getSelectedPlayer());
        });

        panel.add(nextBtn);

        JButton finalBackBtn = backBtn;

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int w = panel.getWidth();
                int h = panel.getHeight();

                if (finalBackBtn != null) {
                    finalBackBtn.setBounds(
                            margin,
                            h - btnHeight,
                            btnWidth,
                            btnHeight
                    );
                }

                nextBtn.setBounds(
                        w - btnWidth - margin,
                        h - btnHeight,
                        btnWidth,
                        btnHeight
                );
            }
        });

        return panel;
    }

    private JButton createImageButton(String path, int width, int height) {

    ImageIcon icon = new ImageIcon(path);
    Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

    JButton button = new JButton(new ImageIcon(scaled));

    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setOpaque(false);

    button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setLocation(button.getX(), button.getY() + 4);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setLocation(button.getX(), button.getY() - 4);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setLocation(button.getX(), button.getY());
            }
        });

        return button;
    }

    private Player getSelectedPlayer() {
        //if (paopao.isSelected())   return new Player("PaoPao");
        //if (terra.isSelected())    return new Player("Terra");
       // if (flammara.isSelected()) return new Player("Flammara");
       // if (adamus.isSelected())   return new Player("Adamus");
      //  if (deia.isSelected())     return new Player("Deia");
        return null;
    }

    private boolean validateSelection() {
        return paopao.isSelected()   || terra.isSelected()  || flammara.isSelected() || adamus.isSelected() ||deia.isSelected();
    }
}