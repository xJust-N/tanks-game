package ru.itis.tanks.network;

import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.map.ServerGameWorld;
import ru.itis.tanks.network.util.GameObjectSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static ru.itis.tanks.network.ChannelMessageType.*;

@RequiredArgsConstructor
public class ChannelWriter {

    private static final byte[] START_BYTES = {(byte) 0xA, (byte) 0xB};

    private final GameObjectSerializer serializer;

    public void write(SocketChannel channel, GameObject obj) throws IOException {
        writeStartBytes(channel);
        writeMessageType(channel, ENTITY_UPDATE);
        channel.write(serializer.serialize(obj));
    }

    public void write(SocketChannel channel, ServerGameWorld world) throws IOException {
        writeStartBytes(channel);
        writeMessageType(channel, ALL_MAP);
        for (GameObject obj : world.getAllObjects()) {
            channel.write(serializer.serialize(obj));
        }
    }
    public void writePosition(SocketChannel channel, MovingObject obj) throws IOException {
        writeStartBytes(channel);
        writeMessageType(channel, COORDINATE_UPDATE);
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4);
        buffer.putInt(obj.getX());
        buffer.putInt(obj.getY());
        buffer.putInt(obj.getDirection().getX());
        buffer.putInt(obj.getDirection().getY());
        buffer.flip();
        channel.write(buffer);
    }

    private void writeStartBytes(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap(START_BYTES));
    }
    private void writeMessageType(SocketChannel channel, ChannelMessageType type) throws IOException {
        channel.write(ByteBuffer.wrap(new byte[]{(byte) type.getCode()}));
    }
}