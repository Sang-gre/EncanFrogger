package screens.input;

import assets.AssetManager;
import assets.SoundManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;
import screens.menu.LeaderboardScreen;
import ui.PopupDialog;

public class InitialsPanel extends JPanel {

    private static final int MAX_INITIALS = 10;

    private static final double TEXT_Y = 0.63;
    private static final double TEXT_SIZE = 0.08;
    private static final double TEXT_MAX_W = 0.35;
    private static final double OK_Y = 0.7;
    private static final double OK_W = 0.13;
    private static final double LB_W = 0.20;
    private static final double LB_Y = 0.82;

    private final Runnable onBack;
    private final Consumer<String> onDone;

    private final StringBuilder initials = new StringBuilder();

    private final Image bgImage;
    private final Image okImage;
    private final Image leaderboardImage;
    private final Image popupImage;
    private final Image usedPopupImage;
    private final JButton okBtn;
    private final JButton leaderboardBtn;

    private LeaderboardScreen leaderboardOverlay;

    public InitialsPanel(Runnable onBack, Consumer<String> onDone) {
        this.onBack = onBack;
        this.onDone = onDone;

        setFocusable(true);
        setLayout(null);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        AssetManager am = AssetManager.getInstance();
        bgImage = am.getBackground("initials");
        okImage = am.getButton("ok2");
        leaderboardImage = am.getButton("leaderboardBtn");
        popupImage = am.getPopup("initialsInput");
        usedPopupImage = am.getPopup("initialsTaken");

        okBtn = createImageButton(okImage);
        okBtn.addActionListener(e -> {
            requestFocusInWindow();
            SoundManager.getInstance().play("click");
            tryConfirm();
        });

        leaderboardBtn = createImageButton(leaderboardImage);
        leaderboardBtn.addActionListener(e -> {
            requestFocusInWindow();
            SoundManager.getInstance().play("click");
            showLeaderboardOverlay();
        });

        add(okBtn);
        add(leaderboardBtn);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutButtons();
            }
        });
    }

    private void layoutButtons() {
        int w = getWidth(), h = getHeight();

        if (okImage != null) {
            int okW = (int) (w * OK_W);
            int okH = (int) (okW * okImage.getHeight(null) / (double) okImage.getWidth(null));
            int okX = (w - okW) / 2;
            int okY = (int) (h * OK_Y);
            okBtn.setBounds(okX, okY, okW, okH);
        }

        if (leaderboardImage != null) {
            int lbW = (int) (w * LB_W);
            int lbH = (int) (lbW * leaderboardImage.getHeight(null) / (double) leaderboardImage.getWidth(null));
            int lbX = (w - lbW) / 2;
            int lbY = (int) (h * LB_Y);
            leaderboardBtn.setBounds(lbX, lbY, lbW, lbH);
        }
    }

    private void showLeaderboardOverlay() {
        leaderboardOverlay = new LeaderboardScreen(false);
        okBtn.setVisible(false);
        leaderboardBtn.setVisible(false);
        repaint();
    }

    private void hideLeaderboardOverlay() {
        leaderboardOverlay = null;
        okBtn.setVisible(true);
        leaderboardBtn.setVisible(true);
        repaint();
    }

    // -------------------------------------------------------------------------
    // INITIALIZATION
    // -------------------------------------------------------------------------
    private JButton createImageButton(Image img) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                if (img == null) {
                    super.paintComponent(g);
                    return;
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
                g2.dispose();
            }
        };
        if (img == null) {
            button.setText("?");
        }
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
    // KEYBOARD
    // -------------------------------------------------------------------------
    private void handleKeyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        char ch = e.getKeyChar();

        if (leaderboardOverlay != null) {
            switch (code) {
                case KeyEvent.VK_UP -> {
                    leaderboardOverlay.scroll(-1);
                    repaint();
                }
                case KeyEvent.VK_DOWN -> {
                    leaderboardOverlay.scroll(1);
                    repaint();
                }
                case KeyEvent.VK_ESCAPE -> hideLeaderboardOverlay();
                default -> {
                }
            }
            return;
        }

        if (code == KeyEvent.VK_ESCAPE) {
            reset();
            onBack.run();
            return;
        }

        if (code == KeyEvent.VK_BACK_SPACE) {
            if (initials.length() > 0)
                initials.deleteCharAt(initials.length() - 1);
            repaint();
            return;
        }

        if (code == KeyEvent.VK_ENTER) {
            tryConfirm();
            return;
        }

        if (Character.isLetter(ch) && initials.length() < MAX_INITIALS) {
            initials.append(Character.toUpperCase(ch));
            repaint();
        }
    }

    // -------------------------------------------------------------------------
    // MOUSE
    // -------------------------------------------------------------------------
    private void handleMouseClick(MouseEvent e) {
        if (leaderboardOverlay != null) {
            if (leaderboardOverlay.isBackClicked(e.getPoint())) {
                SoundManager.getInstance().play("click");
                hideLeaderboardOverlay();
            }
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------
    private void tryConfirm() {
        if (initials.length() == 0) {
            showPopup();
            return;
        }
        String r = initials.toString();
        reset();
        onDone.accept(r);
    }

    private void showPopup() {
        PopupDialog.show(this, popupImage);
        requestFocusInWindow();
    }

    public void showDeadPopup() {
        PopupDialog.show(this, usedPopupImage);
        requestFocusInWindow();
    }

    private void reset() {
        initials.setLength(0);
    }

    public void activate() {
        hideLeaderboardOverlay();
        reset();
        repaint();
        requestFocusInWindow();
    }

    // -------------------------------------------------------------------------
    // RENDERING
    // -------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (bgImage != null)
            g2.drawImage(bgImage, 0, 0, w, h, null);
        else {
            g2.setColor(new Color(20, 10, 40));
            g2.fillRect(0, 0, w, h);
        }

        drawInitials(g2, w, h);

        if (leaderboardOverlay != null) {
            leaderboardOverlay.draw(g2, w, h);
        }

        g2.dispose();
    }

    private void drawInitials(Graphics2D g2, int w, int h) {
        if (initials.length() == 0)
            return;

        AssetManager am = AssetManager.getInstance();
        Font font = am.getFont("proffaliceHandwrite");
        if (font == null)
            font = new Font("Serif", Font.BOLD, 36);
        font = font.deriveFont(Font.BOLD, (float) (h * TEXT_SIZE));

        int maxW = (int) (w * TEXT_MAX_W);
        FontMetrics fm = g2.getFontMetrics(font);
        while (fm.stringWidth(initials.toString()) > maxW && font.getSize2D() > 10) {
            font = font.deriveFont(font.getSize2D() - 1f);
            fm = g2.getFontMetrics(font);
        }

        g2.setFont(font);
        String text = initials.toString();
        int textX = (w - fm.stringWidth(text)) / 2;
        int textY = (int) (h * TEXT_Y);

        g2.setColor(new Color(151, 59, 53));
        g2.drawString(text, textX, textY);
    }
}