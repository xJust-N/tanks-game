package ru.itis.tanks.game;

import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.Updatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientGameWorld {

    private final List<GameObject> allObjects;

    private final HashMap<Integer, Updatable> updatables;

    public ClientGameWorld() {
        allObjects = new ArrayList<>();
        updatables = new HashMap<>();
    }

    public void addObject(GameObject object) {
        allObjects.add(object);
        if(object instanceof Updatable updatable)
            updatables.put(updatable.getId(), updatable);
    }

    public void removeObject(GameObject object) {
        allObjects.remove(object);
        if(object instanceof Updatable updatable){
            updatables.remove(updatable.getId());
        }
    }
}
