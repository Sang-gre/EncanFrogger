package ui;

import assets.AssetManager;
import java.awt.*;
import javax.swing.*;

public class PopupDialog {

    public static void show(Component parent, Image popupImage) {

        Window ancestor = SwingUtilities.getWindowAncestor(parent);

        Image okImage = AssetManager.getInstance().getButton("ok2");

        int popW = (int) (ancestor.getWidth() * 0.55);
        popW = Math.max(400, Math.min(popW, 800));

        int popH = (int) (popW * popupImage.getHeight(null) / (double) popupImage.getWidth(null));
        int btnW = (int) (popW * 0.25);
        int btnH = (int) (btnW * okImage.getHeight(null) / (double) okImage.getWidth(null));

        final int finalPopW = popW;
        final int finalPopH = popH;

        JDialog dialog = new JDialog((Frame) ancestor, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Cursor blankCursor = toolkit.createCustomCursor(
                toolkit.createImage(""),
                new Point(0, 0),
                "blank"
        );

        dialog.setCursor(blankCursor);

        JPanel content = new JPanel(null) {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());

                int popupX = (getWidth() - finalPopW) / 2;
                int popupY = (getHeight() - finalPopH) / 2;

                g2.drawImage(popupImage, popupX, popupY, finalPopW, finalPopH, this);

                g2.dispose();
            }
        };

        content.setOpaque(false);
        content.setCursor(blankCursor);

        JButton okBtn = new JButton(
                new ImageIcon(
                        okImage.getScaledInstance(btnW, btnH, Image.SCALE_SMOOTH)
                )
        );

        okBtn.setBorderPainted(false);
        okBtn.setContentAreaFilled(false);
        okBtn.setFocusPainted(false);
        okBtn.setCursor(blankCursor);
        okBtn.addActionListener(e -> dialog.dispose());

        int popupX = (ancestor.getWidth() - popW) / 2;
        int popupY = (ancestor.getHeight() - popH) / 2;

        okBtn.setBounds(
                popupX + (popW - btnW) / 2,
                popupY + (int) (popH * 0.55),
                btnW,
                btnH
        );

        content.add(okBtn);

        dialog.setContentPane(content);
        dialog.setBounds(ancestor.getBounds());

        Image customCursor = AssetManager.getInstance().getCustomCursor();

        CursorGlassPane glass = new CursorGlassPane(customCursor, content);

        dialog.setGlassPane(glass);
        glass.setVisible(true);

        dialog.setVisible(true);
    }
}