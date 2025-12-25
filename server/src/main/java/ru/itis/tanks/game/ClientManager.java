package ru.itis.tanks.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.impl.tank.ServerTankController;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.tank.TankController;

import java.nio.channels.SocketChannel;

@RequiredArgsConstructor
@Getter
public class ClientManager {

    private final int id;

    private final SocketChannel channel;

    private final String username;

    private final ServerTankController controller;


    public ClientManager(SocketChannel channel, Tank tank){
        this.id = tank.getId();
        this.channel = channel;
        this.controller = new ServerTankController(tank);
        this.username = "unknown";
    }

    public ClientManager(SocketChannel clientChannel, String username, Tank tank) {
        this.id = tank.getId();
        this.channel = clientChannel;
        this.username = username;
        this.controller = new ServerTankController(tank);
    }
}
