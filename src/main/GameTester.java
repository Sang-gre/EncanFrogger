package main;

import characters.Paopao;
import core.GameMap;
import core.GamePanel;
import core.GameState;
import javax.swing.*;

public class GameTester {

    private static final GameMap TEST_MAP = GameMap.LIREO;

    public static void launch() {
        GameLauncher launcher = new GameLauncher();
        launcher.setTitle("TEST");

        GamePanel gamePanel = launcher.getGamePanel();

        SwingUtilities.invokeLater(() -> {
            Paopao player = new Paopao(0, 0);
            gamePanel.startLevel(player, TEST_MAP);
            gamePanel.setState(GameState.PLAYING);
            launcher.startGame();
            gamePanel.requestFocusInWindow();
        });
    }
}