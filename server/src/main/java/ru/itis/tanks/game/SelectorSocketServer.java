package ru.itis.tanks.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.tanks.game.model.*;
import ru.itis.tanks.game.model.impl.tank.*;
import ru.itis.tanks.game.model.map.GameWorld;
import ru.itis.tanks.game.model.map.GameWorldGenerator;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventListener;
import ru.itis.tanks.network.ChannelMessageType;
import ru.itis.tanks.network.ChannelReader;
import ru.itis.tanks.network.ChannelWriter;
import ru.itis.tanks.network.Position;
import ru.itis.tanks.network.util.GameObjectDeserializer;
import ru.itis.tanks.network.util.GameObjectSerializer;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.*;

public class SelectorSocketServer implements GameEventListener {

    private static final int UPDATE_INTERVAL_MS = 16;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<TankController> controllers = new ArrayList<>();

    private final Thread gameUpdateThread;

    private final Map<Integer, ClientManager> clients;

    private final ChannelReader reader;

    private final ChannelWriter writer;

    private GameWorld world;

    private ServerSocketChannel serverChannel;

    private Selector selector;

    private volatile boolean running = true;

    public SelectorSocketServer() {
        this.clients = new LinkedHashMap<>();
        this.reader = new ChannelReader(new GameObjectDeserializer());
        this.writer = new ChannelWriter(new GameObjectSerializer());
        this.gameUpdateThread = new Thread(this::updateLoop);
    }

    public void start(InetSocketAddress address) throws IOException {
        logger.info("Starting socket server");
        world = GameWorldGenerator.generate();
        world.getTanks().values().forEach(t -> controllers.add(new ServerTankController(t)));
        startBotTanksThreads();
        world.addWorldUpdateListener(this);
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(address);
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.debug("Listening on {}", address);
        run();
    }

    private void startBotTanksThreads() {
       //controllers.forEach(controller -> new AITank(controller).start());
    }

    private void run() {
        running = true;
        gameUpdateThread.start();
        while (running) {
            try {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while(keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
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
                            //running = !clients.values().isEmpty();
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
            SocketChannel clientChannel = serverChannel.accept();
            if (clientChannel != null) {
                try {
                    registerNewSelectorChannel(clientChannel);
                } catch (ClosedChannelException e) {
                    logger.debug("Registration failed, connection closed");
                    clientChannel.close();
                    throw e;
                } catch (IOException e) {
                    logger.error("Registration failed", e);
                    clientChannel.close();
                    throw e;
                }
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
        if (id == null) {
            logger.error("Client id is null for key {}", key);
            throw new EOFException("Client did not register");
        }
        ClientManager client = clients.get(id);
        if (client == null) {
            logger.error("Client not found for id {}", id);
            throw new EOFException("Client not found");
        }
        SocketChannel channel = client.getChannel();
        Command cmd = reader.readCommand(channel);
        logger.debug("Received {} command from client {}", cmd, id);
        client.getController().enqueueCommand(cmd);
        logger.info("Successfully handled client {} command", id);
    }

    private void registerNewSelectorChannel(SocketChannel clientChannel) throws IOException {
        clientChannel.configureBlocking(false);
        SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
        String username = readUsernameMessage(clientChannel);
        logger.debug("Received username {}", username);
        Position spawnPos = world.getSpawnPosition();
        Tank tank = new Tank(world, spawnPos.getX(), spawnPos.getY());
        tank.setUsername(username);
        ClientManager client = new ClientManager(clientChannel, username, tank);
        sendWorldToClient(clientChannel);
        clientKey.attach(tank.getId());
        clients.put(tank.getId(), client);
        controllers.add(client.getController());
        world.addObject(tank);
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if(clients.isEmpty())
            return;
        switch (event.getType()) {
            case ADDED_OBJECT -> sendNewObjectToClients(event.getObject());
            case MODIFIED_OBJECT -> sendObjectUpdateToClients(event.getObject());
            case REMOVED_OBJECT -> sendIdToClients((
                    (Removable) event.getObject()).getId());
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

    private void sendObjectUpdateToClients(GameObject obj) {
        logger.debug("Sending object update to clients");
        int i = 0;
        for (ClientManager c : clients.values()) {
            try {
                writer.writeObjectUpdate(c.getChannel(), obj);
                i+=1;
            } catch (IOException e) {
                Integer id = c.getId();
                logger.error("Cannot send object update to {}", id, e);
            }
        }
        if(i == clients.size())
            logger.debug("Successfully sent update object {} to {} clients", obj.toString(), i);
        else
            logger.warn("Failed to send update object {} to {} clients", obj.toString(), clients.size() - i);
    }

    private void sendNewObjectToClients(GameObject obj){
        logger.debug("Sending new object to clients");
        int i = 0;
        for (ClientManager c : clients.values()) {
            try {
                writer.writeNewObject(c.getChannel(), obj);
                i+=1;
            } catch (IOException e) {
                Integer id = c.getId();
                logger.error("Cannot send new object to {}", id, e);
            }
        }
        if(i == clients.size())
            logger.debug("Successfully sent new object {} to {} clients", obj.toString(), i);
        else
            logger.warn("Failed to send new object {} to {} clients", obj.toString(), clients.size() - i);
    }

    private void sendMoveToClients(MovingObject obj) {
        for (ClientManager c : clients.values()) {
            try {
                writer.writePosition(
                        c.getChannel(), obj.getId(), obj.getDirection(), obj.getX(), obj.getY());
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

    private void updateLoop(){
        logger.info("Started world update loop");
        long lastUpdateTime = System.currentTimeMillis();
        long currentTime;
        long deltaTime;
        while(running){
            currentTime = System.currentTimeMillis();
            deltaTime = currentTime - lastUpdateTime;
            if(deltaTime >= UPDATE_INTERVAL_MS){
                controllers.forEach(TankController::processCommands);
                updateWorld(Math.toIntExact(deltaTime));
                lastUpdateTime = currentTime;
            }
            else{
                try {
                    Thread.sleep(UPDATE_INTERVAL_MS);
                } catch (InterruptedException e) {
                    logger.error("Game update loop is interrupted", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void updateWorld(int deltaTime) {
        for (Updatable u : world.getUpdatables().values()) {
            u.update(deltaTime);
        }
    }

    private void sendWorldToClient(SocketChannel channel) throws IOException {
        logger.info("Sending world to client");
        writer.write(channel, world);
        logger.info("Successfully sent world to client");
    }

    private String readUsernameMessage(SocketChannel channel) throws IOException{
        logger.info("Reading username");
        ChannelMessageType type = reader.readType(channel);
        if(type != ChannelMessageType.REGISTER){
            logger.warn("Expected register message, received {}", type);
            throw new EOFException("Invalid register message, closing connection");
        }
        logger.info("Received register message from client {}", channel.getRemoteAddress());
        return reader.readString(channel);
    }
}
