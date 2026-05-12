package core;

import javax.swing.*;
import java.awt.*;

public class LevelPanel extends JPanel{
    JButton[] levelbtns;
    public LevelPanel(){
        setLayout(new GridLayout(4, 5, 10, 10));
        
        levelbtns = new JButton[20];
        
        for (int i = 0; i < levelbtns.length; i++){
            add(levelbtns[i]);
        }

        
    }


}
