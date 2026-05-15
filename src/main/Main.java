package main;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Main {

    public static void main(String[] args) {
        boolean testMode = args.length > 0 && args[0].equals("--test");

        if (testMode) {
            SwingUtilities.invokeLater(() -> GameTester.launch());
        } else {
            SwingUtilities.invokeLater(() -> {
                SplashScreen splash = new SplashScreen();

                SwingWorker<Void, String> loader = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() {
                        publish("Loading assets...");
                        assets.AssetManager.getInstance();
                        publish("Starting game...");
                        return null;
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        splash.setLoadingText(chunks.get(chunks.size() - 1));
                    }

                    @Override
                    protected void done() {
                        splash.dismiss();
                        GameLauncher launcher = new GameLauncher();
                    }
                };

                loader.execute();
            });
        }
    }
}