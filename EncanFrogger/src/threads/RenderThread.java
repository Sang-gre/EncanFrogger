package threads;

import core.GamePanel;
import javax.swing.SwingUtilities;

/*  Runs the render loop at a fixed 60 FPS
    Schedules panel repaints on the EDT */
public class RenderThread extends Thread {

    private static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;

    private final GamePanel panel;
    private volatile boolean running = true;

    public RenderThread(GamePanel panel) {
        this.panel = panel;
    }

    // -------------------------------------------------------------------------
    // Thread loop
    // -------------------------------------------------------------------------
    @SuppressWarnings({"BusyWait", "CallToThreadYield"})
    @Override
    public void run() {
        while (running) {
            long start = System.currentTimeMillis();

            SwingUtilities.invokeLater(panel::repaint);

            long elapsed = System.currentTimeMillis() - start;
            long sleep = FRAME_TIME - elapsed;

            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                // Frame runs too long, let other threads run
                Thread.yield();
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