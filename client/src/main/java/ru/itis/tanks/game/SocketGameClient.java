package ru.itis.tanks.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.tanks.game.controller.ClientTankController;
import ru.itis.tanks.game.controller.TankKeyHandler;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.Updatable;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventType;
import ru.itis.tanks.game.ui.GameWindow;
import ru.itis.tanks.game.ui.panels.GameWorldRenderer;
import ru.itis.tanks.network.ChannelMessageType;
import ru.itis.tanks.network.ChannelReader;
import ru.itis.tanks.network.ChannelWriter;
import ru.itis.tanks.network.Position;
import ru.itis.tanks.network.util.GameObjectDeserializer;
import ru.itis.tanks.network.util.GameObjectSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketGameClient {

    private static final int UPDATE_INTERVAL_MS = 16;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Thread updateThread;

    private final GameWindow gameWindow;

    private final ChannelWriter writer;

    private final ChannelReader reader;

    private final String username;

    private GameWorld world;

    private ClientTankController tankController;

    private Selector selector;

    private SocketChannel socketChannel;

    private volatile boolean isRunning = false;

    public SocketGameClient(GameWindow gameWindow, String username) {
        this.gameWindow = gameWindow;
        this.username = username;
        this.reader = new ChannelReader(new GameObjectDeserializer());
        this.writer = new ChannelWriter(new GameObjectSerializer());
        this.updateThread = new Thread(this::updateLoop);
    }

    public void start(InetSocketAddress socketAddress) throws IOException {
        logger.info("Starting SocketGameClient");
        socketChannel = SocketChannel.open();
        selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(socketAddress);
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        tankController = new ClientTankController(socketChannel, writer);
        gameWindow.addKeyListener(
                new TankKeyHandler(tankController));
        isRunning = true;
        run();
    }

    private void run() throws IOException {
        logger.info("Running SocketGameClient");
        while (isRunning) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                handleSelection(key);
            }
        }
    }

    private void handleSelection(SelectionKey key) throws IOException {
        logger.debug("Handling selection key");
        if (key.isConnectable()) {
            logger.debug("Connectable");
            if (socketChannel.finishConnect()) {
                sendRegistrationMessage();
                key.interestOps(SelectionKey.OP_READ);
                key.interestOpsOr(SelectionKey.OP_WRITE);
                logger.info("Successfully connected to {}", socketChannel.getRemoteAddress());
            }
            return;
        }
        if (key.isReadable()) {
            ChannelMessageType type = reader.readType(socketChannel);
            logger.debug("Received message type: {}", type);
            switch (type) {
                case ALL_MAP -> {
                    world = reader.readWorld(socketChannel);
                    logger.info("Successfully read all map with size {}, width: {}, height: {}",
                            world.getAllObjects().size(), world.getWidth(), world.getHeight());
                    GameWorldRenderer renderer = new GameWorldRenderer(world);
                    gameWindow.changePanel(renderer);
                    updateThread.start();
                    logger.debug("Renderer created and panel changed");

                }
                case MOVING_UPDATE -> {
                    Position pos = reader.readPosition(socketChannel);
                    logger.debug("Received position: {}, {}", pos.getX(), pos.getY());
                    if (pos.getEntityId() == null) {
                        logger.warn("null entity id received, cant update position");
                        return;
                    }
                    Updatable obj = world.getUpdatables().get(pos.getEntityId());
                    obj.setX(pos.getX());
                    obj.setY(pos.getY());
                    if (obj instanceof MovingObject movingObject)
                        movingObject.setDirection(pos.getDirection());
                    world.notifyWorldUpdate(
                            new GameEvent(
                                    obj,
                                    GameEventType.MOVED_OBJECT
                            ));
                }
                case ADDED_ENTITY -> {
                    GameObject obj = reader.readGameObject(socketChannel);
                    world.addObject(obj);
                    logger.debug("Successfully added entity");
                }
                case REMOVED_ENTITY -> {
                    int id = reader.readEntityId(socketChannel);
                    world.removeObject(id);
                    logger.debug("Successfully removed entity");
                }
                case ENTITY_UPDATE -> {
                    GameObject obj = reader.readGameObject(socketChannel);
                    world.updateObject(obj);
                    logger.debug("Successfully updated entity");
                }
                default -> logger.warn("Unsupported type: {}", type);
            }
        }
        if(key.isWritable()){
            if (tankController.hasCommands()) {
                logger.debug("Writing commands");
                tankController.processCommands();
                logger.debug("Sent commands");
            }
        }
        try {
            Thread.sleep(UPDATE_INTERVAL_MS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRegistrationMessage() {
        try {
            logger.debug("Sending registration message for user: {}", username);
            writer.write(socketChannel, ChannelMessageType.REGISTER, username);
            logger.debug("Registration message sent successfully");
        } catch (IOException e) {
            logger.error("Failed to send registration message", e);
            try {
                socketChannel.close();
            } catch (IOException ex) {
                logger.error("Failed to close channel", ex);
            }
        }
    }

    private void updateLoop() {
        logger.debug("Started update thread");
        while (isRunning) {
            gameWindow.update();
            try {
                Thread.sleep(UPDATE_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
