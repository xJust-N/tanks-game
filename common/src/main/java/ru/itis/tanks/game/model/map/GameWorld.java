package ru.itis.tanks.game.model.map;

import lombok.Getter;
import ru.itis.tanks.game.model.*;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventDispatcher;
import ru.itis.tanks.game.model.map.updates.GameEventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.itis.tanks.game.model.map.updates.GameEventType.*;

//Todo: сериализация и десериализация
//Todo game over update(один побеждает, другой проигрывает)
@Getter
public class GameWorld implements GameEventDispatcher {

    private final long width;

    private final long height;

    private final List<GameObject> allObjects;

    private final List<Updatable> updatables;

    private final List<Tank> tanks;

    private final List<GameEventListener> listeners;

    private final CollisionHandler collisionHandler;

    public GameWorld(long width, long height) {
        this.width = width;
        this.height = height;
        listeners = new CopyOnWriteArrayList<>();
        allObjects = new CopyOnWriteArrayList<>();
        updatables = new CopyOnWriteArrayList<>();
        tanks = new CopyOnWriteArrayList<>();
        collisionHandler = new CollisionHandler(this);
    }

    public void addObject(GameObject object) {
        if (object instanceof Updatable updatable)
            updatables.add(updatable);
        if(object instanceof Collideable collideable)
            collisionHandler.addToGrid(collideable, object.getX(), object.getY());
        if(object instanceof Tank tank)
            tanks.add(tank);
        allObjects.add(object);
        notifyWorldUpdate(new GameEvent(object, ADDED_OBJECT));
    }

    public void removeObject(GameObject object) {
        if (object instanceof Updatable updatable)
            updatables.remove(updatable);
        if (object instanceof Collideable)
            collisionHandler.removeFromGrid(object.getX(), object.getY(), (Collideable) object);
        if(object instanceof Tank tank)
            tanks.remove(tank);
        allObjects.remove(object);
        notifyWorldUpdate(new GameEvent(object, REMOVED_OBJECT));
        if(tanks.size() == 1)
            notifyWorldUpdate(new GameEvent(tanks.getFirst(), GAME_OVER));
    }

    public void handleCollision(MovingObject obj, long oldX, long oldY){
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

}
