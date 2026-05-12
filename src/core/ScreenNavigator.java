package core;

import gameobjects.Player;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;

/**
 * Handles all screen transitions: swapping out Swing panels and updating
 * GameStateManager accordingly. GamePanel delegates every "show X screen"
 * call here so it stays free of layout/navigation logic.
 */
public class ScreenNavigator {

    private final GamePanel gamePanel;
    private final GameStateManager stateManager;
    private final assets.SoundManager sound;
    private final main.GameLauncher launcher;

    public ScreenNavigator(GamePanel gamePanel,
                           GameStateManager stateManager,
                           assets.SoundManager sound,
                           main.GameLauncher launcher) {
        this.gamePanel = gamePanel;
        this.stateManager = stateManager;
        this.sound = sound;
        this.launcher = launcher;
    }

    public void showMapSelect(Player selectedPlayer) {
        showMapSelect(selectedPlayer,
                stateManager.getCurrentMap() != null
                        ? stateManager.getCurrentMap()
                        : GameMap.LIREO);
    }

    public void showMapSelect(Player selectedPlayer, GameMap map) {
        stateManager.setState(GameState.MAP_SELECT);
        stateManager.setCurrentMap(map);

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
        stateManager.setFreshStart(true);

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
        sound.stopBGM();
        sound.playBGM("menu");
        removePanelComponentListeners();

        stateManager.setState(GameState.CHARACTER_SELECT);
        stateManager.setFreshStart(false);

        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(
                new CharacterSelect(gamePanel, () -> launcher.showMainMenu()),
                BorderLayout.CENTER);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void showLevelSelect(Player player, GameMap map) {
        gamePanel.stopThreads();
        stateManager.setState(GameState.LEVEL_SELECT);

        gamePanel.removeAll();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(
                new LevelSelect(gamePanel, () -> showMapSelect(player), player, map),
                BorderLayout.CENTER);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void showLeaderboard() {
        stateManager.setShowingLeaderboard(true);
        SwingUtilities.invokeLater(() -> {
            gamePanel.setLeaderboardScreen(new ui.LeaderboardScreen());
            gamePanel.requestFocusInWindow();
            gamePanel.repaint();
        });
    }

    private void removePanelComponentListeners() {
        for (java.awt.event.ComponentListener cl : gamePanel.getComponentListeners()) {
            gamePanel.removeComponentListener(cl);
        }
    }
}