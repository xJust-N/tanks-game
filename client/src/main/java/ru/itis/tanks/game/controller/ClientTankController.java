package ru.itis.tanks.game.controller;

import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.impl.tank.TankController;
import ru.itis.tanks.network.ChannelWriter;

import java.nio.channels.SocketChannel;

@RequiredArgsConstructor
public class ClientTankController implements TankController {

    private final SocketChannel channel;

    private final ChannelWriter writer;

    @Override
    public void setDirection(Direction direction) {
        writer.writePosition();
    }

    @Override
    public void enqueueCommand(Command command) {

    }
}
