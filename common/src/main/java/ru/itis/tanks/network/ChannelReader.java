package ru.itis.tanks.network;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChannelReader {

    private static final int MAX_MESSAGE_SIZE = 10 * 1024 * 1024;

    private static final byte[] START_BYTES = {(byte) 0xA, (byte) 0xB};

    public <T> T readMessage(SocketChannel socketChannel, Class<T> tClass) throws IOException {
        ByteBuffer headerBuffer = ByteBuffer.allocate(2);
        while (headerBuffer.hasRemaining()) {
            int read = socketChannel.read(headerBuffer);
            if (read == -1) {
                if (headerBuffer.position() == 0) {
                    return null;
                }
                throw new EOFException("Connection closed while reading header");
            }
        }
        headerBuffer.flip();
        if (headerBuffer.get() != START_BYTES[0] || headerBuffer.get() != START_BYTES[1]) {
            throw new IOException("Invalid start bytes");
        }
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        while (lengthBuffer.hasRemaining()) {
            int read = socketChannel.read(lengthBuffer);
            if (read == -1) {
                throw new EOFException("Connection closed while reading length");
            }
        }
        lengthBuffer.flip();
        int length = lengthBuffer.getInt();

        if (length < 0 || length > MAX_MESSAGE_SIZE) {
            throw new IOException("Invalid message length: " + length);
        }

        ByteBuffer dataBuffer = ByteBuffer.allocate(length);
        while (dataBuffer.hasRemaining()) {
            int read = socketChannel.read(dataBuffer);
            if (read == -1) {
                throw new EOFException("Connection closed while reading data");
            }
        }

        dataBuffer.flip();
        byte[] bytes = new byte[length];
        dataBuffer.get(bytes);

        //Todo отказаться от java serialization
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            Object tObj = objIn.readObject();
            if(tClass.isInstance(tObj))
                return tClass.cast(tObj);
            else
                throw new IOException("Received message is not object of type %s".formatted(tClass.getSimpleName()));
        } catch (ClassNotFoundException e) {
            throw new IOException("Class not found during deserialization", e);
        }
    }
}