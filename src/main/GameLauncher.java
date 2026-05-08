package main;

import core.GamePanel;
import core.InitialsPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import managers.AssetManager;
import persistence.LeaderboardManager;
import persistence.ScoreEntry;
import ui.CursorGlassPane;
import ui.InstructionsPanel;

public class GameLauncher extends JFrame {

    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    private final TitlePanel gameLaunch;
    private final MainPanel secondPage;
    private final InstructionsPanel instructionsPanel;
    private final GamePanel gamePanel;
    private InitialsPanel initialsPanel;

    private int startingScore = 0;

    // Initials collected at the start of each session
    private String playerInitials = "NAME";
    private int startingLevel = 1;

    public GameLauncher() {
        setTitle("EncanFrogger");
        setSize(850, 500);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Image icon = AssetManager.getInstance().getLogoImage("logo");
        
        if (icon != null) {
            setIconImage(icon);
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        gameLaunch = new TitlePanel(this);
        secondPage = new MainPanel(this);
        instructionsPanel = new InstructionsPanel(this);
        gamePanel = new GamePanel(this);
        initialsPanel = new InitialsPanel(
                this::menuGame,
                initials -> {
                    List<ScoreEntry> existing = LeaderboardManager.loadAll();
                    ScoreEntry match = existing.stream()
                            .filter(e -> e.initials.equalsIgnoreCase(initials))
                            .findFirst()
                            .orElse(null);

                    if (match != null && !match.isAlive) {
                        SwingUtilities.invokeLater(() -> initialsPanel.showDeadPopup());
                        return;
                    }

                    if (match != null && match.isAlive) {
                        startingLevel = match.level;
                        startingScore = match.score;
                        playerInitials = initials;
                        startGame();
                        return;
                    }

                    startingLevel = 1;
                    startingScore = 0;
                    playerInitials = initials;
                    startGame();
                });

        mainPanel.add(gameLaunch, "Launch");
        mainPanel.add(secondPage, "Menu");
        mainPanel.add(initialsPanel, "Initials");
        mainPanel.add(instructionsPanel, "Instructions");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);
        launchGame();
        setupCursor();
        setVisible(true);
    }

    public int getStartingLevel() {
        return startingLevel;
    }

    private void setupCursor() {
        Image customCursor = AssetManager.getInstance().getCustomCursor();
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
                Component clicked = SwingUtilities.getDeepestComponentAt(
                        mainPanel, panelPoint.x, panelPoint.y);
                if (clicked != null) {
                    clicked.dispatchEvent(
                            SwingUtilities.convertMouseEvent(glassPane, e, clicked));
                }
            }
        });

        glassPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Component active = getActivePanel();
                active.dispatchEvent(e);
                active.requestFocusInWindow();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                getActivePanel().dispatchEvent(e);
            }
        });
    }

    private Component getActivePanel() {
        for (Component c : mainPanel.getComponents()) {
            if (c.isVisible())
                return c;
        }
        return gamePanel;
    }

    public void launchGame() {
        cardLayout.show(mainPanel, "Launch");
    }

    public void menuGame() {
        cardLayout.show(mainPanel, "Menu");
    }

    public void showInitialsPanel() {
        gamePanel.resetGameOverState();
        gamePanel.showCharacterSelect();
        cardLayout.show(mainPanel, "Initials");
        SwingUtilities.invokeLater(initialsPanel::activate);
    }
    public void showMainMenu() {
        cardLayout.show(mainPanel, "Menu");
}

    public void showInstructions() {
        cardLayout.show(mainPanel, "Instructions");

    SwingUtilities.invokeLater(() -> {
        instructionsPanel.requestFocusInWindow();
    });
}

    public void startGame() {
        cardLayout.show(mainPanel, "Game");
        SwingUtilities.invokeLater(() -> getGlassPane().requestFocusInWindow());
    }

    public String getPlayerInitials() {
        return playerInitials;
    }

    // To remove in final
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public int getStartingScore() {
        return startingScore;
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