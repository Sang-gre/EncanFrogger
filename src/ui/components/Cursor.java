package ui.components;
import java.awt.*;
import javax.swing.*;

public class Cursor {

    public static void setCustomCursor(JComponent panel, String imagePath) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image empty = toolkit.createImage("");
        panel.setCursor(toolkit.createCustomCursor(empty, new Point(0, 0), "blank cursor"));

        Image customCursor = new ImageIcon(imagePath).getImage();
        panel.putClientProperty("customCursorPng", customCursor);
    }
}