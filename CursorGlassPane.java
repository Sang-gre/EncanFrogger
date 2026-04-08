import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CursorGlassPane extends JComponent {
    private Image customCursor;
    private int x = 0;
    private int y = 0;
    private JPanel mainPanel; 

    public CursorGlassPane(Image customCursor, JPanel mainPanel) {
        this.customCursor = customCursor;
        this.mainPanel = mainPanel;

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                repaint();
                forwardEvent(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                repaint();
                forwardEvent(e);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
                public void mousePressed(MouseEvent e) { 
                    forwardEvent(e); 
                }
            @Override
            public void mouseReleased(MouseEvent e) { 
                forwardEvent(e); 
            }
            @Override
            public void mouseClicked(MouseEvent e) { 
                forwardEvent(e); 
            }
        });
    }

    private void forwardEvent(MouseEvent e) {
        Point panelPoint = SwingUtilities.convertPoint(this, e.getPoint(), mainPanel);
        Component target = SwingUtilities.getDeepestComponentAt(mainPanel, panelPoint.x, panelPoint.y);
        if (target != null) {
            target.dispatchEvent(SwingUtilities.convertMouseEvent(this, e, target));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(customCursor != null){
            g.drawImage(customCursor, x, y, 32, 32, this);
        }
    }
}