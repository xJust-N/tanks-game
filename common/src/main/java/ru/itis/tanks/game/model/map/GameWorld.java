package ru.itis.tanks.game.model.map;

import lombok.Getter;
import ru.itis.tanks.game.model.*;
import ru.itis.tanks.game.model.impl.weapon.Projectile;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventDispatcher;
import ru.itis.tanks.game.model.map.updates.GameEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.itis.tanks.game.model.map.updates.GameEventType.*;

//Todo: сериализация и десериализация
//Todo: проблема с синхронизации: обновление(1 поток)
//Todo game over update(один побеждает, другой проигрывает)
@Getter
public class GameWorld implements GameEventDispatcher {

    private static final long GRID_SIZE = 32;

    //Константа для отступа при столкновении, чтобы объекты не застревали друг в друге
    private static final long COLLISION_OFFSET = 5;

    private final long width;

    private final long height;

    private final List<GameObject> allObjects;

    private final List<Updatable> updatables;

    private final List<Tank> tanks;

    private final List<GameEventListener> listeners;

    /*
     *  Представляет собой сетку, в ячейках которых лежат объекты с коллизией,
     *  Первый Long - х координата, второй - y
     *  Для проверки ближайших коллизий берется ячейка по координатам и ее соседние
     */
    private final Map<Long, Map<Long, List<Collideable>>> collisionGrid;


    public GameWorld(long width, long height) {
        this.width = width;
        this.height = height;
        listeners = new CopyOnWriteArrayList<>();
        allObjects = new CopyOnWriteArrayList<>();
        updatables = new CopyOnWriteArrayList<>();
        tanks = new CopyOnWriteArrayList<>();
        collisionGrid = new ConcurrentHashMap<>();
    }

    public void addObject(GameObject object) {
        if (object instanceof Updatable updatable)
            updatables.add(updatable);
        if(object instanceof Collideable collideable)
            addToGrid(collideable, object.getX(), object.getY());
        if(object instanceof Tank tank)
            tanks.add(tank);
        allObjects.add(object);
        notifyWorldUpdate(new GameEvent(object, ADDED_OBJECT));
    }

    public void removeObject(GameObject object) {
        if (object instanceof Updatable updatable)
            updatables.remove(updatable);
        if (object instanceof Collideable)
            removeFromGrid(object.getX(), object.getY(), (Collideable) object);
        if(object instanceof Tank tank)
            tanks.remove(tank);
        allObjects.remove(object);
        notifyWorldUpdate(new GameEvent(object, REMOVED_OBJECT));
    }

    @Override
    public void addWorldUpdateListener(GameEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void notifyWorldUpdate(GameEvent update) {
        listeners.forEach(l -> l.onGameEvent(update));
    }

    //Вызывается при перемещении движущимся объектом для обработки коллизий и уведомления листенеров
    public void handleCollision(MovingObject obj, long oldX, long oldY) {
        long currentGridX = getGridCoordinate(obj.getX());
        long currentGridY = getGridCoordinate(obj.getY());
        long oldGridX = getGridCoordinate(oldX);
        long oldGridY = getGridCoordinate(oldY);
        List<Collideable> nearbyCollidables = getNearbyCollidables(currentGridX, currentGridY);
        for (Collideable collideable : nearbyCollidables) {
            if (obj == collideable)
                continue;
            if (obj.intersects(collideable)) {
                processCollision(obj, collideable);
            }
        }
        if (currentGridX != oldGridX || currentGridY != oldGridY) {
            removeFromGrid(oldX, oldY, obj);
            addToGrid(obj, obj.getX(), obj.getY());
        }
        notifyWorldUpdate(new GameEvent(obj, MOVED_OBJECT));
    }

    private void processCollision(MovingObject obj, GameObject secondObj) {
        if(obj instanceof Projectile projectile)
            handleProjectileCollision(projectile, secondObj);
        if(obj instanceof Tank tank)
            handleTankCollision(tank, secondObj);
    }


    private void handleProjectileCollision(Projectile projectile, GameObject other) {
        if(projectile.getTank() == other)
            return;
        if (other instanceof Destroyable destroyable) {
            destroyable.takeDamage(projectile.getDamage());
            notifyWorldUpdate(new GameEvent(destroyable, MODIFIED_OBJECT));
        }
        projectile.destroy();
    }

    private void handleTankCollision(Tank tank, GameObject secondObj) {
        if(secondObj instanceof Projectile projectile) {
            handleProjectileCollision(projectile, tank);
            return;
        }
        Direction offsetDir = tank.getDirection().opposite();
        long newX = tank.getX() + offsetDir.getX() * COLLISION_OFFSET;
        long newY = tank.getY() + offsetDir.getY() * COLLISION_OFFSET;
        tank.setX(newX);
        tank.setY(newY);
    }

    private List<Collideable> getNearbyCollidables(long gridX, long gridY) {
        List<Collideable> nearbyCollidables = new ArrayList<>();
        for(int dx = -1; dx <= 1; dx++)
            for(int dy = -1; dy <= 1; dy++)
                nearbyCollidables.addAll(getCollidablesInCell(gridX + dx, gridY + dy));

        return nearbyCollidables;
    }

    private List<Collideable> getCollidablesInCell(long gridX, long gridY) {
        Map<Long, List<Collideable>> collisions = collisionGrid.get(gridX);
        if (collisions != null) {
            List<Collideable> collisionsList = collisions.get(gridY);
            if (collisionsList != null) {
                return collisionsList;
            }
        }
        return List.of();
    }

    private void addToGrid(Collideable object, long x, long y){
        long gridX = getGridCoordinate(x);
        long gridY = getGridCoordinate(y);
        
        if (!collisionGrid.containsKey(gridX))
            collisionGrid.put(gridX, new HashMap<>());
        
        if(!collisionGrid.get(gridX).containsKey(gridY))
            collisionGrid.get(gridX).put(gridY, new ArrayList<>());
            
        collisionGrid.get(gridX).get(gridY).add(object);
    }

    private void removeFromGrid(long x, long y, Collideable obj){
        long gridX = getGridCoordinate(x);
        long gridY = getGridCoordinate(y);
        
        Map<Long, List<Collideable>> xCells = collisionGrid.get(gridX);
        if(xCells != null) {
            List<Collideable> nearbyCollisions = xCells.get(gridY);
            if(nearbyCollisions != null) {
                nearbyCollisions.remove(obj);
                if (nearbyCollisions.isEmpty())
                    xCells.remove(gridX);
            }
            if(xCells.isEmpty())
                collisionGrid.remove(gridX);
        }
    }

    private long getGridCoordinate(long numb){
        return numb / GRID_SIZE;
    }
}
