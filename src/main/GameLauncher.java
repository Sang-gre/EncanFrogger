package main;

import assets.AssetManager;
import core.GamePanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ui.CursorGlassPane;

public class GameLauncher extends JFrame {

    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    private final TitlePanel gameLaunch;
    private final MainPanel secondPage;
    private final GamePanel gamePanel;

    public GameLauncher() {
        setTitle("EncanFrogger");
        setSize(850, 500);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        gameLaunch = new TitlePanel(this);
        secondPage = new MainPanel(this);
        gamePanel = new GamePanel(this);

        mainPanel.add(gameLaunch, "Launch");
        mainPanel.add(secondPage, "Menu");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);
        launchGame();
        setupCursor();
        setVisible(true);

    }

    private void setupCursor() {
        Image customCursor = AssetManager.getCustomCursor();
        CursorGlassPane glassPane = new CursorGlassPane(customCursor, mainPanel);
        setGlassPane(glassPane);
        glassPane.setVisible(true);
        glassPane.setFocusable(true);
        glassPane.requestFocusInWindow();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image empty = toolkit.createImage("");
        setCursor(toolkit.createCustomCursor(empty, new Point(0, 0), "blank cursor"));

        glassPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point panelPoint = SwingUtilities.convertPoint(glassPane, e.getPoint(), mainPanel);
                Component clicked = SwingUtilities.getDeepestComponentAt(mainPanel, panelPoint.x, panelPoint.y);
                if (clicked != null) {
                    clicked.dispatchEvent(SwingUtilities.convertMouseEvent(glassPane, e, clicked));
                }
            }
        });

        glassPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                gamePanel.dispatchEvent(e);
                gamePanel.requestFocusInWindow();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                gamePanel.dispatchEvent(e);
            }
        });
    }

    public void launchGame() {
        cardLayout.show(mainPanel, "Launch");
    }

    public void menuGame() {
        cardLayout.show(mainPanel, "Menu");
    }

    public void startGame() {
        cardLayout.show(mainPanel, "Game");
        SwingUtilities.invokeLater(() -> getGlassPane().requestFocusInWindow());
    }

    // To remove in final
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public static void main(String[] args) {
        boolean testMode = args.length > 0 && args[0].equals("--test");

        if (testMode) {
            SwingUtilities.invokeLater(() -> GameTester.launch());
        } else {
            SwingUtilities.invokeLater(GameLauncher::new);
        }
    }
}
