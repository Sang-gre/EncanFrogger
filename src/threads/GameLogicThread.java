package threads;

import core.GamePanel;

public class GameLogicThread extends Thread {

    private GamePanel panel;
    private boolean running = true;


    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS; // ~16ms

    public GameLogicThread(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {

        while (running) {

            long startTime = System.currentTimeMillis(); // track frame start

            panel.updateGame(); // game logic update

            long elapsed = System.currentTimeMillis() - startTime; // time spent updating
            long sleepTime = FRAME_TIME - elapsed;

            // only sleep remaining time
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                //  if lagging, skip sleep (prevents slowdown)
                Thread.yield();
            }
        }
    }

    public void stopThread() {
        running = false;
    }
}