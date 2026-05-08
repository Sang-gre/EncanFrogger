package ui;

import assets.AssetManager;
import main.GameLauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InstructionsPanel extends JPanel {

    private GameLauncher parent;

    // Array of instruction pages
    private Image[] pages;

    // Current page index
    private int currentPage = 0;

    public InstructionsPanel(GameLauncher parent) {
        this.parent = parent;
        setFocusable(true);
        loadPages();
        setupKeys();
    }
        private void setupKeys() {

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    // NEXT PAGE
                    case KeyEvent.VK_RIGHT:

                        if (currentPage < pages.length - 1) {

                            currentPage++;
                            repaint();

                        } else {

                            // Last page -> back to menu
                            currentPage = 0;
                            parent.showMainMenu();
                        }

                        break;

                        // PREVIOUS PAGE
                    case KeyEvent.VK_LEFT:

                        if (currentPage > 0) {

                            currentPage--;
                            repaint();
                        }

                        break;

                    // EXIT INSTRUCTIONS
                    case KeyEvent.VK_ESCAPE:

                        currentPage = 0;
                        parent.showMainMenu();

                        break;
                }
            }
        });
    
    }

    private void loadPages() {

    pages = new Image[] {

            AssetManager.getInstance().getBackground("instruction1"),
            AssetManager.getInstance().getBackground("instruction2"),
            AssetManager.getInstance().getBackground("instruction3"),
            AssetManager.getInstance().getBackground("instruction4"),
            AssetManager.getInstance().getBackground("instruction5")
    };
}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(pages[currentPage] != null) {
            g.drawImage(
                    pages[currentPage],
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
            );
        }

    
    }
}