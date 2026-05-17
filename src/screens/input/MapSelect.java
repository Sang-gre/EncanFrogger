package screens.input;

import assets.AssetManager;
import core.GamePanel;
import core.level.GameMap;
import gameobjects.Player;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MapSelect extends Selection {

    private static final double NAV_H_RATIO     = 100.0 / 600.0;
    private static final double HEIGHT_RATIO    = 0.72;
    private static final double IMAGE_SCALE_W   = 0.95;
    private static final double IMAGE_SCALE_H   = 1.15;
    private static final double VERTICAL_BIAS   = 0.14;
    private static final int    FLAG_COUNT      = 5;
    private static final int    FLAG_GAP        = 24;
    private static final int    OUTER_PAD       = FLAG_GAP * 3;
    private static final int    SELECTED_OFFSET = 30;

    private final Player selectedPlayer;
    private final int[]  baseY = new int[FLAG_COUNT];

    private JRadioButton[] buttons;
    private JPanel         flagPanel;
    private GameMap        selectedMap;

    public MapSelect(GamePanel gamePanel, Runnable onBack, Player selectedPlayer) {
        super(gamePanel, onBack);
        this.selectedPlayer = selectedPlayer;
        init();
    }

    // -------------------------------------------------------------------------
    // Background / layout
    // -------------------------------------------------------------------------
    @Override
    public JPanel createBackground() {
        JPanel background = new JPanel(null) {
            private final Image img = AssetManager.getInstance().getBackground("mapSelect");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JPanel selection = createSelectionButtons();
        JPanel nav       = super.createNavButtons();

        background.add(selection);
        background.add(nav);

        background.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutPanels(background, selection, nav);
            }
        });

        SwingUtilities.invokeLater(() -> layoutPanels(background, selection, nav));
        return background;
    }

    private void layoutPanels(JPanel background, JPanel selection, JPanel nav) {
        int w = background.getWidth();
        int h = background.getHeight();
        if (w <= 0 || h <= 0) return;

        int navH = (int) Math.round(h * NAV_H_RATIO);
        selection.setBounds(0, 0, w, h - navH);
        nav.setBounds(0, h - navH, w, navH);

        resizeFlags(w, h - navH);
    }

    // -------------------------------------------------------------------------
    // Map Selection panel
    // -------------------------------------------------------------------------
    @Override
    public JPanel createSelectionButtons() {
        GameMap[] maps = { GameMap.LIREO, GameMap.HATHORIA, GameMap.ADAMYA, GameMap.SAPIRO, GameMap.MINEAVE };
        buttons = new JRadioButton[FLAG_COUNT];
        for (int i = 0; i < FLAG_COUNT; i++)
            buttons[i] = createBtn(i, maps[i]);

        ButtonGroup group = new ButtonGroup();
        for (JRadioButton b : buttons) group.add(b);

        flagPanel = new JPanel(null) {
            @Override public boolean isOptimizedDrawingEnabled() { return false; }

            @Override
            protected void paintChildren(Graphics g) {
                Graphics gc = g.create();
                gc.setClip(null);
                super.paintChildren(gc);
                gc.dispose();
            }
        };
        flagPanel.setOpaque(false);

        for (JRadioButton b : buttons) flagPanel.add(b);

        return flagPanel;
    }

    private JRadioButton createBtn(int index, GameMap map) {
        JRadioButton btn = new JRadioButton();
        btn.putClientProperty("originalImg", AssetManager.getInstance().getMapFlag(map));
        btn.putClientProperty("map", map);

        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);

        btn.addItemListener(e -> {
            boolean sel = (e.getStateChange() == ItemEvent.SELECTED);
            btn.setLocation(btn.getX(), baseY[index] + (sel ? -SELECTED_OFFSET : 0));
            flagPanel.repaint();
        });

        return btn;
    }

    // -------------------------------------------------------------------------
    // Flag sizing
    // -------------------------------------------------------------------------
    private void resizeFlags(int panelWidth, int panelHeight) {
        if (flagPanel == null) return;

        int imgWidth  = (int)((panelWidth - OUTER_PAD * 2) / (double) FLAG_COUNT * IMAGE_SCALE_W);
        int imgHeight = (int)(panelHeight * HEIGHT_RATIO * IMAGE_SCALE_H);

        int totalW = imgWidth * FLAG_COUNT + FLAG_GAP * (FLAG_COUNT - 1);
        int startX = (panelWidth - totalW) / 2;
        int startY = (panelHeight - imgHeight) / 2 + (int)(panelHeight * VERTICAL_BIAS);

        for (int i = 0; i < FLAG_COUNT; i++) {
            JRadioButton btn = buttons[i];
            Image original   = (Image) btn.getClientProperty("originalImg");
            btn.setIcon(new ImageIcon(original.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH)));

            baseY[i] = startY;
            int x = startX + i * (imgWidth + FLAG_GAP);
            btn.setBounds(x, startY + (btn.isSelected() ? -SELECTED_OFFSET : 0), imgWidth, imgHeight);
        }

        flagPanel.revalidate();
        flagPanel.repaint();
    }

    // -------------------------------------------------------------------------
    // Selection logic
    // -------------------------------------------------------------------------
    @Override
    protected void onNext() {
        for (JRadioButton btn : buttons) {
            if (btn.isSelected()) {
                selectedMap = (GameMap) btn.getClientProperty("map");
                break;
            }
        }
        if (selectedMap == null) return;
        getGamePanel().startLevel(selectedPlayer, selectedMap, 1);
    }

    @Override
    public boolean validateSelection() {
        for (JRadioButton btn : buttons)
            if (btn.isSelected()) return true;
        return false;
    }

    @Override
    protected String getPopupKey() { return "mapSelect"; }
}