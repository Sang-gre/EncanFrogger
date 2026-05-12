package core;

import gameobjects.Player;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import assets.AssetManager;

public class LevelSelect extends Selection {

    private Player selectedPlayer;
    private GameMap selectedMap;

    private JRadioButton level1;
    private JRadioButton level2;
    private JRadioButton level3;
    private JRadioButton level4;
    private JRadioButton level5;

    public LevelSelect(
            GamePanel gamePanel,
            Runnable onBack,
            Player selectedPlayer,
            GameMap selectedMap
    ) {

        super(gamePanel, onBack);

        this.selectedPlayer = selectedPlayer;
        this.selectedMap = selectedMap;
    }

    @Override
    public JPanel createSelectionButtons() {

        level1 = createBtn(1);
        level2 = createBtn(2);
        level3 = createBtn(3);
        level4 = createBtn(4);
        level5 = createBtn(5);

        JRadioButton[] buttons = {
                level1,
                level2,
                level3,
                level4,
                level5
        };

        ButtonGroup group = new ButtonGroup();

        for (JRadioButton b : buttons) {

            if (b != null) {
                group.add(b);
            }
        }

        int maxLevels = selectedMap.getMaxLevels();

        JPanel panel = new JPanel(
                new GridLayout(1, maxLevels)
        );

        panel.setOpaque(false);

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        120,
                        20,
                        0,
                        20
                )
        );

        for (int i = 0; i < maxLevels; i++) {
            panel.add(buttons[i]);
        }

        return panel;
    }

    private JRadioButton createBtn(int level) {

    if (level > selectedMap.getMaxLevels()) {
        return null;
    }

    JRadioButton btn = new JRadioButton();

    Image img =
            AssetManager.getInstance()
                    .getLevelButtonImage(level);

    btn.putClientProperty("img", img);

    btn.putClientProperty(
            "originalImg",
            img
    );

    btn.putClientProperty(
            "level",
            level
    );

    btn.setOpaque(false);
    btn.setBorderPainted(false);
    btn.setContentAreaFilled(false);
    btn.setFocusPainted(false);

    btn.setHorizontalAlignment(
            SwingConstants.CENTER
    );

    btn.setVerticalAlignment(
            SwingConstants.CENTER
    );

    btn.addItemListener(e -> {

        boolean selected =
                (e.getStateChange()
                        == ItemEvent.SELECTED);

        if (selected) {

            btn.setMargin(
                    new Insets(0, 0, 20, 0)
            );

        } else {

            btn.setMargin(
                    new Insets(20, 0, 0, 0)
            );
        }
    });

    return btn;
}

    @Override
    public JPanel createBackground() {

        JPanel background = new JPanel(null) {

            private final Image img =
                    AssetManager.getInstance()
                            .getBackground(
                                    "selectLevelPanel"
                            );

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                g.drawImage(
                        img,
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        this
                );
            }
        };

        JPanel selection =
                createSelectionButtons();

        JPanel nav =
                createNavButtons();

        background.add(selection);
        background.add(nav);

        background.addComponentListener(
                new ComponentAdapter() {

                    @Override
                    public void componentResized(
                            ComponentEvent e
                    ) {

                        int w =
                                background.getWidth();

                        int h =
                                background.getHeight();

                        selection.setBounds(
                                0,
                                0,
                                w,
                                h - 50
                        );

                        nav.setBounds(
                                0,
                                h - 100,
                                w,
                                100
                        );

                        resizeButtons(
                                w,
                                h - 100
                        );
                    }
                }
        );

        return background;
    }

    private void resizeButtons(
            int panelWidth,
            int panelHeight
    ) {

        int count =
                selectedMap.getMaxLevels();

        int cardWidth =
                panelWidth / count;

        int cardHeight =
                panelHeight - 100;

        JRadioButton[] buttons = {
                level1,
                level2,
                level3,
                level4,
                level5
        };

        for (int i = 0; i < count; i++) {

            JRadioButton btn = buttons[i];

            if (btn == null)
                continue;

            Image img = (Image)
                    btn.getClientProperty(
                            "originalImg"
                    );

            Image scaled =
                    img.getScaledInstance(
                            cardWidth,
                            cardHeight,
                            Image.SCALE_SMOOTH
                    );

            btn.setIcon(
                    new ImageIcon(scaled)
            );
        }
    }

    @Override
    protected void onNext() {

        JRadioButton[] buttons = {
                level1,
                level2,
                level3,
                level4,
                level5
        };

        Integer selectedLevel = null;

        for (JRadioButton btn : buttons) {

            if (btn != null
                    && btn.isSelected()) {

                selectedLevel =
                        (Integer)
                                btn.getClientProperty(
                                        "level"
                                );

                break;
            }
        }

        if (selectedLevel == null)
            return;

        getGamePanel().startLevel(
                selectedPlayer,
                selectedMap,
                selectedLevel
        );
    }

    @Override
    public boolean validateSelection() {

        JRadioButton[] buttons = {
                level1,
                level2,
                level3,
                level4,
                level5
        };

        for (JRadioButton btn : buttons) {

            if (btn != null
                    && btn.isSelected()) {

                return true;
            }
        }

        return false;
    }

    @Override
    protected String getPopupKey() {
        return "levelSelect";
    }
}