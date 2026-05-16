package screens.input;

import assets.AssetManager;
import core.GamePanel;
import core.level.GameMap;
import gameobjects.Player;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

public class MapSelect extends Selection {

    private static final double HEIGHT_RATIO    = 0.62;
    private static final double IMAGE_SCALE_W   = 0.95;
    private static final double IMAGE_SCALE_H   = 1.15;
    private static final int    FLAG_GAP        = 24;
    private static final int    SELECTED_OFFSET = 20;

    private final Player selectedPlayer;
    private JRadioButton lireo, hathoria, adamya, sapiro, mineave;
    private GameMap selectedMap;

    public MapSelect(GamePanel gamePanel, Runnable onBack, Player selectedPlayer) {
        super(gamePanel, onBack);
        this.selectedPlayer = selectedPlayer;
    }

    // -------------------------------------------------------------------------
    // Layout
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
        JPanel nav       = createNavButtons();

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

    @Override
    public JPanel createSelectionButtons() {

        lireo    = createBtn(GameMap.LIREO);
        hathoria = createBtn(GameMap.HATHORIA);
        adamya   = createBtn(GameMap.ADAMYA);
        sapiro   = createBtn(GameMap.SAPIRO);
        mineave  = createBtn(GameMap.MINEAVE);

        JRadioButton[] buttons = { lireo, hathoria, adamya, sapiro, mineave };

        ButtonGroup group = new ButtonGroup();
        for (JRadioButton b : buttons)
            group.add(b);

        JPanel panel = new JPanel(new GridLayout(1, 5, FLAG_GAP, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(120, 20, 0, 20));

        for (JRadioButton b : buttons)
            panel.add(wrapButton(b));

        return panel;
    }

    // -------------------------------------------------------------------------
    // Flag buttons
    // -------------------------------------------------------------------------
    private JRadioButton createBtn(GameMap map) {
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
            boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
            btn.setMargin(selected
                    ? new Insets(0, 0, SELECTED_OFFSET, 0)
                    : new Insets(0, 0, 0, 0));
            btn.revalidate();
            btn.repaint();
            if (btn.getParent() != null) btn.getParent().repaint();
        });

        return btn;
    }

    // Overflow wrapper — allows the button to paint above the cell boundary when selected
    private JPanel wrapButton(JRadioButton btn) {
        JPanel wrapper = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() { return false; }

            @Override
            protected void paintChildren(Graphics g) {
                Graphics gCopy = g.create();
                gCopy.setClip(null);
                super.paintChildren(gCopy);
                gCopy.dispose();
            }
        };

        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.add(Box.createVerticalGlue());
        btn.setAlignmentX(0.5f);
        wrapper.add(btn);
        wrapper.add(Box.createVerticalGlue());

        return wrapper;
    }

    private void resizeFlags(int panelWidth, int panelHeight) {
        int cardWidth  = panelWidth / 5;
        int cardHeight = (int)(panelHeight * HEIGHT_RATIO);

        int imgWidth  = (int)(cardWidth  * IMAGE_SCALE_W);
        int imgHeight = (int)(cardHeight * IMAGE_SCALE_H);

        for (JRadioButton btn : new JRadioButton[]{ lireo, hathoria, adamya, sapiro, mineave }) {
            Image original = (Image) btn.getClientProperty("originalImg");
            btn.setIcon(new ImageIcon(original.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH)));

            Dimension size = new Dimension(imgWidth, imgHeight);
            btn.setPreferredSize(size);
            btn.setMaximumSize(size);
        }
    }

    // -------------------------------------------------------------------------
    // Selection
    // -------------------------------------------------------------------------
    @Override
    protected void onNext() {
        for (JRadioButton btn : new JRadioButton[]{ lireo, hathoria, adamya, sapiro, mineave }) {
            if (btn.isSelected()) {
                selectedMap = (GameMap) btn.getClientProperty("map");
                break;
            }
        }

        if (selectedMap == null)
            return;

        getGamePanel().startLevel(selectedPlayer, selectedMap, 1);
    }

    @Override
    public boolean validateSelection() {
        for (JRadioButton btn : new JRadioButton[]{ lireo, hathoria, adamya, sapiro, mineave })
            if (btn.isSelected()) return true;
        return false;
    }

    @Override
    protected String getPopupKey() { return "mapSelect"; }
}