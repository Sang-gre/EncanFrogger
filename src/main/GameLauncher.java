package main;

import assets.AssetManager;
import core.GamePanel;
import core.InitialsPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import persistence.LeaderboardManager;
import persistence.ScoreEntry;
import ui.CursorGlassPane;
import ui.InstructionsPanel;

public class GameLauncher extends JFrame {

    private static final String DEFAULT_INITIALS = "NAME";
    private static final int DEFAULT_LEVEL = 1;
    private static final int DEFAULT_SCORE = 0;

    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    private final TitlePanel gameLaunch;
    private final MainPanel secondPage;
    private final InstructionsPanel instructionsPanel;
    private final GamePanel gamePanel;
    private final InitialsPanel initialsPanel;

    private String playerInitials = DEFAULT_INITIALS;
    private int startingScore = DEFAULT_SCORE;
    private int startingLevel = DEFAULT_LEVEL;

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
        initialsPanel = new InitialsPanel(this::showMainMenu, this::handleInitialsSubmit);

        mainPanel.add(gameLaunch, "Launch");
        mainPanel.add(secondPage, "Menu");
        mainPanel.add(initialsPanel, "Initials");
        mainPanel.add(instructionsPanel, "Instructions");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);
        initLaunch();
        initCursor();

        setVisible(true);
    }

    // -------------------------------------------------------------------------
    // Session state
    // -------------------------------------------------------------------------
    public String getPlayerInitials() {
        return playerInitials;
    }

    public int getStartingLevel() {
        return startingLevel;
    }

    public int getStartingScore() {
        return startingScore;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------
    public void launchGame() {
        cardLayout.show(mainPanel, "Launch");
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "Menu");
    }

    public void showInitialsPanel() {
        gamePanel.resetGameOverState();
        gamePanel.showCharacterSelect();
        cardLayout.show(mainPanel, "Initials");
        SwingUtilities.invokeLater(initialsPanel::activate);
    }

    public void showInstructions(boolean fromPause) {
        if (fromPause) {
            instructionsPanel.setOnExit(() -> {
                cardLayout.show(mainPanel, "Game");
                gamePanel.resumeFromInstructions();
            });
        } else {
            instructionsPanel.setOnExit(this::showMainMenu);
        }
        cardLayout.show(mainPanel, "Instructions");
        SwingUtilities.invokeLater(instructionsPanel::requestFocusInWindow);
    }

    public void startGame() {
        cardLayout.show(mainPanel, "Game");
        SwingUtilities.invokeLater(() -> getGlassPane().requestFocusInWindow());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private void handleInitialsSubmit(String initials) {
        startingLevel = DEFAULT_LEVEL;
        startingScore = DEFAULT_SCORE;

        List<ScoreEntry> existing = LeaderboardManager.loadAll();
        ScoreEntry match = existing.stream()
                .filter(e -> e.initials.equalsIgnoreCase(initials))
                .findFirst()
                .orElse(null);

        if (match != null && !match.isAlive) {
            SwingUtilities.invokeLater(initialsPanel::showDeadPopup);
            return;
        }

        if (match != null) {
            startingLevel = match.level;
            startingScore = match.score;
        }

        playerInitials = initials;
        startGame();
    }

    private void initLaunch() {
        cardLayout.show(mainPanel, "Launch");
    }

    private void initCursor() {
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
}