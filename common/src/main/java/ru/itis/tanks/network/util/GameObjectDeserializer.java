package ru.itis.tanks.network.util;

import lombok.AllArgsConstructor;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.map.ServerGameWorld;

@AllArgsConstructor
public class GameObjectDeserializer {

    private final ServerGameWorld world;

    public GameObject deserialize(byte[] bytes) {
        return null;
    }
}
