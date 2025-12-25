package ru.itis.tanks.game.model.map;

import lombok.Getter;
import ru.itis.tanks.game.model.*;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventDispatcher;
import ru.itis.tanks.game.model.map.updates.GameEventListener;
import ru.itis.tanks.network.Position;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.itis.tanks.game.model.map.updates.GameEventType.*;

//Todo: сериализация и десериализация
//Todo game over update(один побеждает, другой проигрывает)
@Getter
public class GameWorld implements GameEventDispatcher{

    private final int width;

    private final int height;

    private final List<GameObject> allObjects;

    private final Map<Integer, Updatable> updatables;

    private final Map<Integer, Tank> tanks;

    private final Map<Integer, Identifiable> identifiables;

    private final List<GameEventListener> listeners;

    private final CollisionHandler collisionHandler;


    public GameWorld(int width, int height) {
        this.width = width;
        this.height = height;
        listeners = new CopyOnWriteArrayList<>();
        allObjects = new CopyOnWriteArrayList<>();
        updatables = new ConcurrentHashMap<>();
        tanks = new LinkedHashMap<>();
        identifiables = new HashMap<>();
        collisionHandler = new CollisionHandler(this);
    }

    public void addObject(GameObject object) {
        if (object instanceof Updatable updatable)
            updatables.put(updatable.getId(), updatable);
        if(object instanceof Collideable collideable)
            collisionHandler.addToGrid(collideable, object.getX(), object.getY());
        if(object instanceof Tank tank)
            tanks.put(tank.getId(), tank);
        if(object instanceof Identifiable identifiable)
            identifiables.put(identifiable.getId(), identifiable);
        allObjects.add(object);
        notifyWorldUpdate(new GameEvent(object, ADDED_OBJECT));
    }

    public void removeObject(GameObject object) {
        if (object instanceof Updatable updatable)
            updatables.remove(updatable.getId());
        if (object instanceof Collideable)
            collisionHandler.removeFromGrid(object.getX(), object.getY(), (Collideable) object);
        if(object instanceof Tank tank)
            tanks.remove(tank.getId());
        if(object instanceof Identifiable identifiable)
            identifiables.remove(identifiable.getId());
        allObjects.remove(object);
        notifyWorldUpdate(new GameEvent(object, REMOVED_OBJECT));
        if(tanks.size() == 1)
            notifyWorldUpdate(new GameEvent(tanks.values().stream().findAny().get(), GAME_OVER));
    }

    public void handleCollision(MovingObject obj, int oldX, int oldY){
        collisionHandler.handleCollision(obj, oldX, oldY);
    }

    @Override
    public void addWorldUpdateListener(GameEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void notifyWorldUpdate(GameEvent update) {
        listeners.forEach(l -> l.onGameEvent(update));
    }

    public void removeObject(int id) {
        removeObject(identifiables.get(id));
    }

    public void updateObject(GameObject obj) {
        removeObject(obj);
        addObject(obj);
    }

    public Position getSpawnPosition() {
        //todo
        return new Position(width/2, height/2);
    }
}
