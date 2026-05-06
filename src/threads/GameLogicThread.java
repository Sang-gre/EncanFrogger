package threads;

import core.GamePanel;

public class GameLogicThread extends Thread {

    private GamePanel panel;
    private boolean running = true;

    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;

    public GameLogicThread(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {

        long start;
        long frameTime = 1000 / 60;

        while (running) {
            start = System.currentTimeMillis();

            panel.updateGame();

            long elapsed = System.currentTimeMillis() - start;
            long sleep = frameTime - elapsed;

            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public void stopThread() {
        running = false;
    }
}