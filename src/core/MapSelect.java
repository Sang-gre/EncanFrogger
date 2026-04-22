package core;

import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MapSelect extends Selection {

    private Player selectedPlayer;
    private JRadioButton lireo, hathoria, adamya, sapiro, mineave;
    private GameMap selectedMap;

    public MapSelect(GamePanel gamePanel, Runnable onBack, Player selectedPlayer) {
        super(gamePanel, onBack);
        this.selectedPlayer = selectedPlayer;
    }

    @Override
    public JPanel createSelectionButtons() {

        lireo   = createBtn("assets/flags/lireoFlagMap.png");
        hathoria = createBtn("assets/flags/hathoriaFlagMap.png");
        adamya  = createBtn("assets/flags/adamyaFlagMap.png");
        sapiro  = createBtn("assets/flags/sapiroFlagMap.png");
        mineave = createBtn("assets/flags/mineaveFlagMap.png");

        JRadioButton[] buttons = {lireo, hathoria, adamya, sapiro, mineave};

        ButtonGroup group = new ButtonGroup();
        for (JRadioButton b : buttons) {
            group.add(b);
        }

        JPanel panel = new JPanel(new GridLayout(1, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(120, 20, 0, 20));

        for (JRadioButton b : buttons) {
            panel.add(b);
        }

        return panel;
    }

    private JRadioButton createBtn(String path) {
        JRadioButton btn = new JRadioButton();

        btn.putClientProperty("img", new ImageIcon(path).getImage());

        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);

        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);

        btn.addItemListener(e -> {
            boolean selected = (e.getStateChange() == ItemEvent.SELECTED);

            Image img = (Image) btn.getClientProperty("img");

            int scale = selected ? 110 : 100; 

            int width = btn.getWidth();
            int height = btn.getHeight();

            if (width <= 0 || height <= 0) return;

            int newW = width * scale / 100;
            int newH = height * scale / 100;

            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        });

        return btn;
    }

    @Override
    public JPanel createBackground() {

        JPanel background = new JPanel(null) {
            private final Image img = new ImageIcon("assets/Backgrounds/mapSelectBackground.png").getImage();

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

                resizeFlags(w, h - 100);
            }
        });

        return background;
    }

    private void resizeFlags(int panelWidth, int panelHeight) {

        int count = 5;

        int cardWidth = panelWidth / count;
        int cardHeight = panelHeight - 100;

        JRadioButton[] buttons = {lireo, hathoria, adamya, sapiro, mineave};

        for (JRadioButton btn : buttons) {

            Image img = (Image) btn.getClientProperty("img");

            Image scaled = img.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);

            btn.setIcon(new ImageIcon(scaled));
        }
    }

    @Override
    protected void onNext() {

        GameMap selectedMap = null;

        if (lireo.isSelected()) selectedMap = GameMap.LIREO;
        else if (hathoria.isSelected()) selectedMap = GameMap.HATHORIA;
        else if (adamya.isSelected()) selectedMap = GameMap.ADAMYA;
        else if (sapiro.isSelected()) selectedMap = GameMap.SAPIRO;
        else if (mineave.isSelected()) selectedMap = GameMap.MINEAVE;

        // ✅ prevent crash
        if (selectedMap == null) return;

        getGamePanel().startLevel(selectedPlayer, selectedMap);
    }

    @Override
    public boolean validateSelection() {
        return lireo.isSelected() || hathoria.isSelected() || adamya.isSelected() || sapiro.isSelected() || mineave.isSelected();
    }

}