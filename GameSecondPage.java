import javax.swing.*;
import java.awt.*;

public class GameSecondPage extends JPanel {

    JButton startBttn;
    JButton menuBttn;
    JButton exitBttn;

    JPanel buttonPanel;
    MainContainer parent;

    public GameSecondPage(MainContainer parent){
        this.parent = parent;
        setLayout(new BorderLayout());

        setupButtons();
    }

    public void setupButtons(){

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

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

        //actions
        startBttn.addActionListener(e -> parent.startGame());
        menuBttn.addActionListener(e -> parent.menuGame());
        exitBttn.addActionListener(e -> System.exit(0));
    }
}
