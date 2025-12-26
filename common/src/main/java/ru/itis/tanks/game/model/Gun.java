package ru.itis.tanks.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itis.tanks.game.model.impl.weapon.Projectile;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.GameWorld;

//Определяет какой тип снаряда вылетит
//По сути - паттерн стратегия для произведения выстрела
@AllArgsConstructor
@Getter
public abstract class Gun{

    protected static final int DEFAULT_SHOOT_OFFSET = 2;

    protected final Tank tank;

    public Gun(GameWorld world, int ownerId){
        this.tank = world.getTanks().get(ownerId);
    }

    public abstract Projectile getProjectile();

    public abstract int getReloadDelay();

    protected int calculateProjectileX(int bulletWidth){
        Direction direction = tank.getDirection();
        int tankMiddleX = tank.getX() + tank.getWidth() / 2;
        return tankMiddleX + direction.getX() * DEFAULT_SHOOT_OFFSET - bulletWidth / 2;
    }

    protected int calculateProjectileY(int bulletHeight){
        Direction direction = tank.getDirection();
        int tankMiddleY = tank.getY() + tank.getHeight() / 2;
        return tankMiddleY + direction.getY() * DEFAULT_SHOOT_OFFSET - bulletHeight / 2;
    }
}
