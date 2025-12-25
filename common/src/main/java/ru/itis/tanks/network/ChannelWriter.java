package ru.itis.tanks.network;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.network.util.GameObjectSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import static ru.itis.tanks.network.ChannelMessageType.*;

@RequiredArgsConstructor
public class ChannelWriter {

    private static final Logger logger = LoggerFactory.getLogger(ChannelWriter.class);
    private static final byte[] START_BYTES = {(byte) 0xA, (byte) 0xB};

    private final GameObjectSerializer serializer;

    public void writeObjectUpdate(SocketChannel channel, GameObject obj) throws IOException {
        logger.debug("Writing object update: {} to channel: {}", obj.toString(), channel.getRemoteAddress());
        writeStartBytes(channel);
        writeMessageType(channel, ENTITY_UPDATE);
        ByteBuffer b = serializer.serialize(obj);
        while(b.hasRemaining())
            channel.write(b);
        logger.debug("Object update written: {}", obj);
    }

    public void writeNewObject(SocketChannel channel, GameObject obj) throws IOException {
        logger.debug("Writing new object: {} to channel: {}", obj.toString(), channel.getRemoteAddress());
        writeStartBytes(channel);
        writeMessageType(channel, ADDED_ENTITY);
        ByteBuffer b = serializer.serialize(obj);
        while(b.hasRemaining())
            channel.write(b);
        logger.info("New object written: {} to channel: {}", obj, channel.getRemoteAddress());
    }

    public void write(SocketChannel channel, GameWorld world) throws IOException {
        logger.debug("Writing entire game world to channel: {}", channel.getRemoteAddress());
        writeStartBytes(channel);
        writeMessageType(channel, ALL_MAP);
        ByteBuffer sizeBuffer = ByteBuffer.allocate(8);
        sizeBuffer.putInt(world.getWidth());
        sizeBuffer.putInt(world.getHeight());
        sizeBuffer.flip();
        while(sizeBuffer.hasRemaining())
            channel.write(sizeBuffer);

        List<GameObject> objects = world.getAllObjects();
        ByteBuffer countBuffer = ByteBuffer.allocate(4);
        countBuffer.putInt(objects.size());
        countBuffer.flip();
        while(countBuffer.hasRemaining())
            channel.write(countBuffer);

        for (GameObject obj : objects) {
            ByteBuffer b = serializer.serialize(obj);
            while(b.hasRemaining())
                channel.write(b);
        }
        logger.info("Game world written: {}x{}, objects: {} to channel: {}",
                world.getWidth(), world.getHeight(), objects.size(), channel.getRemoteAddress());
    }

    public void writePosition(SocketChannel channel, Direction dir, int x, int y) throws IOException {
        logger.debug("Writing position update: ({}, {}), direction: {} to channel: {}",
                x, y, dir, channel.getRemoteAddress());
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
        logger.debug("Position update written: ({}, {})", x, y);
    }

    private void writeStartBytes(SocketChannel channel) throws IOException {
        logger.trace("Writing start bytes to channel: {}", channel.getRemoteAddress());
        ByteBuffer buffer = ByteBuffer.wrap(START_BYTES);
        while(buffer.hasRemaining())
            channel.write(buffer);
    }

    private void writeMessageType(SocketChannel channel, ChannelMessageType type) throws IOException {
        logger.trace("Writing message type: {} to channel: {}", type, channel.getRemoteAddress());
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{(byte) type.getCode()});
        while(buffer.hasRemaining())
            channel.write(buffer);
    }

    public void writeId(SocketChannel channel, int id) throws IOException {
        logger.debug("Writing entity removal: {} to channel: {}", id, channel.getRemoteAddress());
        writeStartBytes(channel);
        writeMessageType(channel, REMOVED_ENTITY);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(id);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        logger.debug("Entity removal written: {}", id);
    }

    public void writeGameOverMessage(SocketChannel channel) throws IOException {
        logger.debug("Writing game over message to channel: {}", channel.getRemoteAddress());
        writeStartBytes(channel);
        writeMessageType(channel, GAME_OVER);
        logger.info("Game over message written to channel: {}", channel.getRemoteAddress());
    }

    public void writeCommands(SocketChannel channel, Queue<Command> commands) throws IOException {
        logger.debug("Writing commands: {} to channel: {}", commands.size(), channel.getRemoteAddress());
        writeStartBytes(channel);
        writeMessageType(channel, COMMAND);
        int size = commands.size();
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(size);
        while (buffer.hasRemaining())
            channel.write(buffer);
        buffer = ByteBuffer.allocate(size);
        for(int i = 0; i < size; i++)
            buffer.put((byte) Objects.requireNonNull(commands.poll()).getCode());
        buffer.flip();
        while(buffer.hasRemaining()) {
            channel.write(buffer);
        }
        logger.debug("Commands written: {} to channel: {}", size, channel.getRemoteAddress());
    }

    public void write(SocketChannel socketChannel, ChannelMessageType channelMessageType, String str) throws IOException {
        logger.debug("Writing string message type: {}, length: {} to channel: {}",
                channelMessageType, str.length(), socketChannel.getRemoteAddress());
        writeStartBytes(socketChannel);
        writeMessageType(socketChannel, channelMessageType);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(str.length());
        buffer.flip();
        while (buffer.hasRemaining())
            socketChannel.write(buffer);
        buffer = ByteBuffer.wrap(str.getBytes());
        while (buffer.hasRemaining())
            socketChannel.write(buffer);
        logger.debug("String message written: {} to channel: {}", channelMessageType, socketChannel.getRemoteAddress());
    }

}