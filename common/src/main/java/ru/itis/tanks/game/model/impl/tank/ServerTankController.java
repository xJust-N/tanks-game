package ru.itis.tanks.game.model.impl.tank;

import lombok.Getter;
import ru.itis.tanks.game.model.Direction;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class ServerTankController implements TankController {

    private final Tank tank;

    private final Queue<Command> commandQueue = new ArrayDeque<>();

    private Direction direction;

    public ServerTankController(Tank tank, Direction direction) {
        this.tank = tank;
        this.direction = direction;
        this.tank.setDirection(direction);
        this.tank.setMoving(false);
    }

    public ServerTankController(Tank tank) {
        this(tank, tank.getDirection());
    }

    public void setDirection(Direction direction) {
        switch (direction) {
            case UP -> enqueueCommand(Command.UP);
            case DOWN -> enqueueCommand(Command.DOWN);
            case LEFT -> enqueueCommand(Command.LEFT);
            case RIGHT -> enqueueCommand(Command.RIGHT);
        }
    }

    public void enqueueCommand(Command command) {
        commandQueue.offer(command);
    }

    @Override
    public void processCommands() {
        Command command;
        while ((command = commandQueue.poll()) != null) {
            switch (command) {
                case Command.START_MOVING -> tank.setMoving(true);
                case Command.STOP_MOVING -> tank.setMoving(false);
                case Command.SHOOT -> tank.shoot();
                case UP -> tank.setDirection(Direction.UP);
                case DOWN -> tank.setDirection(Direction.DOWN);
                case LEFT -> tank.setDirection(Direction.LEFT);
                case RIGHT -> tank.setDirection(Direction.RIGHT);
            }
        }
    }

    @Override
    public boolean hasCommands() {
        return !commandQueue.isEmpty();
    }
}