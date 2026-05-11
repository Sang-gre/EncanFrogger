package ui;

import assets.AssetManager;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import main.GameLauncher;

public class InstructionsPanel extends JPanel {

    private GameLauncher parent;
    private Image[] pages;
    private Image background;
    private int currentPage = 0;

    private JButton leftButton;
    private JButton rightButton;
    private JButton xButton;

    private static final int BTN_SIZE = 64;

    public InstructionsPanel(GameLauncher parent) {
        this.parent = parent;

        setFocusable(true);
        setLayout(null);

        background = AssetManager.getInstance().getBackground("title");

        loadPages();
        setupButtons();
        setupKeys();

        requestFocusInWindow();
    }

    private ImageIcon scaleIcon(Image img, int width, int height) {
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    private void animateButtonPress(JButton btn) {
        Point original = btn.getLocation();
        btn.setLocation(original.x, original.y + 5);

        Timer timer = new Timer(80, e -> btn.setLocation(original));
        timer.setRepeats(false);
        timer.start();
    }

    private void updateButtonVisibility() {
        leftButton.setVisible(currentPage > 0);
        rightButton.setVisible(currentPage < pages.length - 1);
    }

    private void setupButtons() {

        Image leftImg = AssetManager.getInstance().getButton("leftArrow");
        Image rightImg = AssetManager.getInstance().getButton("rightArrow");
        Image exitImg = AssetManager.getInstance().getButton("xButton");

        leftButton = new JButton(scaleIcon(leftImg, BTN_SIZE, BTN_SIZE));
        rightButton = new JButton(scaleIcon(rightImg, BTN_SIZE, BTN_SIZE));
        xButton = new JButton(scaleIcon(exitImg, BTN_SIZE, BTN_SIZE));

        for (JButton btn : new JButton[]{leftButton, rightButton, xButton}) {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            add(btn);
        }

        leftButton.addActionListener(e -> {
            animateButtonPress(leftButton);

            if (currentPage > 0) {
                currentPage--;
                repaint();
                updateButtonVisibility();
            }
        });

        rightButton.addActionListener(e -> {
            animateButtonPress(rightButton);

            if (currentPage < pages.length - 1) {
                currentPage++;
                repaint();
                updateButtonVisibility();
            }
        });

        xButton.addActionListener(e -> {
            animateButtonPress(xButton);

            Timer t = new Timer(100, ev -> parent.showMainMenu());
            t.setRepeats(false);
            t.start();
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                leftButton.setBounds(20, h / 2 - BTN_SIZE / 2, BTN_SIZE, BTN_SIZE);
                rightButton.setBounds(w - BTN_SIZE - 20, h / 2 - BTN_SIZE / 2, BTN_SIZE, BTN_SIZE);
                xButton.setBounds(w - BTN_SIZE - 110, 20, BTN_SIZE, BTN_SIZE);
            }
        });

        updateButtonVisibility();
    }

    private void setupKeys() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    case KeyEvent.VK_RIGHT:
                        if (currentPage < pages.length - 1) {
                            currentPage++;
                            repaint();
                            updateButtonVisibility();
                        }
                        break;

                    case KeyEvent.VK_LEFT:
                        if (currentPage > 0) {
                            currentPage--;
                            repaint();
                            updateButtonVisibility();
                        }
                        break;

                    case KeyEvent.VK_ESCAPE:
                        currentPage = 0;
                        parent.showMainMenu();
                        break;
                }
            }
        });
    }

    private void loadPages() {
        pages = new Image[]{
            AssetManager.getInstance().getInstructions("instruction1"),
            AssetManager.getInstance().getInstructions("instruction2"),
            AssetManager.getInstance().getInstructions("instruction3"),
            AssetManager.getInstance().getInstructions("instruction4"),
            AssetManager.getInstance().getInstructions("instruction5")
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        if (pages != null && pages[currentPage] != null) {
            g.drawImage(pages[currentPage], 0, 0, getWidth(), getHeight(), this);
        }
    }
}