package ru.itis.tanks.game;

import ru.itis.tanks.game.ui.GameWindow;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SocketGameClient {

    private final GameWindow gameWindow;

    private final String username;

    private SocketChannel socketChannel;

    public SocketGameClient(GameWindow gameWindow, String username) {
        this.gameWindow = gameWindow;
        this.username = username;
    }

    public void start(InetSocketAddress socketAddress) throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(socketAddress);
    }
}
