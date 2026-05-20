package core.handlers;

import core.GamePanel;
import core.level.GameMap;
import core.logic.GameState;
import core.logic.GameStateManager;
import gameobjects.Player;
import java.awt.BorderLayout;
import java.awt.event.ComponentListener;
import launch.GameLauncher;
import screens.input.CharacterSelect;
import screens.input.MapSelect;
import screens.menu.LeaderboardScreen;

/* Manages transitions between game screens (character select, map select, leaderboard) */
public class ScreenNavigator {

    private final GamePanel gamePanel;
    private final GameStateManager stateManager;
    private final GameLauncher launcher;

    public ScreenNavigator(GamePanel gamePanel,
            GameStateManager stateManager,
            GameLauncher launcher) {
        this.gamePanel = gamePanel;
        this.stateManager = stateManager;
        this.launcher = launcher;
    }

    // -------------------------------------------------------------------------
    // Screen transitions
    // -------------------------------------------------------------------------
    public void showMapSelect(Player selectedPlayer) {
        stateManager.setState(GameState.MAP_SELECT);

        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(
                new MapSelect(gamePanel, () -> launcher.showMainMenu(), selectedPlayer),
                BorderLayout.CENTER);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void showMapSelect(Player selectedPlayer, GameMap map) {
        stateManager.setState(GameState.MAP_SELECT);

        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(
                new MapSelect(gamePanel, () -> launcher.showMainMenu(), selectedPlayer),
                BorderLayout.CENTER);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void showCharacterSelect() {
        gamePanel.stopThreads();
        removePanelComponentListeners();

        stateManager.setState(GameState.CHARACTER_SELECT);

        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(
                new CharacterSelect(gamePanel, () -> launcher.showMainMenu()),
                BorderLayout.CENTER);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void showCharacterSelectNextLevel() {
        gamePanel.stopThreads();
        removePanelComponentListeners();

        stateManager.setState(GameState.CHARACTER_SELECT);

        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(
                new CharacterSelect(gamePanel, () -> launcher.showMainMenu()),
                BorderLayout.CENTER);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void showLeaderboard() {
        stateManager.setShowingLeaderboard(true);
        gamePanel.setLeaderboardScreen(new LeaderboardScreen());
        gamePanel.requestFocusInWindow();
        gamePanel.repaint();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private void removePanelComponentListeners() {
        for (ComponentListener cl : gamePanel.getComponentListeners()) {
            gamePanel.removeComponentListener(cl);
        }
    }
}