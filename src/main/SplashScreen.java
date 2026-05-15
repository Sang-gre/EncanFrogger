package main;

import java.awt.*;
import javax.swing.*;

public class SplashScreen extends JWindow {

    private final JLabel loadingLabel;

    public SplashScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        // Upper panel: image
        ImageIcon rawIcon = new ImageIcon("assets/gameLogo.png");
        Image scaled = rawIcon.getImage().getScaledInstance(600, 350, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
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