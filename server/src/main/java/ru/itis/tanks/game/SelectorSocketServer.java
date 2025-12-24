package ru.itis.tanks.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.Updatable;
import ru.itis.tanks.game.model.impl.tank.Command;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.game.model.map.GameWorldGenerator;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventListener;
import ru.itis.tanks.network.ChannelReader;
import ru.itis.tanks.network.ChannelWriter;
import ru.itis.tanks.network.Position;
import ru.itis.tanks.network.util.GameObjectDeserializer;
import ru.itis.tanks.network.util.GameObjectSerializer;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SelectorSocketServer implements GameEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<Integer, ClientManager> clients;

    private final ChannelReader reader;

    private final ChannelWriter writer;

    private GameWorld world;

    private ServerSocketChannel serverChannel;

    private Selector selector;

    public SelectorSocketServer() {
        this.clients = new LinkedHashMap<>();
        this.reader = new ChannelReader(new GameObjectDeserializer());
        this.writer = new ChannelWriter(new GameObjectSerializer());
    }

    public void start(InetSocketAddress address) throws IOException {
        logger.info("Starting socket server");
        world = GameWorldGenerator.generate();
        world.addWorldUpdateListener(this);
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(address);
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.debug("Listening on {}", address);
        run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            try {
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    try {
                        handleSelection(key);
                    } catch (EOFException | ClosedChannelException e) {
                        logger.info("Connection closed with {}", key.attachment());
                        if(key.attachment() != null) {
                            Integer id = (Integer) key.attachment();
                            Tank clientTank = clients.get(id).getController().getTank();
                            clients.remove(id);
                            world.removeObject(clientTank);
                            logger.debug("Cleaned up from client disconnection");
                            running = !clients.values().isEmpty();
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("IO exception", e);
                running = false;
            }
        }
        logger.info("Stopping socket server");
    }

    private void handleSelection(SelectionKey key) throws IOException {
        logger.debug("Handling selection for key {}", key.interestOps());
        if (key.isAcceptable()) {
            try {
                registerNewSelectorChannel(key, serverChannel.accept());
            } catch (ClosedChannelException e) {
                logger.debug("Registration failed, connection closed");
                throw e;
            } catch (IOException e) {
                logger.error("Registration failed", e);
                throw e;
            }
            return;
        }
        if (key.isReadable()) {
            try {
                handleRead(key);
            } catch (IOException e) {
                logger.error("Failed to handle read", e);
                throw e;
            }
        }

    }

    private void handleRead(SelectionKey key) throws IOException {
        logger.debug("Reading command");
        Integer id = (Integer) key.attachment();
        if (id == null)
            throw new EOFException("Client did not register");
        ClientManager client = clients.get(id);
        SocketChannel channel = client.getChannel();
        Command cmd = reader.readCommand(channel);
        client.getController().enqueueCommand(cmd);
    }

    private void registerNewSelectorChannel(SelectionKey key, SocketChannel clientChannel) throws IOException {
        clientChannel.configureBlocking(false);
        clientChannel.finishConnect();
        clientChannel.register(selector, SelectionKey.OP_READ);
        Position spawnPos = world.getSpawnPosition();
        Tank tank = new Tank(world, spawnPos.getX(), spawnPos.getY());
        clients.put(tank.getId(), new ClientManager(clientChannel, tank));
        key.attach(tank.getId());
        //todo ключ id не привязывается, как прикрепить ключ?
        world.addObject(tank);
    }

    @Override
    public void onGameEvent(GameEvent event) {
        switch (event.getType()) {
            case ADDED_OBJECT, MODIFIED_OBJECT -> sendObjectToClients(event.getObject());
            case REMOVED_OBJECT -> sendIdToClients((
                    (Updatable) event.getObject()).getId());
            case MOVED_OBJECT -> sendMoveToClients((MovingObject) event.getObject());
            case GAME_OVER -> sendGameOverToClients();
        }
    }

    private void sendGameOverToClients() {
        logger.debug("Sending game over");
        for (ClientManager c : clients.values()) {
            try {
                writer.writeGameOverMessage(c.getChannel());
            } catch (IOException e) {
                logger.error("Cannot send game over to {}", c.getId(), e);
            }
        }
    }

    private void sendObjectToClients(GameObject obj) {
        logger.debug("Sending object to client");
        for (ClientManager c : clients.values()) {
            try {
                writer.write(c.getChannel(), obj);
            } catch (IOException e) {
                Integer id = c.getId();
                logger.error("Cannot send object update to {}", id, e);
            }
        }
    }

    private void sendMoveToClients(MovingObject obj) {
        logger.debug("Sending move to client");
        for (ClientManager c : clients.values()) {
            try {
                writer.writePosition(
                        c.getChannel(), obj.getDirection(), obj.getX(), obj.getY());
            } catch (IOException e) {
                logger.error("Cannot send move update to {}", c.getId(), e);
            }
        }
    }

    private void sendIdToClients(int id) {
        logger.debug("Sending id to client");
        for (ClientManager c : clients.values()) {
            try {
                writer.writeId(c.getChannel(), id);
            } catch (IOException e) {
                logger.error("Cannot send remove update to {}", c.getId(), e);
            }
        }
    }
}
