package ru.itis.tanks.game.controller;

import lombok.AllArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.impl.tank.TankController;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TankKeyHandler extends KeyAdapter implements KeyListener {

    protected final TankController tankController;

    private volatile Direction lastDirection;

    private volatile boolean moving = false;

    public TankKeyHandler(TankController tankController) {
        this.tankController = tankController;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> {
                if(lastDirection !=  Direction.UP) {
                    tankController.setDirection(Direction.UP);
                    lastDirection = Direction.UP;
                }
                if(!moving){
                    moving = true;
                    tankController.enqueueCommand(Command.START_MOVING);
                }
            }
            case KeyEvent.VK_S -> {
                if(lastDirection !=  Direction.DOWN) {
                    tankController.setDirection(Direction.DOWN);
                    lastDirection = Direction.DOWN;
                }
                if(!moving){
                    moving = true;
                    tankController.enqueueCommand(Command.START_MOVING);
                }
            }
            case KeyEvent.VK_A -> {
                if(lastDirection !=  Direction.LEFT) {
                    tankController.setDirection(Direction.LEFT);
                    lastDirection = Direction.LEFT;
                }
                if(!moving){
                    moving = true;
                    tankController.enqueueCommand(Command.START_MOVING);
                }
            }
            case KeyEvent.VK_D -> {
                if(lastDirection !=  Direction.RIGHT) {
                    tankController.setDirection(Direction.RIGHT);
                    lastDirection = Direction.RIGHT;
                }
                if(!moving){
                    moving = true;
                    tankController.enqueueCommand(Command.START_MOVING);
                }
            }
            case KeyEvent.VK_F -> tankController.enqueueCommand(Command.SHOOT);
        }
    }

    private boolean isMovementKeyPressed(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_W ||
                e.getKeyCode() == KeyEvent.VK_S ||
                e.getKeyCode() == KeyEvent.VK_A ||
                e.getKeyCode() == KeyEvent.VK_D;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isMovementKeyPressed(e) && moving) {
            tankController.enqueueCommand(Command.STOP_MOVING);
            moving = false;
        }
    }
}
