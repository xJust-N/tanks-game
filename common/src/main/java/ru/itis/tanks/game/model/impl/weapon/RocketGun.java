package ru.itis.tanks.game.model.impl.weapon;

import ru.itis.tanks.game.model.Gun;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.GameWorld;

public class RocketGun extends Gun {

    private static final int DAMAGE = 90;

    private static final int VELOCITY = 12;

    private static final int BULLET_WIDTH = 20;

    private static final int BULLET_HEIGHT = 50;

    private static final int SHOOT_DELAY_MILLIS = 1_000;

    public RocketGun(Tank tank) {
        super(tank);
    }

    public RocketGun(GameWorld world, int ownerId) {
        this(world.getTanks().get(ownerId));
    }

    @Override
    public Projectile getProjectile() {
        int x = calculateProjectileX(BULLET_WIDTH);
        int y = calculateProjectileY(BULLET_HEIGHT);
        return new Projectile(tank, VELOCITY, DAMAGE,
                Texture.ROCKET_BULLET, x, y, BULLET_WIDTH, BULLET_HEIGHT);
    }

    @Override
    public int getReloadDelay() {
        return SHOOT_DELAY_MILLIS;
    }
}
