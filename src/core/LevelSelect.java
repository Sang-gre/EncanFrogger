package core;

import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import assets.AssetManager;

public class LevelSelect extends Selection {

    private final Player selectedPlayer;
    private final GameMap selectedMap;
    private final GamePanel gamePanel;

    private JRadioButton[] levelButtons;

    public LevelSelect(GamePanel gamePanel, Runnable onBack, Player selectedPlayer, GameMap selectedMap) {
    super(gamePanel, onBack);
    this.gamePanel = gamePanel;
    this.selectedPlayer = selectedPlayer;
    this.selectedMap = selectedMap;
}


    @Override
    public JPanel createSelectionButtons() {

    int start = selectedMap.getStartLevel();
    int end = selectedMap.getEndLevel();
    int count = end - start + 1;

    levelButtons = new JRadioButton[count];

    ButtonGroup group = new ButtonGroup();

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
    panel.setOpaque(false);

    for (int i = 0; i < count; i++) {

        int levelNumber = start + i;

        JRadioButton btn = createBtn(levelNumber);

        levelButtons[i] = btn;
        group.add(btn);
        panel.add(btn);
    }

    return panel;
}

    private JRadioButton createBtn(int level) {

        JRadioButton btn = new JRadioButton();

        Image img = AssetManager.getInstance()
                .getLevelButtonImage(level);

        btn.putClientProperty("originalImg", img);
        btn.putClientProperty("level", level);

        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);

        btn.addItemListener(e -> {
            boolean selected = (e.getStateChange() == ItemEvent.SELECTED);

            btn.setMargin(selected
                    ? new Insets(0, 0, 20, 0)
                    : new Insets(20, 0, 0, 0));
        });

        return btn;
    }

    @Override
    public JPanel createBackground() {

        JPanel background = new JPanel(null) {

            private final Image img =
                    AssetManager.getInstance()
                            .getBackground("selectLevelPanel");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JPanel selection = createSelectionButtons();
        JPanel nav = createNavButtons();

        background.add(selection);
        background.add(nav);

        background.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int w = background.getWidth();
                int h = background.getHeight();

                selection.setBounds(0, 0, w, h - 50);
                nav.setBounds(0, h - 100, w, 100);

                resizeButtons(w, h - 100);
            }
        });

        return background;
    }

    private void resizeButtons(int panelWidth, int panelHeight) {

    if (levelButtons == null) return;

    int btnW = panelWidth / 6;   // consistent size
    int btnH = panelHeight / 2;

    for (JRadioButton btn : levelButtons) {

        Image img = (Image) btn.getClientProperty("originalImg");

        if (img == null) continue;

        Image scaled = img.getScaledInstance(
                btnW,
                btnH,
                Image.SCALE_SMOOTH
        );

        btn.setIcon(new ImageIcon(scaled));
    }
}

    @Override
    protected void onNext() {

        if (levelButtons == null) return;

        Integer selectedLevel = null;

        for (JRadioButton btn : levelButtons) {
            if (btn.isSelected()) {
                selectedLevel = (Integer) btn.getClientProperty("level");
                break;
            }
        }

        if (selectedLevel == null) return;

        gamePanel.startLevel(selectedPlayer, selectedMap, selectedLevel);
    }

    @Override
    public boolean validateSelection() {

        if (levelButtons == null) return false;

        for (JRadioButton btn : levelButtons) {
            if (btn.isSelected()) return true;
        }

        return false;
    }

    @Override
    protected String getPopupKey() {
        return "levelSelect";
    }

    
}