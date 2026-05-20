package screens.menu;

import assets.AssetManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.List;
import persistence.LeaderboardManager;
import persistence.ScoreEntry;

public class LeaderboardScreen {

    // --- Assets ---
    private Image panelImg;
    private Image playAgainImg;
    private Image backImg;

    // --- Button hit areas ---
    private Rectangle playAgainBounds;
    private Rectangle backBounds;

    // --- Scroll state ---
    private int scrollOffset = 0;
    private static final int VISIBLE_ROWS = 4;

    // --- Data ---
    private List<ScoreEntry> entries;
    private final boolean showPlayAgain;

    public LeaderboardScreen() {
        this(true);
    }

    public LeaderboardScreen(boolean showPlayAgain) {
        this.showPlayAgain = showPlayAgain;

        panelImg = AssetManager.getInstance().getBackground("leaderboard");
        playAgainImg = AssetManager.getInstance().getButton("playAgain");
        backImg = AssetManager.getInstance().getButton("back");

        entries = LeaderboardManager.loadAll();
    }

    // -------------------------------------------------------------------------
    // Scrolling
    // -------------------------------------------------------------------------
    public void scroll(int direction) {
        if (entries == null)
            return;

        int maxOffset = Math.max(0, entries.size() - VISIBLE_ROWS);
        scrollOffset = Math.max(0, Math.min(scrollOffset + direction, maxOffset));
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    public void draw(Graphics g, int screenW, int screenH) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (entries == null)
            return;

        // Dim background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, screenW, screenH);

        if (panelImg != null)
            g2.drawImage(panelImg, 0, 0, screenW, screenH, null);

        drawEntries(g2, screenW, screenH);
        drawButtons(g2, screenW, screenH);

        if (entries.size() > VISIBLE_ROWS)
            drawScrollArrows(g2, screenW, screenH);
    }

    // Draws leaderboard rows with rank, name, and score columns
    private void drawEntries(Graphics2D g2, int screenW, int screenH) {
        int contentTop = (int) (screenH * 0.33);
        int contentH = (int) (screenH * 0.48);
        int rowHeight = contentH / VISIBLE_ROWS;

        int rankX = (int) (screenW * 0.335);
        int nameX = (int) (screenW * 0.4);
        int scoreX = (int) (screenW * 0.655);

        int maxOffset = Math.max(0, entries.size() - VISIBLE_ROWS);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));

        int fontSize = Math.max(16, screenH / 23);
        Font baseFont = AssetManager.getInstance().getFont("proffaliceHandwrite");
        Font font = baseFont.deriveFont((float) fontSize);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int entryIndex = i + scrollOffset;
            int rowMidY = contentTop + i * rowHeight + rowHeight / 2;
            int textY = rowMidY + fontSize / 3;

            if (entryIndex >= entries.size())
                continue;

            ScoreEntry e = entries.get(entryIndex);

            // Gold / silver / bronze for the top three ranks
            Color rankColor = switch (entryIndex) {
                case 0 -> new Color(255, 215, 0);
                case 1 -> new Color(192, 192, 192);
                case 2 -> new Color(205, 127, 50);
                default -> Color.WHITE;
            };

            g2.setColor(rankColor);
            g2.drawString("#" + (entryIndex + 1), rankX, textY);

            g2.setColor(Color.WHITE);
            g2.drawString(e.initials, nameX, textY);

            // Right-align the score so digits line up
            String scoreStr = Integer.toString(e.score);
            int scoreDrawX = scoreX - fm.stringWidth(scoreStr);
            g2.drawString(scoreStr, scoreDrawX, textY);
        }
    }

    private void drawButtons(Graphics2D g2, int screenW, int screenH) {

        // Back button
        int backH = (int) (screenH * 0.20);
        int backW = (int) (backH * ((double) backImg.getWidth(null) / backImg.getHeight(null)));
        int backX = (int) (screenW * 0.02);
        int backY = (int) (screenH * 0.8);

        backBounds = new Rectangle(backX, backY, backW, backH);
        if (backImg != null)
            g2.drawImage(backImg, backX, backY, backW, backH, null);

        // Play again button (only shown when showPlayAgain is true)
        if (showPlayAgain && playAgainImg != null) {
            int btnH = (int) (screenH * 0.20);
            int btnW = (int) (btnH * ((double) playAgainImg.getWidth(null) / playAgainImg.getHeight(null)));
            int btnX = (int) (screenW * 0.79);
            int btnY = (int) (screenH * 0.8);

            playAgainBounds = new Rectangle(btnX, btnY, btnW, btnH);
            g2.drawImage(playAgainImg, btnX, btnY, btnW, btnH, null);
        } else {
            playAgainBounds = null;
        }
    }

    private void drawScrollArrows(Graphics2D g2, int screenW, int screenH) {
        int contentTop = (int) (screenH * 0.33);
        int contentH = (int) (screenH * 0.48);

        int arrowW = (int) (screenW * 0.04);
        int arrowH = (int) (screenH * 0.025);
        int arrowX = screenW / 2;

        // Up arrow
        int upAlpha = scrollOffset > 0 ? 220 : 50;
        int upY = contentTop - (int) (screenH * 0.05);

        g2.setColor(new Color(255, 215, 0, upAlpha));
        g2.fillPolygon(
                new int[] { arrowX, arrowX + arrowW, arrowX - arrowW },
                new int[] { upY - arrowH, upY, upY }, 3);
        g2.setColor(new Color(180, 130, 0, upAlpha));
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(
                new int[] { arrowX, arrowX + arrowW, arrowX - arrowW },
                new int[] { upY - arrowH, upY, upY }, 3);

        // Down arrow
        int downAlpha = scrollOffset < Math.max(0, entries.size() - VISIBLE_ROWS) ? 220 : 50;
        int downY = contentTop + contentH + (int) (screenH * 0.04);

        g2.setColor(new Color(255, 215, 0, downAlpha));
        g2.fillPolygon(
                new int[] { arrowX, arrowX + arrowW, arrowX - arrowW },
                new int[] { downY + arrowH, downY, downY }, 3);
        g2.setColor(new Color(180, 130, 0, downAlpha));
        g2.drawPolygon(
                new int[] { arrowX, arrowX + arrowW, arrowX - arrowW },
                new int[] { downY + arrowH, downY, downY }, 3);

        g2.setStroke(new BasicStroke(1)); // reset stroke
    }

    // -------------------------------------------------------------------------
    // Click
    // -------------------------------------------------------------------------
    public boolean isPlayAgainClicked(Point p) {
        return showPlayAgain && playAgainBounds != null && playAgainBounds.contains(p);
    }

    public boolean isBackClicked(Point p) {
        return backBounds != null && backBounds.contains(p);
    }
}