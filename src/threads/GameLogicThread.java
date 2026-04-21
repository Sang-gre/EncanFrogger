package threads;

import core.GamePanel;

public class GameLogicThread extends Thread {

    private GamePanel panel;
    private boolean running = true;

    public GameLogicThread(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        while (running) {
            panel.updateGame();

            try {
                Thread.sleep(16); //60 up per sec
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopThread() {
        running = false;
    }
}