package main;

import assets.AssetManager;
import core.GamePanel;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import persistence.LeaderboardManager;
import persistence.ScoreEntry;
import screens.input.InitialsPanel;
import screens.menu.InstructionsPanel;
import screens.menu.MainPanel;
import screens.menu.TitlePanel;
import ui.CursorGlassPane;

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

        Rectangle screenBounds = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        int screenWidth = screenBounds.width;
        int screenHeight = screenBounds.height;

        int windowWidth = (int) (screenWidth * 0.8);
        int windowHeight = (int) (screenHeight * 0.8);

        setSize(windowWidth, windowHeight);

        setMinimumSize(new Dimension(
                (int) (screenWidth * 0.6),
                (int) (screenHeight * 0.6)));

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
                this::showMainMenu,
                this::handleInitialsSubmit,
                this::showLeaderboardFromInitials);

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
    public void resetSessionState() {
        startingLevel = DEFAULT_LEVEL;
        startingScore = DEFAULT_SCORE;
        playerInitials = DEFAULT_INITIALS;
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
        resetSessionState();
        gamePanel.resetGameOverState();
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
        } else {
            startingLevel = DEFAULT_LEVEL;
            startingScore = DEFAULT_SCORE;
        }

        playerInitials = initials;
        gamePanel.setPlayerInitials(initials);
        gamePanel.showCharacterSelect();
        startGame();
    }

    private void showLeaderboardFromInitials() {
        startGame();
        gamePanel.showLeaderboard();
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

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------
    private Component getActivePanel() {
        for (Component c : mainPanel.getComponents()) {
            if (c.isVisible())
                return c;
        }
        return gamePanel;
    }

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
}