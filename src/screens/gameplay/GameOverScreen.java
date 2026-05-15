package screens.gameplay;

import assets.AssetManager;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

public class GameOverScreen {

    private Image bgImage;
    private Image yesImage;
    private Image noImage;

    private Rectangle yesBounds = new Rectangle();
    private Rectangle noBounds = new Rectangle();

    public GameOverScreen() {
        bgImage = AssetManager.getInstance().getGameOver("background");
        yesImage = AssetManager.getInstance().getButton("yes");
        noImage = AssetManager.getInstance().getButton("no");
    }

    public void draw(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (bgImage != null)
            g2.drawImage(bgImage, 0, 0, w, h, null);

        int btnW = (int) (w * 0.11);
        int btnH = (yesImage != null)
                ? (int) (btnW * yesImage.getHeight(null) / (double) yesImage.getWidth(null))
                : (int) (btnW * 0.45);
        int btnY = (int) (h * 0.74);
        int gap = (int) (w * 0.03);

        int yesX = (w / 2) - btnW - (gap / 2);
        int noX = (w / 2) + (gap / 2);

        yesBounds.setBounds(yesX, btnY, btnW, btnH);
        noBounds.setBounds(noX, btnY, btnW, btnH);

        if (yesImage != null)
            g2.drawImage(yesImage, yesX, btnY, btnW, btnH, null);
        if (noImage != null)
            g2.drawImage(noImage, noX, btnY, btnW, btnH, null);

        g2.dispose();
    }

    public boolean isYesClicked(Point p) {
        return yesBounds.contains(p);
    }

    public boolean isNoClicked(Point p) {
        return noBounds.contains(p);
    }
    
    public boolean isBannerClicked(Point p) {
        return false;
    }

    public boolean isOkClicked(Point p) {
        return false;
    }

    public boolean handleKey(int keyCode, char keyChar) {
        return true;
    }

    public boolean isShowingPlayAgain() {
        return true;
    }

    public String getInitials() {
        return "";
    }
}