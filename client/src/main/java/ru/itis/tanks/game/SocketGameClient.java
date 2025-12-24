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
import ru.itis.tanks.game.ui.panels.GameWorldPanel;
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

public class SocketGameClient{

    private final GameWindow gameWindow;

    private final ChannelWriter writer;

    private final ChannelReader reader;

    private final String username;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Selector selector;

    private SocketChannel socketChannel;

    private GameWorld world;

    private boolean isRunning = false;

    public SocketGameClient(GameWindow gameWindow, String username) {
        this.gameWindow = gameWindow;
        this.username = username;
        this.reader = new ChannelReader(new GameObjectDeserializer());
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
        run();
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
            ChannelMessageType type =  reader.readType(socketChannel);
            switch(type) {
                case ALL_MAP -> {
                    world = reader.readWorld(socketChannel);
                    gameWindow.changePanel(
                            new GameWorldPanel(
                                    world.getAllObjects(),
                                    world.getWidth(),
                                    world.getWidth()));
                    logger.info("Successfully read all map");
                }
                case MOVING_UPDATE -> {
                    Position pos = reader.readPosition(socketChannel);
                    if(pos.getEntityId() == null){
                        logger.warn("null entity id received, cant update position");
                        return;
                    }
                    Updatable obj = world.getUpdatables().get(pos.getEntityId());
                    obj.setX(pos.getX());
                    obj.setY(pos.getY());
                    if(obj instanceof MovingObject movingObject)
                        movingObject.setDirection(pos.getDirection());
                    world.notifyWorldUpdate(
                            new GameEvent(
                                    obj,
                                    GameEventType.MOVED_OBJECT
                    ));
                }
                case ADDED_OBJECT ->{
                    GameObject obj = reader.readGameObject(socketChannel);
                    world.addObject(obj);
                }
                case REMOVED_OBJECT ->{
                    int id = reader.readEntityId(socketChannel);
                    world.removeObject(id);
                }
                case ENTITY_UPDATE -> {
                    GameObject obj = reader.readGameObject(socketChannel);
                    world.updateObject(obj);
                }
                default -> logger.warn("Unsupported type: {}", type);
            }
        }
    }

    private void sendRegistrationMessage() {

    }
}
