package ru.itis.tanks.game.controller;

import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.impl.tank.TankController;
import ru.itis.tanks.network.ChannelWriter;

import java.io.IO;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@RequiredArgsConstructor
public class ClientTankController implements TankController {

    private final BlockingQueue<Command> commands = new LinkedBlockingQueue<>();

    private final SocketChannel channel;

    private final ChannelWriter writer;

    @Override
    public void setDirection(Direction direction) {
        switch (direction) {
            case UP -> enqueueCommand(Command.UP);
            case DOWN -> enqueueCommand(Command.DOWN);
            case LEFT -> enqueueCommand(Command.LEFT);
            case RIGHT -> enqueueCommand(Command.RIGHT);
        }
    }

    @Override
    public void enqueueCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void processCommands() {
        try {
            Command command = commands.take();
            writer.writeCommand(channel, command);
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean hasCommands() {
        return !commands.isEmpty();
    }
}
