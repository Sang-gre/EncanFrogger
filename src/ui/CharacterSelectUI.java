package ui;

import assets.AssetManager;
import gameobjects.PlayerType;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class CharacterSelectUI {

    private JPanel mainPanel;
    private JLabel detailCardLabel;
    private JLabel detailInfoLabel;
    private JLabel selectBtn;
    private Rectangle[] cardBounds = new Rectangle[5];

    private JRadioButton[] buttons;
    private int selectedIndex = -1;     // no card is selected
    private boolean characterConfirmed = false;

    private final Runnable onSelectConfirmed;   // notifies CharacterSelect

    // Order must match button index
    private static final PlayerType[] PLAYER_TYPES = {
            PlayerType.PAOPAO,
            PlayerType.TERRA,
            PlayerType.FLAMARA,
            PlayerType.ADAMUS,
            PlayerType.DEIA
    };

    public CharacterSelectUI(JRadioButton[] buttons, Runnable onSelectConfirmed) {
        this.buttons = buttons;
        this.onSelectConfirmed = onSelectConfirmed;
        buildPanel();
    }

    // Initializes all components and sets up event listeners
    private void buildPanel() {
        detailCardLabel = new JLabel();
        detailCardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailCardLabel.setVerticalAlignment(SwingConstants.CENTER);
        detailCardLabel.setVisible(false);

        detailInfoLabel = new JLabel();
        detailInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailInfoLabel.setVerticalAlignment(SwingConstants.CENTER);
        detailInfoLabel.setVisible(false);

        selectBtn = new JLabel();
        selectBtn.setHorizontalAlignment(SwingConstants.CENTER);
        selectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        selectBtn.setVisible(false);
        selectBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                characterConfirmed = true;
                detailCardLabel.setVisible(false);
                detailInfoLabel.setVisible(false);
                selectBtn.setVisible(false);
                redrawConfirmed();
                if (onSelectConfirmed != null)
                    onSelectConfirmed.run();
            }
        });

        mainPanel = new JPanel(null);
        mainPanel.setOpaque(false);

        for (JRadioButton btn : buttons)
            mainPanel.add(btn);
        mainPanel.add(detailInfoLabel);
        mainPanel.add(detailCardLabel);
        mainPanel.add(selectBtn);

        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedIndex >= 0) {
                    Point p = e.getPoint();
                    boolean hitCard = detailCardLabel.isVisible() &&
                            detailCardLabel.getBounds().contains(p);
                    boolean hitInfo = detailInfoLabel.isVisible() &&
                            detailInfoLabel.getBounds().contains(p);
                    boolean hitBtn = selectBtn.isVisible() &&
                            selectBtn.getBounds().contains(p);
                    if (!hitCard && !hitInfo && !hitBtn) {
                        dismissDetail();
                    }
                }
            }
        });

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutAll();
            }
        });
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public boolean isCharacterConfirmed() {
        return characterConfirmed;
    }

    public void setSelectedIndex(int index) {
        characterConfirmed = false;
        selectedIndex = index;
        layoutAll();
    }

    public void resetSelection() {
        selectedIndex = -1;
        characterConfirmed = false;
        detailCardLabel.setVisible(false);
        detailInfoLabel.setVisible(false);
        selectBtn.setVisible(false);
        layoutAll();
    }

    // hides overlay if character is selected, otherwise clears selection
    private void dismissDetail() {
        if (characterConfirmed) {
            detailCardLabel.setVisible(false);
            detailInfoLabel.setVisible(false);
            selectBtn.setVisible(false);
            mainPanel.revalidate();
            mainPanel.repaint();
            return;
        }
        selectedIndex = -1;
        detailCardLabel.setVisible(false);
        detailInfoLabel.setVisible(false);
        selectBtn.setVisible(false);
        ButtonGroup group = new ButtonGroup();
        for (JRadioButton b : buttons)
            group.add(b);
        group.clearSelection();
        layoutAll();
    }

    // redraws cards, puts emphasis on selected card
    private void redrawConfirmed() {
        if (mainPanel == null)
            return;
        int W = mainPanel.getWidth();
        int H = mainPanel.getHeight();
        if (W == 0 || H == 0)
            return;

        int cardCount = 5;
        int gap = 12;
        double ratio = 290.0 / 155.0;
        int cardW = (W - gap * (cardCount - 1) - 40) / cardCount;
        int cardH = (int) (cardW * ratio);
        int maxCardH = (int) (H * 0.70);
        if (cardH > maxCardH) {
            cardH = maxCardH;
            cardW = (int) (cardH / ratio);
        }
        for (int i = 0; i < buttons.length; i++) {
            setCardImage(buttons[i], PLAYER_TYPES[i], cardW, cardH, i != selectedIndex);
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // master layout method
    public void layoutAll() {
        if (mainPanel == null)
            return;
        int W = mainPanel.getWidth();
        int H = mainPanel.getHeight();
        if (W == 0 || H == 0)
            return;

        int cardCount = 5;
        int gap = 12;
        double ratio = 290.0 / 155.0;

        // Calculate card width based on horizontal space
        int cardW = (W - gap * (cardCount - 1) - 40) / cardCount;
        int cardH = (int) (cardW * ratio);

        // Max card height to 70% of panel to prevent overlap
        int maxCardH = (int) (H * 0.70);
        if (cardH > maxCardH) {
            cardH = maxCardH;
            cardW = (int) (cardH / ratio);
        }

        // Center card row horizontally
        int totalWidth = cardW * cardCount + gap * (cardCount - 1);
        int startX = (W - totalWidth) / 2;
        
        // Push cards 30% down to sit below title
        int cardY = (int) (H * 0.30);

        // Positions cards and applies dim effect to unselected cards
        for (int i = 0; i < cardCount; i++) {
            int x = startX + i * (cardW + gap);
            cardBounds[i] = new Rectangle(x, cardY, cardW, cardH);
            buttons[i].setBounds(cardBounds[i]);
            boolean dimmed = selectedIndex >= 0 && i != selectedIndex;
            setCardImage(buttons[i], PLAYER_TYPES[i], cardW, cardH, dimmed);
            buttons[i].setVisible(true);
        }

        // If no selection or already confirmed, hide detail overlay
        if (selectedIndex < 0 || characterConfirmed) {
            detailCardLabel.setVisible(false);
            detailInfoLabel.setVisible(false);
            selectBtn.setVisible(false);
        } else {

            //Calculate select button dimensions based on image aspect ratio
            Rectangle orig = cardBounds[selectedIndex];

            Image selectImg = new ImageIcon("assets/Buttons/selectButton.png").getImage();
            int selectOrigW = selectImg.getWidth(null);
            int selectOrigH = selectImg.getHeight(null);
            int btnW = (int) (W * 0.13);
            int btnH = btnW;
            if (selectOrigW > 0 && selectOrigH > 0) {
                double selectRatio = (double) selectOrigW / selectOrigH;
                btnH = (int) (btnW / selectRatio);
            }

            // Calculate info card dimensions
            int infoH = orig.height - btnH - gap;
            Image rawInfo = AssetManager.getInfoCard(PLAYER_TYPES[selectedIndex]);
            int infoOrigW = rawInfo.getWidth(null);
            int infoOrigH = rawInfo.getHeight(null);
            int infoW = orig.width;
            if (infoOrigW > 0 && infoOrigH > 0) {
                double infoRatio = (double) infoOrigW / infoOrigH;
                infoW = (int) (infoH * infoRatio);
            }

            // Decide whether info is left or right
            int cardCenterX = orig.x + orig.width / 2;
            boolean infoOnLeft = cardCenterX > W / 2;
            int infoX, infoY;
            infoY = orig.y;

            if (infoOnLeft) {
                infoX = orig.x - infoW - gap;
                if (infoX < 10) {
                    infoX = 10;
                    infoW = orig.x - gap - 10;
                    if (infoOrigW > 0 && infoOrigH > 0) {
                        double infoRatio = (double) infoOrigH / infoOrigW;
                        infoH = (int) (infoW * infoRatio);
                    }
                }
            } else {
                infoX = orig.x + orig.width + gap;
                if (infoX + infoW > W - 10) {
                    infoW = W - infoX - 10;
                    if (infoOrigW > 0 && infoOrigH > 0) {
                        double infoRatio = (double) infoOrigH / infoOrigW;
                        infoH = (int) (infoW * infoRatio);
                    }
                }
            }

            // Place selected card
            Image cardImg = AssetManager.getCharacterCard(PLAYER_TYPES[selectedIndex])
                    .getScaledInstance(orig.width, orig.height, Image.SCALE_SMOOTH);
            detailCardLabel.setIcon(new ImageIcon(cardImg));
            detailCardLabel.setBounds(orig.x, orig.y, orig.width, orig.height);
            detailCardLabel.setVisible(true);

            // Place info
            Image infoImg = rawInfo.getScaledInstance(infoW, infoH, Image.SCALE_SMOOTH);
            detailInfoLabel.setIcon(new ImageIcon(infoImg));
            detailInfoLabel.setBounds(infoX, infoY, infoW, infoH);
            detailInfoLabel.setVisible(true);

            // Place select button
            Image scaledSelect = selectImg.getScaledInstance(btnW, btnH, Image.SCALE_SMOOTH);
            selectBtn.setIcon(new ImageIcon(scaledSelect));
            int btnX = infoX + infoW / 2 - btnW / 2;
            int btnY = infoY + infoH + gap;
            selectBtn.setBounds(btnX, btnY, btnW, btnH);
            selectBtn.setVisible(true);

            // Set z-order so overlays are above cards
            mainPanel.setComponentZOrder(selectBtn, 0);
            mainPanel.setComponentZOrder(detailInfoLabel, 1);
            mainPanel.setComponentZOrder(detailCardLabel, 2);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // draws cards
    private void setCardImage(JRadioButton btn, PlayerType type, int w, int h, boolean dimmed) {
        Image raw = AssetManager.getCharacterCard(type);
        if (raw == null)
            return;

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2.drawImage(raw, 0, 0, w, h, null);

        if (dimmed) {
            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g3 = scaled.createGraphics();
            g3.drawImage(raw, 0, 0, w, h, null);
            g3.dispose();
            for (int px = 0; px < w; px++) {
                for (int py = 0; py < h; py++) {
                    int argb = scaled.getRGB(px, py);
                    int a = (argb >> 24) & 0xff;
                    if (a > 0) {
                        int r = (int) (((argb >> 16) & 0xff) * 0.40);
                        int g = (int) (((argb >> 8) & 0xff) * 0.40);
                        int b = (int) ((argb & 0xff) * 0.40);
                        bi.setRGB(px, py, (a << 24) | (r << 16) | (g << 8) | b);
                    }
                }
            }
        }

        g2.dispose();
        btn.setIcon(new ImageIcon(bi));
    }
}