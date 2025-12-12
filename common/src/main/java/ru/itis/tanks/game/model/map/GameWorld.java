package ru.itis.tanks.game.model.map;

import lombok.Getter;
import ru.itis.tanks.game.model.Collideable;
import ru.itis.tanks.game.model.GameObject;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.Updatable;
import ru.itis.tanks.game.model.impl.Tank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Todo: сериализация и десериализация
//Todo: разобраться с коллизиями на уровне карты
@Getter
public class GameWorld implements Updatable{

    private static final long GRID_SIZE = 64;

    private final long width;

    private final long height;

    private final List<GameObject> allObjects;

    private final List<Updatable> updatables;

    private final List<Tank> tanks;

    /*
     *  Представляет собой сетку, в ячейках которых лежат объекты с коллизией,
     *  Первый Long - х координата, второй - y
     */
    private final Map<Long, Map<Long, List<Collideable>>> collisionGrid;


    public GameWorld(long width, long height) {
        this.width = width;
        this.height = height;
        allObjects = new ArrayList<>();
        updatables = new ArrayList<>();
        tanks = new ArrayList<>();
        collisionGrid = new HashMap<>();
    }

    public void handleCollision(MovingObject obj, long oldX, long oldY){
        long currentGridX = getGridCoordinate(obj.getX());
        long currentGridY = getGridCoordinate(obj.getY());
        
        Map<Long, List<Collideable>> collisions = collisionGrid.get(currentGridX);
        if(collisions != null && !collisions.isEmpty()){
            List<Collideable> collisionsList = collisions.get(currentGridY);
            if(collisionsList != null && !collisionsList.isEmpty()){
                for(Collideable collideable : collisionsList){
                    if(obj.intersects((GameObject) collideable)){
                        obj.setX(oldX);
                        obj.setY(oldY);
                        return;
                    }
                }
            }
        }
        
        long oldGridX = getGridCoordinate(oldX);
        long oldGridY = getGridCoordinate(oldY);
        
        if(currentGridX != oldGridX || currentGridY != oldGridY){
            removeFromGrid(oldX, oldY);
            addToGrid(obj, obj.getX(), obj.getY());
        }
    }

    @Override
    public void update(long delta) {
        updatables.forEach(updatable -> updatable.update(delta));
    }

    public void addObject(GameObject object) {
        if (object instanceof Updatable updatable)
            updatables.add(updatable);
        if(object instanceof Collideable collideable)
            addToGrid(collideable, object.getX(), object.getY());
        if(object instanceof Tank tank)
            tanks.add(tank);
        allObjects.add(object);
    }

    public void removeObject(GameObject object) {
        if (object instanceof Updatable updatable)
            updatables.remove(updatable);
        if (object instanceof Collideable)
            removeFromGrid(object.getX(), object.getY());
        if(object instanceof Tank tank)
            tanks.remove(tank);
        allObjects.remove(object);
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

    private void removeFromGrid(long x, long y){
        long gridX = getGridCoordinate(x);
        long gridY = getGridCoordinate(y);
        
        Map<Long, List<Collideable>> grid = collisionGrid.get(gridX);
        if(grid != null) {
            grid.remove(gridY);
            if(grid.isEmpty())
                collisionGrid.remove(gridX);
        }
    }
    private long getGridCoordinate(long numb){
        return numb / GRID_SIZE;
    }
}
