package screens.menu;

import assets.AssetManager;
import assets.SoundManager;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import main.GameLauncher;

public class TitlePanel extends JPanel {

    SoundManager sound;
    private final Image background;
    private final Image titleFont;

    public TitlePanel(GameLauncher parent) {
        sound = new SoundManager();

        background = AssetManager.getInstance().getBackground("title");
        titleFont = AssetManager.getInstance().getBackground("titleFont");
        sound.playBGM("menu");

        setLayout(null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.showMainMenu();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        if (titleFont != null) {
            int targetWidth = getWidth();
            int targetHeight = titleFont.getHeight(this) * targetWidth / titleFont.getWidth(this);
            int x = (getWidth() - targetWidth) / 2;
            int y = (getHeight() - targetHeight);

            int verticalOffset = +40;
            y += verticalOffset;

            g.drawImage(titleFont, x, y, targetWidth, targetHeight, this);
        }
    }
}
