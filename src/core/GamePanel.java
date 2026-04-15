package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import gameobjects.Player;
import assets.AssetManager; 

public class GamePanel extends JPanel implements KeyListener {
    private GameState state;
    private Player player;
    private AssetManager assetManager;

    public GamePanel(){
        this.state = GameState.SETTING_UP;
        this.assetManager = new AssetManager();

        setFocusable(true);
        addKeyListener(this);

        chooseChar(); //dire gud didi hiya igcall guys, test run la ini
    }

    @Override
    public void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);

        switch (state) {
            case MENU:
                // draw menu

                break;
            case SHOP:
                // draw shop
                break;
            case LEADERBOARD:
                // draw leaderboard
                break;
            case SETTING_UP:
                chooseChar();

            case PLAYING:
                // draw game
                //startGame();
                break;
            case PAUSED:
                // draw pause screen
                break;
            case GAME_OVER:
                // draw game over screen
                break;
            case WIN:
                // draw win screen
                break;
        }
    } 

    public void startGame() {
        this.state = GameState.PLAYING;
        
        /* threads here */
    }


    private void chooseChar(){
        setLayout(new BorderLayout());

        JPanel characPanel = new JPanel(new BorderLayout());
        JPanel characTitle = new JPanel();
        JPanel characBttn = new JPanel();
            characBttn.setLayout(new BoxLayout(characBttn, BoxLayout.X_AXIS));
        JPanel buttons = new JPanel(new BorderLayout());
            buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        characTitle.add(new JLabel ("CHOOSE CHARACTER"), SwingConstants.CENTER);

        //characters button - i'll be using radio button po
        JRadioButton flammara = new JRadioButton("Flammara"); //remove th ename if not necessary 
        JRadioButton deia = new JRadioButton("Deia");
        JRadioButton adamus = new JRadioButton("Adamus");
        JRadioButton terra = new JRadioButton("Terra");

        ButtonGroup charGroup = new ButtonGroup();
        charGroup.add(flammara);
        charGroup.add(deia);
        charGroup.add(adamus);
        charGroup.add(terra);

        characBttn.add(flammara);
        characBttn.add(Box.createHorizontalStrut(40));
        characBttn.add(deia);
        characBttn.add(Box.createHorizontalStrut(40));
        characBttn.add(adamus);
        characBttn.add(Box.createHorizontalStrut(40));
        characBttn.add(terra);
        characBttn.add(Box.createHorizontalStrut(40));

        JButton next = new JButton("NEXT");
        JButton back = new JButton("BACK");

        buttons.add(back, BorderLayout.WEST);
        buttons.add(next, BorderLayout.EAST);

        characPanel.add(characTitle, BorderLayout.NORTH);
        characPanel.add(buttons, BorderLayout.SOUTH);
        characPanel.add(characBttn, BorderLayout.EAST);

        add(characPanel, BorderLayout.CENTER);
        //
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

            // incomplete pa 'to
            if (state == GameState.PLAYING) {
                switch (key) {
                    case KeyEvent.VK_W:
                        break;
                    case KeyEvent.VK_A:
                        break;
                    case KeyEvent.VK_S:
                        break;
                    case KeyEvent.VK_D:
                        break;
                    case KeyEvent.VK_ESCAPE:
                        state = GameState.PAUSED;
                        break;
                }
            } else if (state == GameState.PAUSED && key == KeyEvent.VK_ESCAPE) {
                state = GameState.PLAYING;
            }        
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    public AssetManager getAssetManager() { return assetManager; }
}
