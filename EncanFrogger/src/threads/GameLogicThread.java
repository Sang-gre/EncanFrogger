package threads;

import core.GamePanel;

/*  Runs the game logic update loop at a fixed 60 FPS
    Calls GamePanel.updateGame() each frame */
public class GameLogicThread extends Thread {

    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;

    private final GamePanel panel;
    private volatile boolean running = true;

    public GameLogicThread(GamePanel panel) {
        this.panel = panel;
    }

    // -------------------------------------------------------------------------
    // Thread loop
    // -------------------------------------------------------------------------
    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (running) {
            long start = System.currentTimeMillis();

            panel.updateGame();

            long elapsed = System.currentTimeMillis() - start;
            long sleep = FRAME_TIME - elapsed;

            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Thread control
    // -------------------------------------------------------------------------
    public void stopThread() {
        running = false;
    }
}