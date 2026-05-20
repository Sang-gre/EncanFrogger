package screens.gameplay;

import assets.AssetManager;
import assets.SoundManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PauseScreen extends JPanel {

    // --- Assets ---
    private final Image panelBg;
    private final Image resumeImg;
    private final Image menuImg;
    private final Image exitImg;

    // --- Buttons ---
    private final JButton resumeBtn;
    private final JButton menuBtn;
    private final JButton exitBtn;

    // --- Misc ---
    private int lastBtnH = -1; // tracks last computed button height
    private final SoundManager sound = SoundManager.getInstance();

    public PauseScreen(Runnable onResume, Runnable onMenu, Runnable onExit) {
        setLayout(null);
        setOpaque(false);

        panelBg = AssetManager.getInstance().getBackground("pausePanel");
        resumeImg = AssetManager.getInstance().getButton("resume");
        menuImg = AssetManager.getInstance().getButton("menu2");
        exitImg = AssetManager.getInstance().getButton("exit2");

        resumeBtn = makeButton();
        menuBtn = makeButton();
        exitBtn = makeButton();

        resumeBtn.addActionListener(e -> {
            sound.play("click");
            onResume.run();
        });
        menuBtn.addActionListener(e -> {
            sound.play("click");
            onMenu.run();
        });
        exitBtn.addActionListener(e -> {
            sound.play("click");
            onExit.run();
        });

        add(resumeBtn);
        add(menuBtn);
        add(exitBtn);

        // Re-layout buttons when the panel is resized
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutButtons();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Button setup
    // -------------------------------------------------------------------------
    private JButton makeButton() {
        JButton btn = new JButton();

        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        int pressOffset = 4;

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setLocation(btn.getX(), btn.getY() + pressOffset);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setLocation(btn.getX(), btn.getY() - pressOffset);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Snap back if the cursor leaves before releasing
                btn.setLocation(btn.getX(), btn.getY());
            }
        });

        return btn;
    }

    // -------------------------------------------------------------------------
    // Layout
    // -------------------------------------------------------------------------
    private void layoutButtons() {
        int w = getWidth();
        int h = getHeight();

        int btnH = (int) (h * 0.18);
        if (btnH == lastBtnH)
            return; // skip if no change in size
        lastBtnH = btnH;

        int resumeW = scaledWidth(resumeImg, btnH);
        int menuW = scaledWidth(menuImg, btnH);
        int exitW = scaledWidth(exitImg, btnH);

        resumeBtn.setIcon(new ImageIcon(resumeImg.getScaledInstance(resumeW, btnH, Image.SCALE_SMOOTH)));
        menuBtn.setIcon(new ImageIcon(menuImg.getScaledInstance(menuW, btnH, Image.SCALE_SMOOTH)));
        exitBtn.setIcon(new ImageIcon(exitImg.getScaledInstance(exitW, btnH, Image.SCALE_SMOOTH)));

        int startY = (int) (h * 0.27);
        int spacing = (int) (h * 0.185);
        int offset = 4; // just to center within panel bg

        resumeBtn.setBounds((w - resumeW) / 2 - offset, startY, resumeW, btnH);
        menuBtn.setBounds((w - menuW) / 2 - offset, startY + spacing, menuW, btnH);
        exitBtn.setBounds((w - exitW) / 2 - offset, startY + spacing * 2, exitW, btnH);
    }

    /* Scaled given a target height, preserving aspect ratio */
    private int scaledWidth(Image img, int targetH) {
        int naturalW = img.getWidth(null);
        int naturalH = img.getHeight(null);
        if (naturalW <= 0 || naturalH <= 0)
            return targetH * 4; // fallback if image dimensions aren't available yet
        return (int) ((double) naturalW / naturalH * targetH);
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dim the game behind the pause overlay
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (panelBg != null)
            g.drawImage(panelBg, 0, 0, getWidth(), getHeight(), this);
    }

    /* Draws onto external graphics context */
    public void draw(Graphics g, int w, int h) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, w, h);

        if (panelBg != null)
            g.drawImage(panelBg, 0, 0, w, h, null);
    }
}