package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractPowerup;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.weapon.RocketGun;
import ru.itis.tanks.game.model.map.GameWorld;

public class RocketGunPowerup extends AbstractPowerup {

    private static final int DEFAULT_WIDTH = 50;

    private static final int DEFAULT_HEIGHT = 50;

    public RocketGunPowerup(GameWorld world, long x, long y) {
        super(world, Texture.ROCKET_GUN_POWERUP, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void onTankCollect(Tank tank) {
        tank.setGun(new RocketGun(tank));
    }
}
