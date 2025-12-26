package ru.itis.tanks.game.ui.panels;

import ru.itis.tanks.game.SocketGameClient;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {

    private final JButton backButton;

    private final SocketGameClient client;

    public GameOverPanel(SocketGameClient client) {
        this.backButton = new JButton("Back");
        this.client = client;
        init();
    }

    private void init() {
        setVisible(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);
        Dimension d = new Dimension(400, 400);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        JLabel gameOverLabel = new JLabel("Game over");
        gameOverLabel.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            client.stopAndCleanup();
        });
        int height = 10;
        add(Box.createVerticalStrut(height * 10));
        add(gameOverLabel);
        add(Box.createVerticalStrut(height));
        add(backButton);
        add(Box.createVerticalStrut(height));
    }
}