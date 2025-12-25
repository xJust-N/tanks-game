package ru.itis.tanks.network;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.network.util.GameObjectDeserializer;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

@RequiredArgsConstructor
public class ChannelReader {

    private static final Logger logger = LoggerFactory.getLogger(ChannelReader.class);
    private static final int MAX_MESSAGE_SIZE = 10 * 1024 * 1024;
    private static final byte[] START_BYTES = {(byte) 0xA, (byte) 0xB};

    private final GameObjectDeserializer deserializer;

    public ChannelMessageType readType(SocketChannel channel) throws IOException {
        logger.debug("Reading message type from channel: {}", channel.getRemoteAddress());
        ByteBuffer buffer = ByteBuffer.allocate(3);
        readFully(channel, buffer);
        readAndCheckStartBytes(buffer);
        byte messageType = buffer.get();
        ChannelMessageType type = ChannelMessageType.fromCode(messageType);
        logger.debug("Message type read: {} from channel: {}", type, channel.getRemoteAddress());
        return type;
    }

    public Queue<Command> readCommands(SocketChannel channel) throws IOException {
        logger.debug("Reading commands from channel: {}", channel.getRemoteAddress());
        ByteBuffer buffer = ByteBuffer.allocate(4);
        readFully(channel, buffer);
        int size = buffer.getInt();
        logger.debug("Ready to read {} commands from channel: {}", size, channel.getRemoteAddress());
        ByteBuffer commandBuffer = ByteBuffer.allocate(size);
        readFully(channel, commandBuffer);
        Queue<Command> commands = new ArrayDeque<>(size);
        for (int i = 0; i < size; i++)
            commands.add(Command.fromCode(commandBuffer.get()));
        logger.debug("Commands read: {} from channel: {}", commands.size(), channel.getRemoteAddress());
        return commands;
    }

    public GameObject readGameObject(SocketChannel channel) throws IOException {
        logger.debug("Reading game object from channel: {}", channel.getRemoteAddress());
        GameObject obj = deserializer.deserialize(channel);
        logger.debug("Game object read: {} from channel: {}", obj.toString(), channel.getRemoteAddress());
        return obj;
    }

    public int readEntityId(SocketChannel channel) throws IOException {
        logger.debug("Reading entity ID from channel: {}", channel.getRemoteAddress());
        ByteBuffer buffer = ByteBuffer.allocate(4);
        readFully(channel, buffer);
        int id = buffer.getInt();
        logger.debug("Entity ID read: {} from channel: {}", id, channel.getRemoteAddress());
        return id;
    }

    public GameWorld readWorld(SocketChannel channel) throws IOException {
        logger.debug("Reading game world from channel: {}", channel.getRemoteAddress());
        ByteBuffer sizeBuffer = ByteBuffer.allocate(8);
        readFully(channel, sizeBuffer);
        int w = sizeBuffer.getInt();
        int h = sizeBuffer.getInt();
        ByteBuffer countBuffer = ByteBuffer.allocate(4);
        readFully(channel, countBuffer);
        int count = countBuffer.getInt();
        List<GameObject> objects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            objects.add(deserializer.deserialize(channel));
        }
        GameWorld world = new GameWorld(w, h);
        objects.forEach(world::addObject);
        deserializer.setWorld(world);
        logger.info("Game world read: {}x{}, objects: {} from channel: {}",
                w, h, objects.size(), channel.getRemoteAddress());
        return world;
    }

    public Position readPosition(SocketChannel channel) throws IOException {
        logger.debug("Reading position from channel: {}", channel.getRemoteAddress());
        ByteBuffer buffer = ByteBuffer.allocate(4 * 5);
        readFully(channel, buffer);
        Position position = new Position(
                buffer.getInt(),
                buffer.getInt(),
                buffer.getInt(),
                Direction.ofValue(buffer.getInt(), buffer.getInt())
        );
        logger.debug("Position read: ({}, {}) from channel: {}",
                position.getX(), position.getY(), channel.getRemoteAddress());
        return position;
    }

    public String readString(SocketChannel channel) throws IOException {
        logger.debug("Reading string from channel: {}", channel.getRemoteAddress());
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        readFully(channel, lengthBuffer);
        int length = lengthBuffer.getInt();
        if(length >= MAX_MESSAGE_SIZE)
            throw new EOFException("Message too long");
        ByteBuffer buffer = ByteBuffer.allocate(length);
        readFully(channel, buffer);
        String result = new String(buffer.array());
        logger.debug("String read, length: {} from channel: {}", length, channel.getRemoteAddress());
        return result;
    }

    private void readFully(SocketChannel channel, ByteBuffer buffer) throws IOException {
        logger.trace("Reading fully from channel: {}, buffer size: {}",
                channel.getRemoteAddress(), buffer.capacity());
        int totalRead = 0;
        while (buffer.hasRemaining()) {
            int read = channel.read(buffer);
            if (read == -1) {
                throw new EOFException("Connection closed prematurely, read " + totalRead);
            }
            totalRead += read;
        }
        buffer.flip();
        logger.trace("Read fully completed, bytes read: {}", totalRead);
    }

    private void readAndCheckStartBytes(ByteBuffer buffer) throws IOException {
        logger.trace("Checking start bytes");
        if (buffer.get() != START_BYTES[0])
            throw new IOException("Invalid channel start");
        if(buffer.get() != START_BYTES[1])
            throw new IOException("Invalid channel start");
        logger.trace("Start bytes checked successfully");
    }
}