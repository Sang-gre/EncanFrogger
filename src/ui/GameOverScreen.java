package ui;

import assets.AssetManager;
import java.awt.*;
import java.awt.event.KeyEvent;

public class GameOverScreen {

    private final StringBuilder initials = new StringBuilder();
    private final int MAX_INITIALS = 5;
    private boolean isTyping = false;

    private Image bgImage;
    private Image blankImage;
    private Image activeImage;
    private Image okImage;

    private Rectangle okBounds     = new Rectangle();
    private Rectangle bannerBounds = new Rectangle(); // ← track banner too

    public GameOverScreen() {
        bgImage     = AssetManager.getInstance().getGameOver("background");
        blankImage  = AssetManager.getInstance().getGameOver("enterInitialsBlank");
        activeImage = AssetManager.getInstance().getGameOver("enterInitials");
        okImage     = AssetManager.getInstance().getGameOver("okButton");
    }

    public void draw(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /* BACKGROUND */
        if (bgImage != null)
            g2.drawImage(bgImage, 0, 0, w, h, null);

        /* BANNER — activeImage = "Enter Initials", blankImage = empty for typing */
        int bannerW = (int) (w * 0.68);
        int bannerH = (int) (bannerW * blankImage.getHeight(null) / (double) blankImage.getWidth(null) * 0.90);
        int bannerX = (w - bannerW) / 2;
        int bannerY = (int) (h * 0.35);
        bannerBounds.setBounds(bannerX, bannerY, bannerW, bannerH);

        Image bannerImg = isTyping ? blankImage : activeImage; // ← flipped
        if (bannerImg != null)
            g2.drawImage(bannerImg, bannerX, bannerY, bannerW, bannerH, null);

        /* TYPED INITIALS */
        if (initials.length() > 0) {
            Font font = AssetManager.getInstance().getFont("enchantedLand");
            g2.setFont(font != null
                    ? font.deriveFont(Font.BOLD, bannerH * 0.23f)
                    : new Font("Segoe UI", Font.BOLD, 30));
            g2.setColor(Color.WHITE);
            String display = initials.toString();
            FontMetrics fm = g2.getFontMetrics();
            int textX = bannerX + (bannerW - fm.stringWidth(display)) / 2;
            int textY = bannerY + (int) (bannerH * 0.44);
            g2.drawString(display, textX, textY);
        }

        /* OK BUTTON */
        int okW = (int) (w * 0.15);
        int okH = (int) (okW * okImage.getHeight(null) / (double) okImage.getWidth(null));
        int okX = (w - okW) / 2;
        int okY = bannerY + (int) (bannerH * 0.54);
        okBounds.setBounds(okX, okY, okW, okH);
        if (okImage != null)
            g2.drawImage(okImage, okX, okY, okW, okH, null);

        g2.dispose();
    }

    public boolean handleKey(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (initials.length() > 0)
                initials.deleteCharAt(initials.length() - 1);
            if (initials.length() == 0)
                isTyping = false; // revert to "Enter Initials" prompt
            return true;
        }
        if (keyCode == KeyEvent.VK_ENTER) {
            return false;
        }
        if (Character.isLetter(keyChar) && initials.length() < MAX_INITIALS) {
            isTyping = true;
            initials.append(Character.toUpperCase(keyChar));
            return true;
        }
        return true;
    }

    public boolean isOkClicked(Point p) {
        return okBounds.contains(p);
    }

    public boolean isBannerClicked(Point p) {
        if (bannerBounds.contains(p)) {
            isTyping = true;
            return true;
        }
        return false;
    }

    public String getInitials() {
        return initials.toString();
    }
}