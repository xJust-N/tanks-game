package ru.itis.tanks.game.model.impl;

import lombok.Getter;
import ru.itis.tanks.game.model.Destroyable;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.map.GameWorld;

@Getter
public class Projectile extends MovingObject implements Destroyable {

    private final int damage;

    public Projectile(GameWorld world, long velocity, Direction direction, int damage,
                      long x, long y, int width, int height) {
        super(world, velocity, direction, x, y, width, height);
        this.damage = damage;
    }

    @Override
    public void takeDamage(int damageValue) {
        destroy();
    }

    @Override
    public void destroy() {
        if (world != null) {
            world.removeObject(this);
        }
    }
}
