/*
dito yung first window to appear sa game, nasa loob siya ng cardLayouted na MainContainer 
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameLauncher extends JPanel {

    MainContainer parent;

    public GameLauncher(MainContainer parent){
        this.parent = parent;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("ENCANFROGGER", JLabel.CENTER);
        JLabel title2 = new JLabel("THE ADVENTURES OF SANG'GRES", JLabel.CENTER);
        JLabel subtitle = new JLabel("Click anywhere to continue", JLabel.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 28));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(title2);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(subtitle);
        textPanel.add(Box.createVerticalGlue());

        add(textPanel, BorderLayout.CENTER);

        
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                parent.showSecondPage();
            }
        });
    }
}
