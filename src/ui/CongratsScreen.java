package ui;

import java.awt.*;
import assets.AssetManager;

public class CongratsScreen {

    private Image bgImage;
    private Image okImage;

    public CongratsScreen() {

        bgImage = AssetManager.getInstance()
                .getCongrats("levelClearedBackground");

        okImage = AssetManager.getInstance()
                .getButton("playAgainButton");
    }

    public void draw(Graphics g, int w, int h) {

        Graphics2D g2 = (Graphics2D) g.create();

        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, w, h, null);
        }

        int btnW = (int)(w * 0.12);

        int btnH = (okImage != null)
                ? (int)(btnW * okImage.getHeight(null)
                / (double) okImage.getWidth(null))
                : (int)(btnW * 0.45);

        int btnX = (w - btnW) / 2;
        int btnY = (int)(h * 0.75);


        if (okImage != null) {
            g2.drawImage(okImage, btnX, btnY, btnW, btnH, null);
        }

        g2.dispose();
    }

    public boolean isOkClicked(Point p, int w, int h) {
    return p != null && p.x >= 0 && p.y >= 0 && p.x <= w && p.y <= h;
}
}