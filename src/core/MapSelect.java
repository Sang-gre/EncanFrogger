package core;

import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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

    @Override
    public JPanel createBackground() {
        JPanel background = new JPanel(new BorderLayout()) {
            private final Image img = new ImageIcon("assets/mapSelectBackground.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        background.setOpaque(true);
        background.add(createSelectionButtons(), BorderLayout.CENTER);
        background.add(createNavButtons(), BorderLayout.SOUTH);

        return background;
    }

    protected JPanel createNavButtons() {
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
                JOptionPane.showMessageDialog(MapSelect.this, "Please select a character!");
                return;
            }
            onNext();
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

    public boolean validateSelection() {
        return lireo.isSelected()   || hathoria.isSelected()  || adamya.isSelected() || sapiro.isSelected() || mineave.isSelected();
    }
}