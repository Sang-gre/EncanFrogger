package core;

import characters.Adamus;
import characters.Deia;
import characters.Flamara;
import characters.Paopao;
import characters.Terra;
import gameobjects.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CharacterSelect extends Selection {

    private JRadioButton paopao, terra, flammara, adamus, deia;

    public CharacterSelect(GamePanel gamePanel, Runnable onBack) {
        super(gamePanel, onBack);
    }

    @Override
    public JPanel createSelectionButtons() {

        int W = 155;
        int H = 290;
        int gap = 5;

        paopao = createBtn("assets/characterCards/paopaoCard.png",
                           "assets/characterInfoCard/paopaoInfoCard.png", W, H);

        terra = createBtn("assets/characterCards/terraCard.png",
                          "assets/characterInfoCard/terraInfoCard.png", W, H);

        flammara = createBtn("assets/characterCards/flammaraCard.png",
                             "assets/characterInfoCard/flammaraInfoCard.png", W, H);

        adamus = createBtn("assets/characterCards/adamusCard.png",
                           "assets/characterInfoCard/adamusInfoCard.png", W, H);

        deia = createBtn("assets/characterCards/deiaCard.png",
                         "assets/characterInfoCard/deiaInfoCard.png", W, H);

        JRadioButton[] buttons = {paopao, terra, flammara, adamus, deia};

        ButtonGroup group = new ButtonGroup();
        for (JRadioButton btn : buttons) {
            group.add(btn);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        panel.add(paopao);
        panel.add(Box.createHorizontalStrut(gap));
        panel.add(terra);
        panel.add(Box.createHorizontalStrut(gap));
        panel.add(flammara);
        panel.add(Box.createHorizontalStrut(gap));
        panel.add(adamus);
        panel.add(Box.createHorizontalStrut(gap));
        panel.add(deia);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setOpaque(false);
        centerWrapper.add(panel);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(115, 0, 0, 0));

        return centerWrapper;
    }

    private JRadioButton createBtn(String normal, String selected, int w, int h) {
        JRadioButton btn = new JRadioButton();
        btn.setIcon(scaleIcon(normal, w, h));
        btn.setSelectedIcon(scaleIcon(selected, w, h));

        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);

        Dimension size = new Dimension(w, h);
        btn.setPreferredSize(size);
        btn.setMinimumSize(size);
        btn.setMaximumSize(size);

        return btn;
    }

    private ImageIcon scaleIcon(String path, int w, int h) {
        Image img = new ImageIcon(path).getImage()
                .getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    @Override
    public JPanel createBackground() {

        JPanel background = new JPanel(null) {
            private final Image img = new ImageIcon("assets/mapSelectBackground.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JPanel selection = createSelectionButtons();
        JPanel nav = super.createNavButtons(); 

        background.add(selection);
        background.add(nav);

        background.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int w = background.getWidth();
                int h = background.getHeight();

                selection.setBounds(0, 0, w, h - 50);

                nav.setBounds(0, h - 100, w, 100);
            }
        });

        return background;
    }

    protected Player getSelectedPlayer() {
        if (paopao.isSelected()) return new Paopao(0, 0);
        if (terra.isSelected()) return new Terra(0, 0);
        if (flammara.isSelected()) return new Flamara(0, 0);
        if (adamus.isSelected()) return new Adamus(0, 0);
        if (deia.isSelected()) return new Deia(0, 0);
        return null;
    }

    @Override
    protected void onNext() {
        Player selected = getSelectedPlayer();
        getGamePanel().showMapSelect(selected);
    }

    @Override
    public boolean validateSelection() {
        return paopao.isSelected() || terra.isSelected() ||
               flammara.isSelected() || adamus.isSelected() ||
               deia.isSelected();
    }
}