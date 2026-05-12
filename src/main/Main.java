package main;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        boolean testMode = args.length > 0 && args[0].equals("--test");

        if (testMode) {
            SwingUtilities.invokeLater(() -> GameTester.launch());
        } else {
            SwingUtilities.invokeLater(GameLauncher::new);
        }
    }
}