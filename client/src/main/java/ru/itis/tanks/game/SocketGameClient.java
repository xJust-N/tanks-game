package ru.itis.tanks.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.tanks.game.controller.ClientTankController;
import ru.itis.tanks.game.controller.TankKeyHandler;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.ui.GameWindow;
import ru.itis.tanks.network.ChannelReader;
import ru.itis.tanks.network.ChannelWriter;
import ru.itis.tanks.network.util.GameObjectSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SocketGameClient{

    private final GameWindow gameWindow;

    private final ChannelWriter writer;

    private final ChannelReader reader;

    private final String username;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Selector selector;

    private SocketChannel socketChannel;

    private boolean isRunning = false;

    public SocketGameClient(GameWindow gameWindow, String username) {
        this.gameWindow = gameWindow;
        this.username = username;
        this.reader = new ChannelReader();
        this.writer = new ChannelWriter(new GameObjectSerializer());
    }

    public void start(InetSocketAddress socketAddress) throws IOException {
        logger.info("Starting SocketGameClient");
        socketChannel = SocketChannel.open();
        selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(socketAddress);
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        gameWindow.addKeyListener(
                new TankKeyHandler(
                        new ClientTankController(
                                socketChannel, writer)));
        isRunning = true;
    }

    private void run() throws IOException {
        logger.info("Running SocketGameClient");
        while (isRunning){
            selector.select();
            for(SelectionKey key : selector.selectedKeys()) {
                handleSelection(key);
            }
        }
    }

    private void handleSelection(SelectionKey key) throws IOException {
        logger.debug("Handling selection key");
        if(key.isConnectable()) {
            logger.debug("Connectable");
            socketChannel.finishConnect();
            sendRegistrationMessage();
            key.interestOps(SelectionKey.OP_READ);
            logger.info("Successfully connected to {}", socketChannel.getRemoteAddress());
            return;
        }
        if(key.isReadable()) {
            reader.readMessage(socketChannel)
        }
    }

    private void sendRegistrationMessage() {
    }
}
