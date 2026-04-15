import javax.swing.*;
import java.awt.*;

public class MainContainer extends JFrame{
    

    JPanel mainPanel;
    CardLayout cardLayout;

    GameLauncher gameLaunch;
    GamePanel gamePanel;

    public MainContainer(){
        setTitle("EncanFrogger");
        //set icon/logo here po
        setSize(850, 500); //to be edited
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameLaunch = new GameLauncher();
        gamePanel = new GamePanel();


        cardLayout = new CardLayout();
        mainPanel = new JPanel (cardLayout);

        mainPanel.add(gameLaunch, "Launch Game");
        mainPanel.add(gamePanel, "Game Start");

        add(mainPanel);
        
        launchGame();
        
        setVisible(true);
    }

    public void launchGame(){
        cardLayout.show(mainPanel, "Launch Game");
    }

    public void startGame(){
        cardLayout.show(mainPanel, "Start Game");
    }

    public void menuGame(){
        cardLayout.show(mainPanel, "Launch Game");//to be edited once mayda na menu class
    }

    public static void main (String[] args){
        new MainContainer();
    }
}


