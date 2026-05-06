package ui;

import assets.AssetManager;
import java.awt.*;
import javax.swing.*;

public class HUDpane extends JPanel {

    private JLabel[] hearts;
    private JLabel scoreValue;

    public HUDpane() {

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(800, 60));
        
        /* Score Label */
        Image scoreImg = AssetManager.getHUD("score");
        JLabel score = new JLabel(
                new ImageIcon(
                        scoreImg.getScaledInstance(180, 50, Image.SCALE_SMOOTH)));
        score.setBounds(10, 5, 180, 50);
        add(score);

        /* Score Text */
        scoreValue = new JLabel("0");
        scoreValue.setFont(AssetManager.getFont("enchantedLand").deriveFont(24f));
        scoreValue.setForeground(new Color(246, 242, 195));
        scoreValue.setBounds(105, 4, 140, 50);
        scoreValue.setHorizontalAlignment(SwingConstants.LEFT);
        scoreValue.setVerticalAlignment(SwingConstants.CENTER);

        add(scoreValue);

        setComponentZOrder(scoreValue, 0);

        hearts = new JLabel[3];

        Image heartImg = AssetManager.getHUD("heart");

        for (int i = 0; i < hearts.length; i++) {

            JLabel h = new JLabel(
                    new ImageIcon(
                            heartImg.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

            h.setBounds(200 + (i * 45), 10, 40, 40);

            hearts[i] = h;
            add(h);
        }

        /*
         * Image menuImg = AssetManager.getHUD("menu");
         * 
         * JButton menuButton = new JButton(
         * new ImageIcon(
         * menuImg.getScaledInstance(120, 50, Image.SCALE_SMOOTH)
         * )
         * );
         * 
         * menuButton.setBounds(650, 5, 120, 50);
         * 
         * menuButton.setBorderPainted(false);
         * menuButton.setContentAreaFilled(false);
         * menuButton.setFocusPainted(false);
         * 
         * add(menuButton);
         */ // wala pang image for this so di ko muna i add
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