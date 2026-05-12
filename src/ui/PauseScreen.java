package ui;

import assets.AssetManager;
import assets.SoundManager;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PauseScreen extends JPanel {

    private final Image panelBg;
    private final Image resumeImg;
    private final Image menuImg;
    private final Image exitImg;

    private final JButton resumeBtn;
    private final JButton menuBtn;
    private final JButton exitBtn;

    private int lastBtnH = -1;

    private SoundManager sound = new SoundManager();

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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutButtons();
            }
        });
    }

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
                btn.setLocation(btn.getX(), btn.getY());
            }
        });

        return btn;
    }

    private void layoutButtons() {
        int w = getWidth();
        int h = getHeight();

        int panelH = h;
        int panelY = 0;

        int btnH = (int) (panelH * 0.18);
        if (btnH == lastBtnH)
            return;
        lastBtnH = btnH;

        int resumeW = scaledWidth(resumeImg, btnH);
        int menuW = scaledWidth(menuImg, btnH);
        int exitW = scaledWidth(exitImg, btnH);

        resumeBtn.setIcon(new ImageIcon(resumeImg.getScaledInstance(resumeW, btnH, Image.SCALE_SMOOTH)));
        menuBtn.setIcon(new ImageIcon(menuImg.getScaledInstance(menuW, btnH, Image.SCALE_SMOOTH)));
        exitBtn.setIcon(new ImageIcon(exitImg.getScaledInstance(exitW, btnH, Image.SCALE_SMOOTH)));

        int startY = panelY + (int) (panelH * 0.27);
        int spacing = (int) (panelH * 0.185);

        int offset = 4;

        resumeBtn.setBounds((w - resumeW) / 2 - offset, startY, resumeW, btnH);
        menuBtn.setBounds((w - menuW) / 2 - offset, startY + spacing, menuW, btnH);
        exitBtn.setBounds((w - exitW) / 2 - offset, startY + spacing * 2, exitW, btnH);
    }

    private int scaledWidth(Image img, int targetH) {
        int naturalW = img.getWidth(null);
        int naturalH = img.getHeight(null);
        if (naturalW <= 0 || naturalH <= 0)
            return targetH * 4; // fallback
        return (int) ((double) naturalW / naturalH * targetH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (panelBg != null) {
            g.drawImage(panelBg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void draw(Graphics g, int w, int h) {
        // dim
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, w, h);

        // panel bg
        if (panelBg != null) {
            g.drawImage(panelBg, 0, 0, w, h, null);
        }
    }
}