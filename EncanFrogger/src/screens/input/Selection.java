package screens.input;

import assets.AssetManager;
import assets.SoundManager;
import core.GamePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import persistence.LeaderboardManager;
import persistence.ScoreEntry;
import ui.PopupDialog;

public abstract class Selection extends JPanel {

    // Base dimensions
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;
    private static final int BASE_NAV_H = 100;

    // Element proportions and width ratios
    private static final double BTN_W_RATIO = 120.0 / BASE_WIDTH;
    private static final double BTN_ASPECT = 60.0 / 140.0; // h/w
    private static final double COIN_W_RATIO = 100.0 / BASE_WIDTH;
    private static final double COIN_ASPECT = 60.0 / 120.0;
    private static final double LVL_W_RATIO = 135.0 / BASE_WIDTH;
    private static final double LVL_ASPECT = 60.0 / 160.0;

    private static final int BORDER_MARGIN = 4;

    private final GamePanel gamePanel;
    protected final Runnable onBack;
    private final SoundManager sound = SoundManager.getInstance();

    protected JLabel coinLabel;
    protected JLabel levelLabel;

    // Raw images
    private final Image coinImgRaw;
    private final Image levelImgRaw;
    private final Image backImgRaw;
    private final Image nextImgRaw;

    public Selection(GamePanel gamePanel, Runnable onBack) {
        this.gamePanel = gamePanel;
        this.onBack = onBack;

        AssetManager am = AssetManager.getInstance();
        coinImgRaw = am.getTracker("coinTrack");
        levelImgRaw = am.getTracker("levelTrack");
        backImgRaw = am.getButton("back");
        nextImgRaw = am.getButton("next");

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
    }

    protected final void init() {
        add(createBackground(), BorderLayout.CENTER);
    }

    // -------------------------------------------------------------------------
    // Background panel
    // -------------------------------------------------------------------------
    public JPanel createBackground() {
        JPanel background = new JPanel(new BorderLayout()) {
            private final Image img = AssetManager.getInstance().getBackground("characterSelect");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null)
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        background.setOpaque(true);
        background.add(createSelectionButtons(), BorderLayout.CENTER);
        background.add(createNavButtons(), BorderLayout.SOUTH);

        return background;
    }

    // -------------------------------------------------------------------------
    // Nav-button panel
    // -------------------------------------------------------------------------
    protected JPanel createNavButtons() {
        JPanel panel = new JPanel(null) {
            @Override
            public Dimension getPreferredSize() {
                int parentH = Selection.this.getHeight();
                int h = parentH > 0
                        ? (int) (parentH * (BASE_NAV_H / (double) BASE_HEIGHT))
                        : BASE_NAV_H;
                return new Dimension(BASE_WIDTH, h);
            }
        };
        panel.setOpaque(false);

        // ---- Back button ----
        final JButton backBtn;
        if (onBack != null) {
            backBtn = makeNavButton();
            backBtn.addActionListener(e -> {
                onBack.run();
                sound.play("click");
            });
            panel.add(backBtn);
        } else {
            backBtn = null;
        }

        // ---- Next button ----
        final JButton nextBtn = makeNavButton();
        nextBtn.addActionListener((ActionEvent e) -> {
            if (!validateSelection()) {
                showPopupDialog();
                return;
            }
            sound.play("click");
            onNext();
        });
        panel.add(nextBtn);

        // ---- Tracker labels ----
        AssetManager am = AssetManager.getInstance();
        coinLabel = new JLabel();
        levelLabel = new JLabel();
        configureLabelStyle(coinLabel, am);
        configureLabelStyle(levelLabel, am);

        List<ScoreEntry> entries = LeaderboardManager.loadAll();
        String currentInitials = gamePanel.getPlayerInitials();
        ScoreEntry match = entries.stream()
                .filter(e -> e.initials.equalsIgnoreCase(currentInitials))
                .findFirst().orElse(null);

        if (match != null) {
            levelLabel.setText("LEVEL " + match.level);
            coinLabel.setText(String.valueOf(match.coins));
        } else {
            levelLabel.setText("LEVEL 1");
            coinLabel.setText("0");
        }

        panel.add(coinLabel);
        panel.add(levelLabel);

        // ---- Resize listener ----
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                relayout(panel, backBtn, nextBtn);
            }

