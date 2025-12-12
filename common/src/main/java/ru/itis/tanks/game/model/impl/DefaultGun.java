package ru.itis.tanks.game.model.impl;

import lombok.AllArgsConstructor;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.Gun;

@AllArgsConstructor
public class DefaultGun extends Gun {

    private static final int DAMAGE = 25;

    private static final int VELOCITY = 1;

    private final Tank tank;

    //TODO размер пули
    @Override
    public Projectile getProjectile() {
        Direction direction = tank.getDirection();
        long xOffset = direction.getX() * SPAWN_OFFSET;
        long yOffset = direction.getY() * SPAWN_OFFSET;
        return new Projectile(tank.getWorld(), VELOCITY, direction, DAMAGE,
                tank.getX() + xOffset, tank.getY() + yOffset, 8, 8);
    }
}
