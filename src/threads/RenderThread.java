package threads;

import javax.swing.SwingUtilities;

import core.GamePanel;

public class RenderThread extends Thread {

    private GamePanel panel;
    private boolean running = true;

    // target FPS (same as logic thread)
    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;

    public RenderThread(GamePanel panel) {
        this.panel = panel;
    }

     @Override
    public void run() {

        while (running) {

            long startTime = System.currentTimeMillis(); // track frame start

            // ensure Swing rendering happens on EDT
            SwingUtilities.invokeLater(panel::repaint);

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = FRAME_TIME - elapsed;

            // consistent frame pacing
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                // if we're behind, don't sleep (prevents lag accumulation)
                Thread.yield();
            }
        }
    }


    public void stopThread() {
        running = false;
    }
}
