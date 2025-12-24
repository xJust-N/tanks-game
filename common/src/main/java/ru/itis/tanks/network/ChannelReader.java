package ru.itis.tanks.network;

import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.network.util.GameObjectDeserializer;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

@RequiredArgsConstructor
public class ChannelReader {

    private static final int MAX_MESSAGE_SIZE = 10 * 1024 * 1024;

    private static final byte[] START_BYTES = {(byte) 0xA, (byte) 0xB};

    private final GameObjectDeserializer deserializer;

    public ChannelMessageType readType(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(3);
        readFully(channel, buffer);
        readAndCheckStartBytes(buffer);
        byte messageType = buffer.get();
        return ChannelMessageType.fromCode(messageType);
    }

    public Command readCommand(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        readFully(channel, buffer);
        return Command.fromCode(buffer.get());
    }

    public GameObject readGameObject(SocketChannel channel) throws IOException {
        return deserializer.deserialize(channel);
    }

    public int readEntityId(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        readFully(channel, buffer);
        return buffer.getInt();
    }

    public GameWorld readWorld(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 * 2);
        readFully(channel, buffer);
        int w = buffer.getInt();
        int h = buffer.getInt();
        List<GameObject> objects = deserializer.deserializeAll(channel);
        GameWorld world = new GameWorld(w, h);
        objects.forEach(world::addObject);
        return world;
    }

    public Position readPosition(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 * 5);
        readFully(channel, buffer);
        return new Position(
                buffer.getInt(),
                buffer.getInt(),
                buffer.getInt(),
                Direction.ofValue(buffer.getInt(), buffer.getInt())
        );
    }

    private void readFully(SocketChannel channel, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new EOFException("Connection closed prematurely");
            }
        }
        buffer.flip();
    }

    private void readAndCheckStartBytes(ByteBuffer buffer) throws IOException {
        if (buffer.get() != START_BYTES[0])
            throw new IOException("Invalid channel start");
        if(buffer.get() != START_BYTES[1])
            throw new IOException("Invalid channel start");
    }
}