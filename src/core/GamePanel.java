package core;

import assets.AssetManager;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import threads.GameLogicThread;
import threads.RenderThread;

public class GamePanel extends JPanel implements KeyListener {

    private GameState state;
    private Player player;
    private AssetManager assetManager;

    private GameLogicThread logicThread;
    private RenderThread renderThread;

    private JButton nextBtn;
    private JButton backBtn;

    private JPanel background;

    public GamePanel() {
        this.state = GameState.SETTING_UP;
        this.assetManager = new AssetManager();

        setFocusable(true);
        addKeyListener(this);

        chooseChar();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    private void chooseChar() {

        removeAll();
        setLayout(new BorderLayout());

        background = new JPanel() {
            Image img = new ImageIcon("assets/chooseCharacterBackground.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        background.setLayout(new BorderLayout());

        JPanel characBttn = new JPanel();
        characBttn.setLayout(new BoxLayout(characBttn, BoxLayout.X_AXIS));
        characBttn.setOpaque(false);

        JRadioButton paopao = new JRadioButton("PaoPao");
        JRadioButton terra = new JRadioButton("Terra");
        JRadioButton flammara = new JRadioButton("Flammara");
        JRadioButton adamus = new JRadioButton("Adamus");
        JRadioButton deia = new JRadioButton("Deia");

        ButtonGroup charGroup = new ButtonGroup();
        charGroup.add(paopao);
        charGroup.add(terra);
        charGroup.add(flammara);
        charGroup.add(adamus);
        charGroup.add(deia);

        characBttn.add(paopao);
        characBttn.add(Box.createHorizontalStrut(40));
        characBttn.add(terra);
        characBttn.add(Box.createHorizontalStrut(40));
        characBttn.add(flammara);
        characBttn.add(Box.createHorizontalStrut(40));
        characBttn.add(adamus);
        characBttn.add(Box.createHorizontalStrut(40));
        characBttn.add(deia);

        background.add(characBttn, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);

        backBtn = new JButton("Back");
        nextBtn = new JButton("Next");

        backBtn.setFocusPainted(false);
        nextBtn.setFocusPainted(false);

        buttonPanel.add(backBtn, BorderLayout.WEST);
        buttonPanel.add(nextBtn, BorderLayout.EAST);

        background.add(buttonPanel, BorderLayout.SOUTH);

        add(background, BorderLayout.CENTER);

        nextBtn.addActionListener(e -> {
            if (!flammara.isSelected() &&
                !deia.isSelected() &&
                !adamus.isSelected() &&
                !terra.isSelected()) {

                JOptionPane.showMessageDialog(GamePanel.this, "Please select a character!");
                return;
            }
            startGame();
        });

        backBtn.addActionListener(e -> {
            System.out.println("Back clicked");
        });

        revalidate();
        repaint();
    }

    public void startGame() {
        this.state = GameState.PLAYING;

        removeAll();
        revalidate();
        repaint();

        logicThread = new GameLogicThread(this);
        renderThread = new RenderThread(this);

        logicThread.start();
        renderThread.start();
    }

    public void updateGame() {
        if (player != null) {
            player.update();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (state == GameState.PLAYING) {
            if (key == KeyEvent.VK_ESCAPE) {
                state = GameState.PAUSED;
            }
        } else if (state == GameState.PAUSED && key == KeyEvent.VK_ESCAPE) {
            state = GameState.PLAYING;
        }
    }

    @Override 
    public void keyTyped(KeyEvent e) {}

    @Override 
    public void keyReleased(KeyEvent e) {}

    public GameState getState() { 
        return state; 
    }

    public void setState(GameState state) { 
        this.state = state; 
    }

    public Player getPlayer() { 
        return player; 
    }

    public void setPlayer(Player player) { 
        this.player = player; 
    }

    public AssetManager getAssetManager() { 
        return assetManager; 
    }
}