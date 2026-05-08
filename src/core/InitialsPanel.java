package core;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;
import managers.AssetManager;
import ui.PopupDialog;

public class InitialsPanel extends JPanel implements KeyListener {

    private static final int MAX_INITIALS = 10;

    private static final double TEXT_Y = 0.63;
    private static final double TEXT_SIZE = 0.08;
    private static final double TEXT_MAX_W = 0.35;
    private static final double OK_Y = 0.7;
    private static final double OK_W = 0.13;

    private final Runnable onBack;
    private final Consumer<String> onDone;

    private final StringBuilder initials = new StringBuilder();

    private Image bgImage;
    private Image okImage;
    private Image popupImage;
    private Image deadPopupImage;
    private JButton okBtn;

    public InitialsPanel(Runnable onBack, Consumer<String> onDone) {
        this.onBack = onBack;
        this.onDone = onDone;

        setFocusable(true);
        setLayout(null);
        addKeyListener(this);

        AssetManager am = AssetManager.getInstance();
        bgImage = am.getBackground("initials");
        okImage = am.getButton("ok2");
        popupImage = am.getPopup("initialsInput");

        okBtn = createImageButton(okImage, 100, 50);
        okBtn.addActionListener(e -> {
            requestFocusInWindow();
            tryConfirm();
        });
        add(okBtn);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth(), h = getHeight();
                if (okImage != null) {
                    int okW = (int) (w * OK_W);
                    int okH = (int) (okW * okImage.getHeight(null) / (double) okImage.getWidth(null));
                    int okX = (w - okW) / 2;
                    int okY = (int) (h * OK_Y);
                    okBtn.setBounds(okX, okY, okW, okH);
                }
            }
        });
    }

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
        g2.dispose();
    }

    private void drawInitials(Graphics2D g2, int w, int h) {
        if (initials.length() == 0)
            return;

        AssetManager am = AssetManager.getInstance();
        Font font = am.getFont("enchantedLand");
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

        g2.setColor(Color.WHITE);
        g2.drawString(text, textX, textY);
    }

    /* KEYBOARD */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        char ch = e.getKeyChar();

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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /* HELPERS */
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
        PopupDialog.show(this, popupImage); // change to dead
        requestFocusInWindow();
    }

    private void reset() {
        initials.setLength(0);
    }

    public void activate() {
        reset();
        repaint();
        requestFocusInWindow();
    }

    private JButton createImageButton(Image img, int width, int height) {
        if (img == null)
            return new JButton("?");
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
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
}