package ru.itis.tanks.network.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.map.GameWorld;

import java.nio.channels.SocketChannel;
import java.util.List;

@NoArgsConstructor
public class GameObjectDeserializer {

    private GameWorld world;

    public GameObject deserialize(SocketChannel bytes) {
        return null;
    }

    public List<GameObject> deserializeAll(SocketChannel channel) {
        return null;
    }
}
