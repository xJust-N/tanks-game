package ru.itis.tanks.game.controller;

import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.impl.tank.TankController;
import ru.itis.tanks.network.ChannelWriter;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

@RequiredArgsConstructor
public class ClientTankController implements TankController {

    private final Queue<Command> commands = new ArrayDeque<>();

    private final SocketChannel channel;

    private final ChannelWriter writer;

    @Override
    public void setDirection(Direction direction) {
        writer.writeDirection(channel, direction);
    }

    @Override
    public void enqueueCommand(Command command) {
            commands.add(command);
    }

    @Override
    public void processCommands() {
        while (!commands.isEmpty()) {
            try {
                writer.writeCommands(channel, commands);
            } catch (IOException e) {
                //todo
            }
        }
    }
}
