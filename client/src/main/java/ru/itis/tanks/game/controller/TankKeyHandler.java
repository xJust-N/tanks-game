package ru.itis.tanks.game.controller;

import lombok.AllArgsConstructor;
import ru.itis.tanks.game.model.Direction;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@AllArgsConstructor
public class TankKeyHandler extends KeyAdapter implements KeyListener {

    private final TankController tankController;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> {
                tankController.setDirection(Direction.UP);
                tankController.startMoving();
            }
            case KeyEvent.VK_S -> {
                tankController.setDirection(Direction.DOWN);
                tankController.startMoving();
            }
            case KeyEvent.VK_A -> {
                tankController.setDirection(Direction.LEFT);
                tankController.startMoving();
            }
            case KeyEvent.VK_D -> {
                tankController.setDirection(Direction.RIGHT);
                tankController.startMoving();
            }
            case KeyEvent.VK_F -> tankController.shoot();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                tankController.stopMoving();
                break;
        }
    }
}
