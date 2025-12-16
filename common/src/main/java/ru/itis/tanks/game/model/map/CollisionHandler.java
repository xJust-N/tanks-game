package ru.itis.tanks.game.model.map;

import ru.itis.tanks.game.model.*;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.weapon.Projectile;
import ru.itis.tanks.game.model.map.updates.GameEvent;
import ru.itis.tanks.game.model.map.updates.GameEventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.itis.tanks.game.model.map.updates.GameEventType.MODIFIED_OBJECT;
import static ru.itis.tanks.game.model.map.updates.GameEventType.MOVED_OBJECT;

public class CollisionHandler {

    private static final int GRID_SIZE = 32;

    //Константа для отступа при столкновении, чтобы объекты не застревали друг в друге
    private static final int COLLISION_OFFSET = 5;

    /*
     *  Представляет собой сетку, в ячейках которых лежат объекты с коллизией,
     *  Первый Int - х координата, второй - y
     *  Для проверки ближайших коллизий берется ячейка по координатам и ее соседние
     */
    private final Map<Integer, Map<Integer, List<Collideable>>> collisionGrid;

    private final GameEventDispatcher dispatcher;

    public CollisionHandler(GameEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        collisionGrid = new ConcurrentHashMap<>();
    }

    public void handleCollision(MovingObject obj, int oldX, int oldY) {
        int oldGridX = getGridCoordinate(oldX);
        int oldGridY = getGridCoordinate(oldY);
        List<Collideable> nearbyCollidables =
                getNearbyCollidables(getGridCoordinate(obj.getX()), getGridCoordinate(obj.getY()));
        for (Collideable collideable : nearbyCollidables) {
            if (obj == collideable)
                continue;
            if (obj.intersects(collideable)) {
                processCollision(obj, collideable);
            }
        }
        if (getGridCoordinate(obj.getX()) != oldGridX || getGridCoordinate(obj.getY()) != oldGridY) {
            removeFromGrid(oldX, oldY, obj);
            addToGrid(obj, obj.getX(), obj.getY());
        }
        dispatcher.notifyWorldUpdate(new GameEvent(obj, MOVED_OBJECT));
    }


    void addToGrid(Collideable object, int x, int y) {
        int gridX = getGridCoordinate(x);
        int gridY = getGridCoordinate(y);

        if (!collisionGrid.containsKey(gridX))
            collisionGrid.put(gridX, new HashMap<>());

        if (!collisionGrid.get(gridX).containsKey(gridY))
            collisionGrid.get(gridX).put(gridY, new ArrayList<>());

        collisionGrid.get(gridX).get(gridY).add(object);
    }

    void removeFromGrid(int x, int y, Collideable obj) {
        int gridX = getGridCoordinate(x);
        int gridY = getGridCoordinate(y);

        Map<Integer, List<Collideable>> xCells = collisionGrid.get(gridX);
        if (xCells != null) {
            List<Collideable> nearbyCollisions = xCells.get(gridY);
            if (nearbyCollisions != null) {
                nearbyCollisions.remove(obj);
                if (nearbyCollisions.isEmpty())
                    xCells.remove(gridY);
            }
            if (xCells.isEmpty())
                collisionGrid.remove(gridX);
        }
    }

    private void processCollision(MovingObject obj, GameObject secondObj) {
        if (obj instanceof Projectile projectile)
            handleProjectileCollision(projectile, secondObj);
        else if (obj instanceof Tank tank)
            handleTankCollision(tank, secondObj);
    }


    private void handleProjectileCollision(Projectile projectile, GameObject otherObj) {
        if (projectile.getTank() == otherObj)
            return;
        if (otherObj instanceof Projectile otherProjectile) {
            if(projectile.getTank() == otherProjectile.getTank())
                return;
            otherProjectile.remove();
        }
        else if (otherObj instanceof Destroyable destroyable) {
            destroyable.takeDamage(projectile.getDamage());
            dispatcher.notifyWorldUpdate(new GameEvent(destroyable, MODIFIED_OBJECT));
        }
        projectile.remove();
    }

    private void handleTankCollision(Tank tank, GameObject secondObj) {
        if (secondObj instanceof Projectile projectile) {
            //handleProjectileCollision(projectile, tank);
            return;
        } else if (secondObj instanceof Collectable collectable) {
            collectable.onTankCollect(tank);
            collectable.remove();
            return;
        }
        Direction offsetDir = tank.getDirection().opposite();
        int newX = tank.getX() + offsetDir.getX() * COLLISION_OFFSET;
        int newY = tank.getY() + offsetDir.getY() * COLLISION_OFFSET;
        tank.setX(newX);
        tank.setY(newY);
    }

    private List<Collideable> getNearbyCollidables(int gridX, int gridY) {
        List<Collideable> nearbyCollidables = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                nearbyCollidables.addAll(getCollidablesInCell(gridX + dx, gridY + dy));

        return nearbyCollidables;
    }

    private List<Collideable> getCollidablesInCell(int gridX, int gridY) {
        Map<Integer, List<Collideable>> collisions = collisionGrid.get(gridX);
        if (collisions != null) {
            List<Collideable> collisionsList = collisions.get(gridY);
            if (collisionsList != null) {
                return collisionsList;
            }
        }
        return List.of();
    }

    private int getGridCoordinate(int numb) {
        return numb / GRID_SIZE;
    }
}
