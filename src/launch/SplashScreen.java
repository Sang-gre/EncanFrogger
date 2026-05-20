package launch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class SplashScreen extends JWindow {

    private final JLabel loadingLabel;

    private static String getBasePath() {
        try {
            File codeSource = new File(
                    SplashScreen.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI());
            if (codeSource.getName().endsWith(".jar")) {
                return codeSource.getParentFile()
                        .getParentFile()
                        .getParentFile()
                        .getAbsolutePath() + File.separator;
            }
            return codeSource.getParentFile()
                    .getParentFile()
                    .getAbsolutePath() + File.separator;
        } catch (Exception e) {
            return "";
        }
    }

    private static final String BASE = getBasePath();

    public SplashScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        // Upper panel: image
        ImageIcon rawIcon = new ImageIcon(BASE + "EncanFrogger/assets/icons/gameLogo.png");
        Image scaled = rawIcon.getImage().getScaledInstance(600, 350, Image.SCALE_SMOOTH);
        JLabel imageLabel;
        imageLabel = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
        panel.add(imageLabel, BorderLayout.CENTER);

        // Bottom panel: loading + text
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 5));
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 15, 20));

        // text
        loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bottomPanel.add(loadingLabel, BorderLayout.NORTH);

        // loading
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(new Color(255, 200, 0));
        progressBar.setBackground(Color.DARK_GRAY);
        progressBar.setBorderPainted(false);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().add(panel);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setLoadingText(String text) {
        SwingUtilities.invokeLater(() -> loadingLabel.setText(text));
    }

    public void dismiss() {
        setVisible(false);
        dispose();
    }
}