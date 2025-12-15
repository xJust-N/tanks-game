package ru.itis.tanks.game.model.impl.weapon;

import ru.itis.tanks.game.model.Gun;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.Texture;

public class DefaultGun extends Gun {

    private static final int DAMAGE = 15;

    private static final int VELOCITY = 30;

    private static final int BULLET_WIDTH = 10;

    private static final int BULLET_HEIGHT = 20;

    private static final long SHOOT_DELAY_MILLIS = 500;

    public DefaultGun(Tank tank) {
        super(tank);
    }

    @Override
    public Projectile getProjectile() {
        long x = calculateProjectileX(BULLET_WIDTH);
        long y = calculateProjectileY(BULLET_HEIGHT);
        return new Projectile(tank, VELOCITY, DAMAGE,
                Texture.BULLET, x, y, BULLET_WIDTH, BULLET_HEIGHT);
    }

    @Override
    public long getReloadDelay() {
        return SHOOT_DELAY_MILLIS;
    }
}
