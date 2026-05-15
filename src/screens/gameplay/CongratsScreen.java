package screens.gameplay;

import assets.AssetManager;
import java.awt.*;

public class CongratsScreen {

    private Image bgImage;
    private Image okImage;
    private Rectangle okBounds = new Rectangle();

    private int previousScore;
    private int totalScore;
    private int coins;
    private Font customFont;

    public CongratsScreen(int previousScore, int totalScore, int coins) {
        this.previousScore = previousScore;
        this.totalScore = totalScore;
        this.coins = coins;

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
        g2.setFont(customFont.deriveFont((float)(w * 0.035)));

        String prevText = String.valueOf(previousScore);
        String totalText = String.valueOf(totalScore);
        String coinsText = String.valueOf(coins);

        int prevLabelX = (int)(w * 0.53);
        int prevLabelY = (int)(h * 0.60);
        int totalLabelX = (int)(w * 0.485);
        int totalLabelY = (int)(h * 0.879);
        int coinsLabelX = (int)(w * 0.55); 
        int coinsLabelY = (int)(h * 0.74);

        g2.drawString(prevText, prevLabelX, prevLabelY);
        g2.drawString(totalText, totalLabelX, totalLabelY);
        g2.drawString(coinsText, coinsLabelX, coinsLabelY);

        // --- Draw button ---
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
