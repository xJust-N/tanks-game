package ru.itis.tanks.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChannelWriter {

    private static final byte[] START_BYTES = {(byte) 0xA, (byte) 0xB};

    /*
     * Запись объектов в канал
     * Сначала записываются фиксированные служебные байты (2 байта),
     * затем длина сообщения (4 байта),
     * затем сам объект
     */

    public <T> void writeMessage(SocketChannel channel, T tObj) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(byteStream)) {
            oos.writeObject(tObj);
        }
        byte[] bytes = byteStream.toByteArray();

        ByteBuffer headerBuffer = ByteBuffer.wrap(START_BYTES);
        while (headerBuffer.hasRemaining()) {
            channel.write(headerBuffer);
        }

        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(bytes.length);
        lengthBuffer.flip();
        while (lengthBuffer.hasRemaining()) {
            channel.write(lengthBuffer);
        }

        ByteBuffer dataBuffer = ByteBuffer.wrap(bytes);
        while (dataBuffer.hasRemaining()) {
            channel.write(dataBuffer);
        }
    }
}