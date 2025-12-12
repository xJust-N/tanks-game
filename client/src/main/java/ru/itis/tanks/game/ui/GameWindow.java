package ru.itis.tanks.game.ui;

import ru.itis.tanks.game.ui.renderer.GameRenderer;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private GameRenderer gameRenderer;

    public GameWindow(GameRenderer gameRenderer) throws HeadlessException {
        this.gameRenderer = gameRenderer;
        initializeWindow();
    }

    private void initializeWindow() {
        setTitle("Tanks Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        add(gameRenderer);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateGameWorld() {
        gameRenderer.render();
    }
}