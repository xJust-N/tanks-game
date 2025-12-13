package ru.itis.tanks.game.model;

import lombok.AllArgsConstructor;
import ru.itis.tanks.game.model.impl.weapon.Projectile;
import ru.itis.tanks.game.model.impl.tank.Tank;

//Определяет какой тип снаряда вылетит
//По сути - паттерн стратегия для произведения выстрела
@AllArgsConstructor
public abstract class Gun{

    protected static final long DEFAULT_SHOOT_OFFSET = 2L;

    protected final Tank tank;

    public abstract Projectile getProjectile();

    public abstract long getReloadDelay();

    protected long calculateProjectileX(long bulletWidth){
        Direction direction = tank.getDirection();
        long tankMiddleX = tank.getX() + tank.getWidth() / 2;
        return tankMiddleX + direction.getX() * DEFAULT_SHOOT_OFFSET - bulletWidth / 2;
    }

    protected long calculateProjectileY(long bulletHeight){
        Direction direction = tank.getDirection();
        long tankMiddleY = tank.getY() + tank.getHeight() / 2;
        return tankMiddleY + direction.getY() * DEFAULT_SHOOT_OFFSET - bulletHeight / 2;
    }
}
