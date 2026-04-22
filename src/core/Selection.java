package core;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class Selection extends JPanel {

    private final GamePanel gamePanel;
    protected final Runnable onBack;

    public Selection(GamePanel gamePanel, Runnable onBack) {
        this.gamePanel = gamePanel;
        this.onBack    = onBack;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));

        add(createBackground(), BorderLayout.CENTER);
    }

    public JPanel createBackground() {
        JPanel background = new JPanel(new BorderLayout()) {
            private final Image img = new ImageIcon("assets/Backgrounds/chooseCharacterBackground.png").getImage();

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
            backBtn = createImageButton("assets/Buttons/backButton.png", btnWidth, btnHeight);
            backBtn.addActionListener(e -> onBack.run());
            panel.add(backBtn);
        }

        final JButton nextBtn = createImageButton("assets/Buttons/nextButton.png", btnWidth, btnHeight);
        nextBtn.addActionListener((ActionEvent e) -> {
            if (!validateSelection()) {
                JOptionPane.showMessageDialog(Selection.this, "Please select a character!");
                return;
            }
            onNext();
        });

        panel.add(nextBtn);

        JButton finalBackBtn = backBtn;

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
                            btnHeight
                    );
                }

                nextBtn.setBounds(
                        w - btnWidth - margin,
                        h - btnHeight,
                        btnWidth,
                        btnHeight
                );
            }
        });

        return panel;
    }

    protected abstract void onNext();

    public JButton createImageButton(String path, int width, int height) {

        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

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


    public GamePanel getGamePanel(){ return gamePanel; }
    public Runnable getOnBack(){ return onBack; }
}