import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainContainer extends JFrame {

    JPanel mainPanel;
    CardLayout cardLayout;

    GameLauncher gameLaunch;
    GameSecondPage secondPage;
    GamePanel gamePanel;

    public MainContainer(){
        setTitle("EncanFrogger");
        setSize(850, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        
        gameLaunch = new GameLauncher(this);
        secondPage = new GameSecondPage(this);
        gamePanel = new GamePanel();

        mainPanel.add(gameLaunch, "Launch");
        mainPanel.add(secondPage, "SecondPage");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);

        launchGame();

        Image customCursor = new ImageIcon("ASSETS/customCursor.png").getImage();
        CursorGlassPane glassPane = new CursorGlassPane(customCursor, mainPanel); 
        setGlassPane(glassPane);
        glassPane.setVisible(true);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image empty = toolkit.createImage("");
        setCursor(toolkit.createCustomCursor(empty, new Point(0,0), "blank cursor"));

        glassPane.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point panelPoint = SwingUtilities.convertPoint(glassPane, e.getPoint(), mainPanel);
            Component clicked = SwingUtilities.getDeepestComponentAt(mainPanel, panelPoint.x, panelPoint.y);
            if(clicked != null) {
                clicked.dispatchEvent(SwingUtilities.convertMouseEvent(glassPane, e, clicked));
            }
        }
    });

        setVisible(true);
    }

    public void launchGame(){
        cardLayout.show(mainPanel, "Launch");
    }

    public void showSecondPage(){
        cardLayout.show(mainPanel, "SecondPage");
    }

    public void startGame(){
        cardLayout.show(mainPanel, "Game");
    }

    public void menuGame(){
        cardLayout.show(mainPanel, "SecondPage"); 
    }

    public static void main(String[] args){
        new MainContainer();
    }
}
