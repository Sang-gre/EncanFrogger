package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPanel extends JPanel {

    JButton startBttn;
    JButton menuBttn;
    JButton exitBttn;

    JPanel buttonPanel;
    GameLauncher parent;

    private Image background;
    private Image buttonDashboard;

    private Image startImg;
    private Image menuImg;
    private Image exitImg;

    public MainPanel(GameLauncher parent) {
        this.parent = parent;

        setLayout(null);

        loadAssets();
        setupButtons();
    }

    private void loadAssets() {
        background = new ImageIcon("ASSETS/background.png").getImage();
        buttonDashboard = new ImageIcon("ASSETS/buttonDashboard.png").getImage();

        startImg = new ImageIcon("ASSETS/startButton.png").getImage();
        menuImg  = new ImageIcon("ASSETS/menuButton.png").getImage();
        exitImg  = new ImageIcon("ASSETS/exitButton.png").getImage();
    }

    private void setupButtons() {

        buttonPanel = new JPanel(null);
        buttonPanel.setOpaque(false);

        startBttn = createButton(startImg);
        menuBttn  = createButton(menuImg);
        exitBttn  = createButton(exitImg);

        buttonPanel.add(startBttn);
        buttonPanel.add(menuBttn);
        buttonPanel.add(exitBttn);

        add(buttonPanel);

        startBttn.addActionListener(e -> parent.startGame());
        menuBttn.addActionListener(e -> parent.menuGame());
        exitBttn.addActionListener(e -> System.exit(0));
    }

    private JButton createButton(Image img) {
        JButton btn = new JButton();
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setIcon(new ImageIcon(img));

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

    @Override
    public void doLayout() {
        super.doLayout();

        int panelW = (int)(getWidth() * 0.30);
        int panelH = (int)(getHeight() * 0.50);

        int x = (getWidth() - panelW) / 2;
        int y = (int)(getHeight() * 0.30);

        buttonPanel.setBounds(x, y, panelW, panelH);

        int gap = (int)(panelH * 0.02);
        int btnW = (int)(panelW * 0.70);
        int btnH = (panelH - (gap * 4)) / 3;

        int centerX = (panelW - btnW) / 2;

        startBttn.setBounds(centerX, gap, btnW, btnH);
        menuBttn.setBounds(centerX, gap * 2 + btnH, btnW, btnH);
        exitBttn.setBounds(centerX, gap * 3 + btnH * 2, btnW, btnH);

        scaleButton(startBttn, startImg, btnW, btnH);
        scaleButton(menuBttn, menuImg, btnW, btnH);
        scaleButton(exitBttn, exitImg, btnW, btnH);
    }

    private void scaleButton(JButton button, Image img, int w, int h) {
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaled));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        if (buttonDashboard != null) {
            int targetW = getWidth();
            int targetH = buttonDashboard.getHeight(this) * targetW / buttonDashboard.getWidth(this);

            g.drawImage(buttonDashboard, 0, getHeight() - targetH, targetW, targetH, this);
        }
    }
}