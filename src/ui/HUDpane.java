package ui;

import assets.AssetManager;
import java.awt.*;
import javax.swing.*;

public class HUDpane extends JPanel {

    private JLabel[] hearts;
    private JLabel scoreLabel;
    private JLabel scoreValue;
    private JButton pauseButton;

    private final Image pauseImg;
    private final Image scoreImg;
    private final Image heartImg;

    private int lastHeight = -1;

    public HUDpane() {
        this(null);
    }

    public HUDpane(Runnable onPauseClicked) {

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(800, 60));

        pauseImg = AssetManager.getInstance().getButton("pause");
        scoreImg = AssetManager.getInstance().getHUD("score");
        heartImg = AssetManager.getInstance().getHUD("heart");

        /* Pause Button */
        pauseButton = new JButton();
        pauseButton.setBorderPainted(false);
        pauseButton.setContentAreaFilled(false);
        pauseButton.setFocusPainted(false);
        pauseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (onPauseClicked != null) {
            pauseButton.addActionListener(e -> onPauseClicked.run());
        }
        add(pauseButton);

        /* Score Label */
        scoreLabel = new JLabel();
        add(scoreLabel);

        /* Score Text */
        scoreValue = new JLabel("0");
        scoreValue.setForeground(new Color(246, 242, 195));
        scoreValue.setHorizontalAlignment(SwingConstants.LEFT);
        scoreValue.setVerticalAlignment(SwingConstants.CENTER);
        add(scoreValue);
        setComponentZOrder(scoreValue, 0);

        /* Hearts */
        hearts = new JLabel[3];
        for (int i = 0; i < hearts.length; i++) {
            hearts[i] = new JLabel();
            add(hearts[i]);
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();

        int h = getHeight();
        if (h <= 0) return;
        if (h == lastHeight) return;
        lastHeight = h;

        // scale everything relative to HUD height
        int btnSize   = (int) (h * 0.85);
        int scoreH    = (int) (h * 0.85);
        int scoreW    = (int) (scoreH * (180.0 / 50.0)); // keep original aspect ratio
        int heartSize = (int) (h * 0.65);
        int pad       = (int) (h * 0.08);
        int centerY   = (h - btnSize) / 2;

        /* Pause Button */
        pauseButton.setIcon(new ImageIcon(
                pauseImg.getScaledInstance(btnSize, btnSize, Image.SCALE_SMOOTH)));
        pauseButton.setBounds(pad, centerY, btnSize, btnSize);

        /* Score Label */
        int scoreX = pad + btnSize + pad;
        scoreLabel.setIcon(new ImageIcon(
                scoreImg.getScaledInstance(scoreW, scoreH, Image.SCALE_SMOOTH)));
        scoreLabel.setBounds(scoreX, (h - scoreH) / 2, scoreW, scoreH);

        /* Score Text */
        scoreValue.setFont(
                AssetManager.getInstance().getFont("enchantedLand").deriveFont((float) (h * 0.4)));
        int textX = scoreX + (int) (scoreW * 0.5);
        scoreValue.setBounds(textX, (h - scoreH) / 2, scoreW, scoreH);

        /* Hearts */
        int heartsX = scoreX + scoreW + pad;
        int heartY  = (h - heartSize) / 2;
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setIcon(new ImageIcon(
                    heartImg.getScaledInstance(heartSize, heartSize, Image.SCALE_SMOOTH)));
            hearts[i].setBounds(heartsX + i * (heartSize + pad / 2), heartY, heartSize, heartSize);
        }
    }

    public void updateScore(int newScore) {
        scoreValue.setText(String.valueOf(newScore));
    }

    public void updateLives(int livesRemaining) {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisible(i < livesRemaining);
        }
    }
}