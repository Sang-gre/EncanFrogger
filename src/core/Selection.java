package core;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import assets.AssetManager;
import assets.SoundManager;
import level.LevelManager;
import ui.PopupDialog;

public abstract class Selection extends JPanel {

    private final GamePanel gamePanel;
    protected final Runnable onBack;
    private SoundManager sound = new SoundManager();

    public Selection(GamePanel gamePanel, Runnable onBack) {
        this.gamePanel = gamePanel;
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

        add(createBackground(), BorderLayout.CENTER);
    }

    public JPanel createBackground() {
        JPanel background = new JPanel(new BorderLayout()) {
            private final Image img = AssetManager.getInstance().getBackground("characterSelect");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        background.setOpaque(true);
        background.add(createSelectionButtons(), BorderLayout.CENTER);
        background.add(createNavButtons(), BorderLayout.SOUTH);

        return background;
    }

    protected JPanel createNavButtons() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(800, 100));

        int btnWidth = 140;
        int btnHeight = 60;
        int margin = 20;

        JButton backBtn = null;

        if (onBack != null) {
            backBtn = createImageButton(AssetManager.getInstance().getButton("back"), btnWidth, btnHeight);
            backBtn.addActionListener(e -> {
                onBack.run();
                sound.play("click");
            });
            panel.add(backBtn);
        }

        final JButton nextBtn = createImageButton(AssetManager.getInstance().getButton("next"), btnWidth, btnHeight);
        nextBtn.addActionListener((ActionEvent e) -> {
            if (!validateSelection()) {
                showPopupDialog();
                return;
            }
            sound.play("click");
            onNext();
        });

        panel.add(nextBtn);

        JButton finalBackBtn = backBtn;
    
        JLabel coinLabel = new JLabel();
        int coins = new CollisionSystem().getCoinsCollected();
        coinLabel.setText(Integer.toString(coins * 20)); //20 kase wala lang huhu

        JLabel levelLabel = new JLabel();
        int level = new LevelManager(getWidth(), getHeight()).getCurrentLevel();
        levelLabel.setText("LEVEL " +  level);
        //set font and whatsoever

        panel.add(coinLabel);
        panel.add(levelLabel);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int w = panel.getWidth();
                int h = panel.getHeight();

                if (finalBackBtn != null) {
                    finalBackBtn.setBounds(
                            margin,
                            h - btnHeight,
                            btnWidth,
                            btnHeight);
                }

                nextBtn.setBounds(
                        w - btnWidth - margin,
                        h - btnHeight,
                        btnWidth,
                        btnHeight);


                //for the labels din po
                coinLabel.setBounds()
                
            }
        });

        
        return panel;
    }

    protected abstract void onNext();

    public JButton createImageButton(Image img, int width, int height) {

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

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public Runnable getOnBack() {
        return onBack;
    }
}