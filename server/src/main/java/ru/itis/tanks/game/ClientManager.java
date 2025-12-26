package ru.itis.tanks.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.impl.tank.ServerTankController;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.tank.TankController;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

@RequiredArgsConstructor
@Getter
public class ClientManager {

    private final int id;

    private final SelectionKey key;

    private final String username;

    private final ServerTankController controller;

    public ClientManager(SelectionKey key, String username, Tank tank) {
        this.id = tank.getId();
        this.key = key;
        this.username = username;
        this.controller = new ServerTankController(tank);
    }
    public SocketChannel getChannel() {
        return (SocketChannel) key.channel();
    }
}
