package threads;

import core.GamePanel;
import javax.swing.SwingUtilities;

public class RenderThread extends Thread {

    private GamePanel panel;
    private boolean running = true;

    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;

    public RenderThread(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {

        while (running) {
            long startTime = System.currentTimeMillis();

            SwingUtilities.invokeLater(panel::repaint);

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = FRAME_TIME - elapsed;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                Thread.yield();
            }
        }
    }

    public void stopThread() {
        running = false;
    }
}