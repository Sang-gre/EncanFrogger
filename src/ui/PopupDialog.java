package ui;

import java.awt.*;
import javax.swing.*;
import managers.AssetManager;

public class PopupDialog {

    public static void show(Component parent, Image popupImage) {
        Window ancestor = SwingUtilities.getWindowAncestor(parent);
        Image okImage = AssetManager.getInstance().getButton("ok2");

        int popW = (int) (ancestor.getWidth() * 0.55);
        popW = Math.max(400, Math.min(popW, 800));
        int popH = (int) (popW * popupImage.getHeight(null) / (double) popupImage.getWidth(null));

        int btnW = (int) (popW * 0.25);
        int btnH = (int) (btnW * okImage.getHeight(null) / (double) okImage.getWidth(null));

        JDialog dialog = new JDialog((Frame) ancestor, true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image empty = toolkit.createImage("");
        Cursor blankCursor = toolkit.createCustomCursor(empty, new Point(0, 0), "blank");
        dialog.setCursor(blankCursor);

        JPanel content = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(popupImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        content.setOpaque(false);
        content.setCursor(blankCursor);

        JButton okBtn = new JButton(new ImageIcon(
                okImage.getScaledInstance(btnW, btnH, Image.SCALE_SMOOTH)));
        okBtn.setBorderPainted(false);
        okBtn.setContentAreaFilled(false);
        okBtn.setFocusPainted(false);
        okBtn.addActionListener(e -> dialog.dispose());
        okBtn.setBounds((popW - btnW) / 2, (int) (popH * 0.55), btnW, btnH);
        content.add(okBtn);

        content.setPreferredSize(new Dimension(popW, popH));
        dialog.setContentPane(content);
        dialog.pack();

        Image customCursor = AssetManager.getInstance().getCustomCursor();
        CursorGlassPane dialogGlassPane = new CursorGlassPane(customCursor, content);
        dialog.setGlassPane(dialogGlassPane);
        dialogGlassPane.setVisible(true);

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}