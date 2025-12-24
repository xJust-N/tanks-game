package ru.itis.tanks.game.model.impl.tank;

import ru.itis.tanks.game.model.Direction;

public interface TankController {

    void setDirection(Direction direction);

    void enqueueCommand(Command command);

    void processCommands();
}
