package ru.itis.tanks.game.model;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.map.GameWorld;

@Getter
@Setter
public abstract class MovingObject extends GameObject implements Collideable, Updatable{

    protected final GameWorld world;

    protected long velocity;

    protected Direction direction;

    protected boolean isMoving;

    public MovingObject(GameWorld world, long velocity, Direction direction,
                        long x, long y, int width, int height) {
        super(x, y, width, height);
        this.world = world;
        this.velocity = velocity;
        this.direction = direction;
        this.isMoving = true;

    }

    @Override
    public void update(long delta) {
        if(!isMoving)
            return;
        long oldX = x;
        long oldY = y;
        x += direction.getX() * velocity * delta / 16;
        y += direction.getY() * velocity * delta / 16;
        if (x != oldX || y != oldY)
            world.handleCollision(this, oldX, oldY);
    }
}
