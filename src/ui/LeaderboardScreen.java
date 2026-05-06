package ui;

import assets.AssetManager;
import java.awt.*;
import java.util.List;
import persistence.LeaderboardManager;
import persistence.ScoreEntry;

public class LeaderboardScreen {

    private Image panelImg;
    private Image playAgainImg;

    private Rectangle playAgainBounds;

    private int scrollOffset = 0;
    private static final int VISIBLE_ROWS = 4;
    private List<ScoreEntry> entries;

    public LeaderboardScreen() {
        panelImg = AssetManager.getBackground("leaderboard");
        playAgainImg = AssetManager.getButton("playAgain");
        entries = LeaderboardManager.loadAll();
    }

    public void scroll(int direction) {
        int maxOffset = Math.max(0, entries.size() - VISIBLE_ROWS);
        scrollOffset = Math.max(0, Math.min(scrollOffset + direction, maxOffset));
    }

    public void draw(Graphics g, int screenW, int screenH) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dim background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, screenW, screenH);

        /* Panel */
        int panelW = screenW;
        int panelH = screenH;
        int panelY = (screenH - panelH) / 2;

        if (panelImg != null)
            g2.drawImage(panelImg, 0, panelY, panelW, panelH, null);

        /* Column Layout */
        int contentTop = panelY + (int) (panelH * 0.33);
        int contentH = (int) (panelH * 0.48);
        int rowHeight = contentH / VISIBLE_ROWS;

        int rankX = (int) (panelW * 0.335);
        int nameX = (int) (panelW * 0.37);
        int scoreX = (int) (panelW * 0.655);

        int maxOffset = Math.max(0, entries.size() - VISIBLE_ROWS);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));

        int fontSize = Math.max(16, panelH / 23);
        Font baseFont = AssetManager.getFont("enchantedLand");
        Font font = baseFont.deriveFont((float) fontSize);
        g2.setFont(font);

        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int entryIndex = i + scrollOffset;
            int rowMidY = contentTop + i * rowHeight + rowHeight / 2;
            int textY = rowMidY + fontSize / 3;

            if (entryIndex < entries.size()) {
                ScoreEntry e = entries.get(entryIndex);

                Color rankColor = switch (entryIndex) {
                    case 0 -> new Color(255, 215, 0);
                    case 1 -> new Color(192, 192, 192);
                    case 2 -> new Color(205, 127, 50);
                    default -> Color.WHITE;
                };

                g2.setColor(rankColor);
                g2.drawString("#" + (entryIndex + 1), rankX, textY);

                g2.setColor(Color.WHITE);
                g2.drawString(e.initials.toUpperCase(), nameX, textY);

                String scoreStr = String.format("%06d", e.score);
                FontMetrics fm = g2.getFontMetrics();
                int scoreDrawX = scoreX - fm.stringWidth(scoreStr);
                g2.drawString(scoreStr, scoreDrawX, textY);

            } else {
                g2.setColor(new Color(180, 180, 180, 100));
                g2.drawString("---", nameX, textY);
                g2.drawString("---", scoreX - g2.getFontMetrics().stringWidth("---"), textY);
            }
        }

        /* Scroll indicators */
        if (entries.size() > VISIBLE_ROWS) {
            int arrowW = (int) (panelW * 0.04);
            int arrowH = (int) (panelH * 0.025);
            int arrowX = panelW / 2;

            // Up arrow
            int upAlpha = scrollOffset > 0 ? 220 : 50;
            int upY = contentTop - (int) (panelH * 0.05);
            g2.setColor(new Color(255, 215, 0, upAlpha));
            g2.fillPolygon(
                    new int[] { arrowX, arrowX + arrowW, arrowX - arrowW },
                    new int[] { upY - arrowH, upY, upY },
                    3);
            g2.setColor(new Color(180, 130, 0, upAlpha));
            g2.setStroke(new BasicStroke(2));
            g2.drawPolygon(
                    new int[] { arrowX, arrowX + arrowW, arrowX - arrowW },
                    new int[] { upY - arrowH, upY, upY },
                    3);

            // Down arrow
            int downAlpha = scrollOffset < maxOffset ? 220 : 50;
            int downY = contentTop + contentH + (int) (panelH * 0.04);
            g2.setColor(new Color(255, 215, 0, downAlpha));
            g2.fillPolygon(
                    new int[] { arrowX, arrowX + arrowW, arrowX - arrowW },
                    new int[] { downY + arrowH, downY, downY },
                    3);
            g2.setColor(new Color(180, 130, 0, downAlpha));
            g2.drawPolygon(
                    new int[] { arrowX, arrowX + arrowW, arrowX - arrowW },
                    new int[] { downY + arrowH, downY, downY },
                    3);

            g2.setStroke(new BasicStroke(1));
        }

        // --- Play Again button ---
        int btnH = (int) (screenH * 0.20);
        int btnW = (int) (btnH * ((double) playAgainImg.getWidth(null) / playAgainImg.getHeight(null)));
        int btnX = (int) (screenW * 0.79);
        int btnY = (int) (screenH * 0.8);

        playAgainBounds = new Rectangle(btnX, btnY, btnW, btnH);
        if (playAgainImg != null)
            g2.drawImage(playAgainImg, btnX, btnY, btnW, btnH, null);
    }

    public boolean isPlayAgainClicked(Point p) {
        return playAgainBounds != null && playAgainBounds.contains(p);
    }
}