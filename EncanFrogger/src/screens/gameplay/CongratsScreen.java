package screens.gameplay;

import assets.AssetManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

public class CongratsScreen {

    // --- Assets ---
    private final Image bgImage;
    private final Image okImage;
    private Font customFont;

    // --- Score data ---
    private final int previousScore;
    private final int totalScore;
    private final int coins;

    // --- Misc ---
    private final Rectangle okBounds = new Rectangle();

    public CongratsScreen(int previousScore, int totalScore, int coins) {
        this.previousScore = previousScore;
        this.totalScore = totalScore;
        this.coins = coins;

        bgImage = AssetManager.getInstance().getCongrats("levelClearedBackground");
        okImage = AssetManager.getInstance().getButton("playAgainButton");

        customFont = AssetManager.getInstance().getFont("proffaliceHandwrite");
        if (customFont == null)
            customFont = new Font("Arial", Font.BOLD, 24); // fallback
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------
    public void draw(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (bgImage != null)
            g2.drawImage(bgImage, 0, 0, w, h, null);

        g2.setColor(Color.WHITE);
        g2.setFont(customFont.deriveFont((float) (w * 0.035)));

        g2.drawString(String.valueOf(previousScore), (int) (w * 0.53), (int) (h * 0.60));
        g2.drawString(String.valueOf(totalScore), (int) (w * 0.485), (int) (h * 0.879));
        g2.drawString(String.valueOf(coins), (int) (w * 0.55), (int) (h * 0.74));

        drawOkButton(g2, w, h);

        g2.dispose();
    }

    /* OK/continue button */
    private void drawOkButton(Graphics2D g2, int w, int h) {
        int btnW = (int) (w * 0.12);
        int btnH = (okImage != null)
                ? (int) (btnW * okImage.getHeight(null) / (double) okImage.getWidth(null))
                : (int) (btnW * 0.45);

        int btnX = (w - btnW) / 2;
        int btnY = (int) (h * 0.75);

        okBounds.setBounds(btnX, btnY, btnW, btnH);

        if (okImage != null)
            g2.drawImage(okImage, btnX, btnY, btnW, btnH, null);
    }

    // -------------------------------------------------------------------------
    // Click
    // -------------------------------------------------------------------------
    public boolean isOkClicked(Point p) {
        return okBounds.contains(p);
    }
}