package ui;

import assets.AssetManager;
import java.awt.*;

public class CongratsScreen {

    private Image bgImage;
    private Image okImage;
    private Image levelClearedImage;

    private boolean finalClear;

    public CongratsScreen(boolean finalClear) {

        this.finalClear = finalClear;

        bgImage = AssetManager.getInstance()
                .getCongrats("levelClearedBackground");

        okImage = AssetManager.getInstance()
                .getButton("playAgainButton");


    }

    public void draw(Graphics g, int w, int h) {

        Graphics2D g2 = (Graphics2D) g.create();

        // background
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, w, h, null);
        }

        // ONLY show levelCleared.png on final level
        if (finalClear && levelClearedImage != null) {

            int imgW = (int)(w * 0.45);

            int imgH = (int)(
                    imgW *
                    levelClearedImage.getHeight(null)
                    / (double) levelClearedImage.getWidth(null)
            );

            int imgX = (w - imgW) / 2;
            int imgY = (int)(h * 0.08);

            g2.drawImage(
                    levelClearedImage,
                    imgX,
                    imgY,
                    imgW,
                    imgH,
                    null
            );
        }

        // button
        int btnW = (int)(w * 0.12);

        int btnH = (okImage != null)
                ? (int)(
                        btnW *
                        okImage.getHeight(null)
                        / (double) okImage.getWidth(null)
                )
                : (int)(btnW * 0.45);

        int btnX = (w - btnW) / 2;
        int btnY = (int)(h * 0.75);

        if (okImage != null) {
            g2.drawImage(okImage, btnX, btnY, btnW, btnH, null);
        }

        g2.dispose();
    }
}