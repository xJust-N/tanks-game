package ru.itis.tanks.game.model;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.impl.IdManager;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.ServerGameWorld;

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public abstract class MovingObject extends AbstractGameObject implements Collideable, Updatable {

    protected static final int VELOCITY_DIVIDER = 64;

    protected final ServerGameWorld world;

    protected int velocity;

    protected volatile Direction direction;

    protected AtomicBoolean isMoving;

    private final int id;

    private boolean readyToUpdate = false;

    public MovingObject(ServerGameWorld world, int id, int velocity, Direction direction, boolean isMoving,
                        Texture texture, int x, int y, int width, int height) {
        super(x, y, width, height, texture);
        this.world = world;
        this.id = id;
        this.velocity = velocity;
        this.direction = direction;
        this.isMoving = new AtomicBoolean(isMoving);
    }

    public MovingObject(ServerGameWorld world, int velocity, Direction direction,
                        Texture texture, int x, int y, int width, int height) {
        this(world, IdManager.getNextId(), velocity, direction, true, texture, x, y, width, height);
    }

    @Override
    public void update(int delta) {
        if (isMoving.get()) {
            int oldX = x;
            int oldY = y;
            x += direction.getX() * velocity * delta / VELOCITY_DIVIDER;
            y += direction.getY() * velocity * delta / VELOCITY_DIVIDER;
            if (x != oldX || y != oldY) {
                world.handleCollision(this, oldX, oldY);
            }
        }
        readyToUpdate = false;
    }
    public void setMoving(boolean isMoving) {
        this.isMoving.set(isMoving);
    }

    public boolean isMoving(){
        return isMoving.get();
    }
}
