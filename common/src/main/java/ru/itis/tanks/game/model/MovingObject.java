package ru.itis.tanks.game.model;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.GameWorld;

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public abstract class MovingObject extends AbstractGameObject implements Collideable, Updatable {

    protected static final int VELOCITY_DIVIDER = 64;

    protected final GameWorld world;

    protected long velocity;

    protected volatile Direction direction;

    protected AtomicBoolean isMoving;

    private boolean readyToUpdate = false;

    public MovingObject(GameWorld world, long velocity, Direction direction, boolean isMoving,
                        Texture texture, long x, long y, int width, int height) {
        super(texture, x, y, width, height);
        this.world = world;
        this.velocity = velocity;
        this.direction = direction;
        this.isMoving = new AtomicBoolean(isMoving);
    }

    public MovingObject(GameWorld world, long velocity, Direction direction,
                        Texture texture, long x, long y, int width, int height) {
        this(world, velocity, direction, true, texture, x, y, width, height);
    }

    @Override
    public void update(long delta) {
        if (isMoving.get()) {
            long oldX = x;
            long oldY = y;
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
}
