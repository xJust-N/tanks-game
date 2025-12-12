package ru.itis.tanks.game.model.impl;

import lombok.AllArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.Gun;
import ru.itis.tanks.game.model.map.GameWorld;


@AllArgsConstructor
public class DefaultGun extends Gun {

    private static final int DAMAGE = 25;

    private static final int VELOCITY = 1;

    private final GameWorld world;

    private final Tank tank;

    //TODO размер пули
    @Override
    public Projectile getProjectile() {
        Direction direction = tank.getDirection();
        long xOffset = direction.getX() * 16;
        long yOffset = direction.getY() * 16;
        return new Projectile(world, VELOCITY, direction, DAMAGE,
                tank.getX() + xOffset, tank.getY() + yOffset, 8, 8);
    }
}
