package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractCollectable;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.weapon.RocketGun;
import ru.itis.tanks.game.model.map.ServerGameWorld;

public class RocketGunPowerup extends AbstractCollectable {

    private static final int DEFAULT_WIDTH = 50;

    private static final int DEFAULT_HEIGHT = 50;

    public RocketGunPowerup(ServerGameWorld world, int x, int y) {
        super(world, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Texture.ROCKET_GUN_POWERUP);
    }

    @Override
    public void onTankCollect(Tank tank) {
        tank.setGun(new RocketGun(tank));
    }
}