            @Override
            public void componentShown(ComponentEvent e) {
                relayout(panel, backBtn, nextBtn);
            }
        });

        return panel;
    }

    // -------------------------------------------------------------------------
    // Resizing
    // -------------------------------------------------------------------------
    private void relayout(JPanel panel, JButton backBtn, JButton nextBtn) {
        int w = panel.getWidth();
        int h = panel.getHeight();
        if (w <= 0 || h <= 0)
            return;

        // Scale with panel but cap at original design sizes
        int btnW = (int) (w * BTN_W_RATIO);
        int btnH = (int) (btnW * BTN_ASPECT);
        int coinW = (int) (w * COIN_W_RATIO);
        int coinH = (int) (coinW * COIN_ASPECT);
        int lvlW = (int) (w * LVL_W_RATIO);
        int lvlH = (int) (lvlW * LVL_ASPECT);

        int bottomPad = (int) (h * 0.05);
        int btnY = h - btnH - bottomPad;

        // Buttons
        if (backBtn != null) {
            rescaleButton(backBtn, backImgRaw, btnW, btnH);
            backBtn.setBounds(BORDER_MARGIN, btnY, btnW, btnH);
        }
        rescaleButton(nextBtn, nextImgRaw, btnW, btnH);
        nextBtn.setBounds(w - btnW - BORDER_MARGIN, btnY, btnW, btnH);

        // Tracker labels aligned with buttons
        int trackerY = btnY + (btnH - Math.max(coinH, lvlH)) / 2;
        int gap = 8;
        int totalTracker = coinW + gap + lvlW;
        int startX = (w - totalTracker) / 2;

        coinLabel.setBounds(startX, trackerY, coinW, coinH);
        levelLabel.setBounds(startX + coinW + gap, trackerY, lvlW, lvlH);

        rescaleLabel(coinLabel, coinImgRaw, coinW, coinH);
        rescaleLabel(levelLabel, levelImgRaw, lvlW, lvlH);

        // Scale font
        float fontSize = Math.max(8f, coinH * 0.28f);
        Font scaled = coinLabel.getFont().deriveFont(fontSize);
        coinLabel.setFont(scaled);
        levelLabel.setFont(scaled);

        panel.revalidate();
        panel.repaint();
    }

    /* Rescales a button's icon based on raw image */
    private void rescaleButton(JButton btn, Image raw, int w, int h) {
        if (raw == null || w <= 0 || h <= 0)
            return;
        btn.setIcon(new ImageIcon(raw.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
    }

    /* Rescales a tracker label's icon based on raw image */
    private void rescaleLabel(JLabel label, Image raw, int w, int h) {
        if (raw == null || w <= 0 || h <= 0)
            return;
        label.setIcon(new ImageIcon(raw.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
    }

    /* Shared font/colour/alignment for tracker labels. */
    private void configureLabelStyle(JLabel label, AssetManager am) {
        Font font = am.getFont("proffaliceHandwrite");
        if (font == null)
            font = new Font("Serif", Font.BOLD, 20);
        label.setFont(font.deriveFont(18f));
        label.setForeground(new Color(246, 242, 195));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.CENTER);
    }

    private JButton makeNavButton() {
        JButton button = new JButton();
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

    // -------------------------------------------------------------------------
    // Public helper for subclasses that need to create extra buttons
    // -------------------------------------------------------------------------
    public JButton createImageButton(Image img, int width, int height) {
        JButton button = makeNavButton();
        if (img != null)
            button.setIcon(new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        else
            button.setText("?");
        return button;
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------
    protected abstract void onNext();

    public abstract JPanel createSelectionButtons();

    public abstract boolean validateSelection();

    protected String getPopupKey() {
        return null;
    }

    private void showPopupDialog() {
        Image popupImg = getPopupKey() != null
                ? AssetManager.getInstance().getPopup(getPopupKey())
                : null;
        if (popupImg == null) {
            JOptionPane.showMessageDialog(this, "Please make a selection!");
            return;
        }
        PopupDialog.show(this, popupImg);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public Runnable getOnBack() {
        return onBack;
    }
}