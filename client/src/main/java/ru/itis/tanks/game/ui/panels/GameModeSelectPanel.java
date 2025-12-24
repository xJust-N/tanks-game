package ru.itis.tanks.game.ui.panels;

import ru.itis.tanks.game.GameModeSelectListener;
import ru.itis.tanks.game.ui.model.GameMode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameModeSelectPanel extends JPanel {

    private final JButton localMultiplayerButton;

    private final JButton joinButton;

    private final List<GameModeSelectListener> listeners;

    public GameModeSelectPanel() {
        localMultiplayerButton = new JButton("Local Multiplayer");
        joinButton = new JButton("Join online game");
        listeners = new ArrayList<>();
        init();
    }

    public GameModeSelectPanel(GameModeSelectListener l) {
        this();
        listeners.add(l);
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
        JLabel label = new JLabel("Game Mode");
        label.setAlignmentX(CENTER_ALIGNMENT);
        localMultiplayerButton.setAlignmentX(CENTER_ALIGNMENT);
        localMultiplayerButton.addActionListener(_ -> {
            for(GameModeSelectListener listener : listeners){
                listener.onGameModeSelected(GameMode.LOCAL_MULTIPLAYER);
            }
        });
        joinButton.setAlignmentX(CENTER_ALIGNMENT);
        joinButton.addActionListener(_ -> {
            for(GameModeSelectListener listener : listeners){
                listener.onGameModeSelected(GameMode.JOIN_GAME);
            }
        });
        int height = 10;
        add(Box.createVerticalStrut(10 * height));
        add(label);
        add(Box.createVerticalStrut(height));
        add(localMultiplayerButton);
        add(Box.createVerticalStrut(height));
        add(joinButton);
        add(Box.createVerticalStrut(height));
    }

    public void addListener(GameModeSelectListener l) {
        listeners.add(l);
    }
}
