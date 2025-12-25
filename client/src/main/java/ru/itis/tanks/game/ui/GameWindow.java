package ru.itis.tanks.game.ui;

import ru.itis.tanks.game.ui.panels.GameWorldRenderer;

import javax.swing.*;
import java.awt.HeadlessException;

public class GameWindow extends JFrame {

    private JPanel currentPanel;

    public GameWindow(JPanel currentPanel) throws HeadlessException {
        this.currentPanel = currentPanel;
        init();
    }

    private void init() {
        setTitle("Tanks Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(currentPanel);
        pack();
        setLocationRelativeTo(null);
        setFocusable(true);
        requestFocus();
        setVisible(true);
    }

    public void update() {
        currentPanel.repaint();
        if(currentPanel instanceof GameWorldRenderer renderer)
            renderer.refresh();
    }

    public void changePanel(JPanel panel) {
        currentPanel = panel;
        setContentPane(currentPanel);
        setPreferredSize(panel.getPreferredSize());
        revalidate();
        repaint();
        pack();
    }
    
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}