package ru.itis.tanks.game.model.map;

import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.Updatable;

import java.util.List;

public interface GameWorld {

    int getWidth();

    int getHeight();

    List<GameObject> getAllObjects();

    List<Updatable> getUpdatables();
}
