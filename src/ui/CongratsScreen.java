package ui;

import assets.AssetManager;
import java.awt.*;

public class CongratsScreen {

    private Image bgImage;
    private Image okImage;
    private Rectangle okBounds = new Rectangle();

    private int previousScore;
    private int totalScore;
    private Font customFont;

    public CongratsScreen(int previousScore, int totalScore) {
        this.previousScore = previousScore;
        this.totalScore = totalScore;

        bgImage = AssetManager.getInstance().getCongrats("levelClearedBackground");
        okImage = AssetManager.getInstance().getButton("playAgainButton");
        customFont = AssetManager.getInstance().getFont("proffaliceHandwrite");

        if (customFont == null) {
            customFont = new Font("Arial", Font.BOLD, 24);
        }
    }

    public void draw(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();

        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, w, h, null);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(customFont.deriveFont((float)(w * 0.035))); // use imported font

        String prevText = String.valueOf(previousScore);
        String totalText = String.valueOf(totalScore);

        int prevLabelY = (int)(h * 0.60);
        int totalLabelY = (int)(h * 0.879);
        int prevLabelX = (int)(w * 0.52);
        int totalLabelX = (int)(w * 0.485);

        g2.drawString(prevText, prevLabelX, prevLabelY);
        g2.drawString(totalText, totalLabelX, totalLabelY);

        int btnW = (int)(w * 0.12);
        int btnH = (okImage != null)
                ? (int)(btnW * okImage.getHeight(null) / (double) okImage.getWidth(null))
                : (int)(btnW * 0.45);

        int btnX = (w - btnW) / 2;
        int btnY = (int)(h * 0.75);

        okBounds.setBounds(btnX, btnY, btnW, btnH);

        if (okImage != null) {
            g2.drawImage(okImage, btnX, btnY, btnW, btnH, null);
        }

        g2.dispose();
    }


    public boolean isOkClicked(Point p) {
        return okBounds.contains(p);
    }
}
