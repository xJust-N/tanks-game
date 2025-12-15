package ru.itis.tanks.game.model.impl.tank;

import lombok.Getter;
import ru.itis.tanks.game.model.Direction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class TankController {

    private final Tank tank;

    //чтобы не было состояния гонки с обновлением мира
    private final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<>();

    @Getter
    private Direction direction;


    public TankController(Tank tank, Direction direction) {
        this.tank = tank;
        this.direction = direction;
        this.tank.setDirection(direction);
        this.tank.setMoving(false);
    }

    public TankController(Tank tank) {
        this(tank, tank.getDirection());
    }

    public void setDirection(Direction direction) {
            if (direction != null) {
                this.direction = direction;
                tank.setDirection(direction);
            }
    }

    public void enqueueCommand(Command command) {
        commandQueue.offer(command);
    }

    public void processCommands() {
        Command command;
        while ((command = commandQueue.poll()) != null) {
            switch (command) {
                case Command.START_MOVING -> tank.setMoving(true);
                case Command.STOP_MOVING -> tank.setMoving(false);
                case Command.SHOOT -> tank.shoot();
            }
        }
    }
}