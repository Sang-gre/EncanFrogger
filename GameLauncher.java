/*
dito yung first window to appear sa game, nasa loob siya ng cardLayouted na MainContainer 
*/
import javax.swing.*;
import java.awt.*;

public class GameLauncher extends JPanel{
    //components
    JButton startBttn;
    JButton menuBttn;
    JButton exitBttn;

    JPanel buttonPanel;

    public GameLauncher(){
        setLayout(new BorderLayout());

        setupButtons();
    }

    public void setupButtons(){
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,  BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //button initialization
        startBttn = new JButton("START");
        menuBttn = new JButton("MENU");
        exitBttn = new JButton("EXIT");

        //button alignment
        startBttn.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuBttn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBttn.setAlignmentX(Component.CENTER_ALIGNMENT);

        //button spacing
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(startBttn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(menuBttn);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(exitBttn);
        buttonPanel.add(Box.createVerticalGlue());



        add(buttonPanel, BorderLayout.CENTER);
        
    }
}
