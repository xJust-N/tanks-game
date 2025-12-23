package ru.itis.tanks.game.controller;

import lombok.AllArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.impl.tank.TankController;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@AllArgsConstructor
public class TankKeyHandler extends KeyAdapter implements KeyListener {

    protected final TankController tankController;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> {
                tankController.setDirection(Direction.UP);
                tankController.enqueueCommand(Command.START_MOVING);
            }
            case KeyEvent.VK_S -> {
                tankController.setDirection(Direction.DOWN);
                tankController.enqueueCommand(Command.START_MOVING);
            }
            case KeyEvent.VK_A -> {
                tankController.setDirection(Direction.LEFT);
                tankController.enqueueCommand(Command.START_MOVING);
            }
            case KeyEvent.VK_D -> {
                tankController.setDirection(Direction.RIGHT);
                tankController.enqueueCommand(Command.START_MOVING);
            }
            case KeyEvent.VK_F -> tankController.enqueueCommand(Command.SHOOT);
        }
    }

    //TODO танк не перестает останавливаться если какая то кнопка отпущена,
    // движется в направлении последней нажатой кнопки
    private boolean isMovementKeyPressed(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_W || 
               e.getKeyCode() == KeyEvent.VK_S || 
               e.getKeyCode() == KeyEvent.VK_A || 
               e.getKeyCode() == KeyEvent.VK_D;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (isMovementKeyPressed(e)) {
            tankController.enqueueCommand(Command.STOP_MOVING);
        }
    }
}
