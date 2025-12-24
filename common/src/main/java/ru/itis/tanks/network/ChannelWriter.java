package ru.itis.tanks.network;

import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.network.util.GameObjectSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import static ru.itis.tanks.network.ChannelMessageType.*;

@RequiredArgsConstructor
public class ChannelWriter {

    private static final byte[] START_BYTES = {(byte) 0xA, (byte) 0xB};

    private final GameObjectSerializer serializer;

    public void write(SocketChannel channel, GameObject obj) throws IOException {
        writeStartBytes(channel);
        writeMessageType(channel, ENTITY_UPDATE);
        ByteBuffer b = serializer.serialize(obj);
        while(b.hasRemaining())
            channel.write(b);
    }

    public void write(SocketChannel channel, GameWorld world) throws IOException {
        writeStartBytes(channel);
        writeMessageType(channel, ALL_MAP);
        for (GameObject obj : world.getAllObjects()) {
            ByteBuffer b = serializer.serialize(obj);
            while(b.hasRemaining())
                channel.write(b);
        }
    }

    public void writePosition(SocketChannel channel, Direction dir, int x, int y) throws IOException {
        writeStartBytes(channel);
        writeMessageType(channel, MOVING_UPDATE);
        ByteBuffer buffer = ByteBuffer.allocate(4 * 4);
        buffer.putInt(x);
        buffer.putInt(y);
        buffer.putInt(dir.getX());
        buffer.putInt(dir.getY());
        buffer.flip();
        while(buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    public void writeCommand(SocketChannel channel, Command cmd) throws IOException {
        channel.write(ByteBuffer.wrap(new byte[]{(byte) cmd.getCode()}));
    }

    private void writeStartBytes(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap(START_BYTES));
    }
    private void writeMessageType(SocketChannel channel, ChannelMessageType type) throws IOException {
        channel.write(ByteBuffer.wrap(new byte[]{(byte) type.getCode()}));
    }

    //todo
    public void writeDirection(SocketChannel channel, Direction direction) {
    }

    public void writeId(SocketChannel channel, int id) throws IOException {
    }

    public void writeGameOverMessage(SocketChannel channel) throws IOException {

    }

    public void writeCommands(SocketChannel channel, Queue<Command> commands) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(commands.size());
        for (Command command : commands) {
            buffer.put((byte) command.getCode());
        }
        buffer.flip();
        while(buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}