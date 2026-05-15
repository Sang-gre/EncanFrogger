package screens.menu;

import assets.AssetManager;
import assets.SoundManager;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import main.GameLauncher;

public class InstructionsPanel extends JPanel {

    private GameLauncher launcher;
    private Image[] pages;
    private final Image background;

    private Image leftImg;
    private Image rightImg;
    private Image exitImg;

    private int currentPage = 0;

    private JButton leftButton;
    private JButton rightButton;
    private JButton xButton;

    private Runnable onExit = () -> launcher.showMainMenu(); // default
    SoundManager sound = new SoundManager();

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    public InstructionsPanel(GameLauncher launcher) {
        this.launcher = launcher;

        setFocusable(true);
        setLayout(null);

        background = AssetManager.getInstance().getBackground("title");

        loadPages();
        setupButtons();
        setupKeys();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private ImageIcon scaleIcon(Image img, int width, int height) {
        return new ImageIcon(
                img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
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

    private void updateResponsiveLayout() {
        int w = getWidth();
        int h = getHeight();

        int btnSize = Math.max(48, Math.min(w, h) / 8);

        leftButton.setIcon(scaleIcon(leftImg, btnSize, btnSize));
        rightButton.setIcon(scaleIcon(rightImg, btnSize, btnSize));
        xButton.setIcon(scaleIcon(exitImg, btnSize, btnSize));

        leftButton.setBounds(w / 15, h / 2 - btnSize / 2, btnSize, btnSize);
        rightButton.setBounds(w - btnSize - w / 12, h / 2 - btnSize / 2, btnSize, btnSize);

        int xMargin = w / 8;
        xButton.setBounds(w - btnSize - xMargin, h / 25, btnSize, btnSize);
    }

    private void setupButtons() {
        leftImg = AssetManager.getInstance().getButton("leftArrow");
        rightImg = AssetManager.getInstance().getButton("rightArrow");
        exitImg = AssetManager.getInstance().getButton("xButton");

        leftButton = new JButton();
        rightButton = new JButton();
        xButton = new JButton();

        for (JButton btn : new JButton[] { leftButton, rightButton, xButton }) {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setFocusable(false);
            add(btn);
        }

        leftButton.addActionListener(e -> {

            sound.play("click");

            animateButtonPress(leftButton);
            if (currentPage > 0) {
                currentPage--;
                repaint();
                updateButtonVisibility();
            }
            requestFocusInWindow();
        });

        rightButton.addActionListener(e -> {
            sound.play("click");

            animateButtonPress(rightButton);
            if (currentPage < pages.length - 1) {
                currentPage++;
                repaint();
                updateButtonVisibility();
            }
            requestFocusInWindow();
        });

        xButton.addActionListener(e -> {
            sound.play("click");

            animateButtonPress(xButton);
            currentPage = 0; // reset page for next time
            Timer t = new Timer(100, ev -> onExit.run());
            t.setRepeats(false);
            t.start();
            requestFocusInWindow();
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateResponsiveLayout();
            }
        });

        updateResponsiveLayout();
        updateButtonVisibility();
    }

    private void setupKeys() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        sound.play("click");

                        if (currentPage < pages.length - 1) {
                            currentPage++;
                            repaint();
                            updateButtonVisibility();
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        sound.play("click");
                        if (currentPage > 0) {
                            currentPage--;
                            repaint();
                            updateButtonVisibility();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        sound.play("click");
                        currentPage = 0;
                        onExit.run();
                    }
                }
            });
        }


    private void loadPages() {
        pages = new Image[] {
                AssetManager.getInstance().getInstructions("instruction1"),
                AssetManager.getInstance().getInstructions("instruction2"),
                AssetManager.getInstance().getInstructions("instruction3"),
                AssetManager.getInstance().getInstructions("instruction4"),
                AssetManager.getInstance().getInstructions("instruction5"),
                AssetManager.getInstance().getInstructions("instruction6"),
                AssetManager.getInstance().getInstructions("instruction7")
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