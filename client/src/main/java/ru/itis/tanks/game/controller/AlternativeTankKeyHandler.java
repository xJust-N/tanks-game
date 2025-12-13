package ru.itis.tanks.game.controller;

import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.impl.tank.TankController;

import java.awt.event.KeyEvent;

public class AlternativeTankKeyHandler extends TankKeyHandler {

    public AlternativeTankKeyHandler(TankController tankController) {
        super(tankController);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                tankController.setDirection(Direction.UP);
                tankController.enqueueCommand(Command.START_MOVING);
            }
            case KeyEvent.VK_DOWN -> {
                tankController.setDirection(Direction.DOWN);
                tankController.enqueueCommand(Command.START_MOVING);
            }
            case KeyEvent.VK_LEFT -> {
                tankController.setDirection(Direction.LEFT);
                tankController.enqueueCommand(Command.START_MOVING);
            }
            case KeyEvent.VK_RIGHT -> {
                tankController.setDirection(Direction.RIGHT);
                tankController.enqueueCommand(Command.START_MOVING);
            }
            case KeyEvent.VK_CONTROL -> tankController.enqueueCommand(Command.SHOOT);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                tankController.enqueueCommand(Command.STOP_MOVING);
                break;
        }
    }
}