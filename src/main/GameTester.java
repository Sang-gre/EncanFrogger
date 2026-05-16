package main;

import core.GamePanel;
import core.level.GameMap;
import core.logic.GameState;
import gameobjects.Player;
import gameobjects.PlayerType;
import javax.swing.SwingUtilities;

public class GameTester {

    private static final GameMap TEST_MAP = GameMap.LIREO;

    public static void launch() {
        GameLauncher launcher = new GameLauncher();
        launcher.setTitle("TEST");

        GamePanel gamePanel = launcher.getGamePanel();

        SwingUtilities.invokeLater(() -> {
            Player player = new Player(0, 0, PlayerType.PAOPAO);
            gamePanel.startLevel(player, TEST_MAP, TEST_MAP.getStartLevel());
            gamePanel.setState(GameState.PLAYING);
            launcher.startGame();
            gamePanel.requestFocusInWindow();
        });
    }
}