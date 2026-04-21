package threads;

import core.GamePanel;

public class RenderThread extends Thread {

    private GamePanel panel;
    private boolean running = true;

    public RenderThread(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        while (running) {
            panel.repaint(); // SAFE

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopThread() {
        running = false;
    }
}
