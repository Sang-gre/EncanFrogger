package screens.input;

import assets.AssetManager;
import core.GamePanel;
import gameobjects.Player;
import gameobjects.PlayerType;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import ui.CharacterSelectUI;

public class CharacterSelect extends Selection {

    private JRadioButton paopao, terra, flammara, adamus, deia;
    private CharacterSelectUI charUI;

    public CharacterSelect(GamePanel gamePanel, Runnable onBack) {
        super(gamePanel, onBack);
    }

    @Override
    public JPanel createSelectionButtons() {
        paopao = createBtn(0);
        terra = createBtn(1);
        flammara = createBtn(2);
        adamus = createBtn(3);
        deia = createBtn(4);

        JRadioButton[] buttons = { paopao, terra, flammara, adamus, deia };
        ButtonGroup group = new ButtonGroup();
        for (JRadioButton btn : buttons)
            group.add(btn);

        charUI = new CharacterSelectUI(buttons, null);
        return charUI.getPanel();
    }

    private JRadioButton createBtn(int index) {
        JRadioButton btn = new JRadioButton() {
            @Override
            public boolean contains(int x, int y) {
                return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
            }
        };
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);

        btn.addActionListener(e -> charUI.setSelectedIndex(index));

        return btn;
    }

    @Override
    public JPanel createBackground() {
        JPanel background = new JPanel(null) {
            private final Image img = AssetManager.getInstance().getBackground("characterSelect");

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
                selection.setBounds(0, 0, w, h - 100);
                nav.setBounds(0, h - 100, w, 100);
                charUI.layoutAll();
            }
        });

        SwingUtilities.invokeLater(() -> {
            int w = background.getWidth();
            int h = background.getHeight();
            if (w > 0 && h > 0) {
                selection.setBounds(0, 0, w, h - 100);
                nav.setBounds(0, h - 100, w, 100);
                charUI.layoutAll();
            }
        });

        return background;
    }

    protected Player getSelectedPlayer() {
        if (paopao.isSelected())
            return new Player(0, 0, PlayerType.PAOPAO);
        if (terra.isSelected())
            return new Player(0, 0, PlayerType.TERRA);
        if (flammara.isSelected())
            return new Player(0, 0, PlayerType.FLAMARA);
        if (adamus.isSelected())
            return new Player(0, 0, PlayerType.ADAMUS);
        if (deia.isSelected())
            return new Player(0, 0, PlayerType.DEIA);
        return null;
    }

    @Override
    protected void onNext() {
        if (charUI.isCharacterConfirmed()) {
            getGamePanel().showMapSelect(getSelectedPlayer());
        }
    }

    @Override
    public boolean validateSelection() {
        return charUI.isCharacterConfirmed();
    }

    @Override
    protected String getPopupKey() {
        return "characterSelect";
    }
}