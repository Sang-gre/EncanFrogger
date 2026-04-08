import java.awt.*;
import javax.swing.*;

public class GameSecondPage extends JPanel {

    JButton startBttn;
    JButton menuBttn;
    JButton exitBttn;

    JPanel buttonPanel;
    MainContainer parent;

    private Image background;
    private Image buttonDashboard;

    public GameSecondPage(MainContainer parent){
        this.parent = parent;
        setLayout(new BorderLayout());

        background = new ImageIcon("ASSETS/background.png").getImage();
        buttonDashboard = new ImageIcon("ASSETS/buttonDashboard.png").getImage();

        setupButtons();
    }

    public void setupButtons(){

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
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

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        if (buttonDashboard != null) {
            int targetWidth = getWidth();   
            int targetHeight = buttonDashboard.getHeight(this) * targetWidth / buttonDashboard.getWidth(this); 
            int x = (getWidth() - targetWidth);
            int y = (getHeight() - targetHeight);

            g.drawImage(buttonDashboard, x, y, targetWidth, targetHeight, this);
        }
    }
}
