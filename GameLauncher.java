/*
dito yung first window to appear sa game, nasa loob siya ng cardLayouted na MainContainer 
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameLauncher extends JPanel {

    MainContainer parent;
    private Image background;
    private Image titleFont;

    public GameLauncher(MainContainer parent){
        this.parent = parent;

        background = new ImageIcon("ASSETS/background.png").getImage();
        titleFont = new ImageIcon("ASSETS/titleFont.png").getImage();

        setLayout(null);
        
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                parent.showSecondPage();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
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
