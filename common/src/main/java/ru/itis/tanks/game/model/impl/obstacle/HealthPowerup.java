package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractPowerup;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.GameWorld;

public class HealthPowerup extends AbstractPowerup {

    private static final int HEALTH_INCREMENT = 50;

    private static final int DEFAULT_WIDTH = 32;

    private static final int DEFAULT_HEIGHT = 32;

    public HealthPowerup(GameWorld world, long x, long y) {
        super(world, Texture.HP_POWERUP, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void onTankCollect(Tank tank) {
        tank.incMaxHp(HEALTH_INCREMENT);
        tank.incHp(HEALTH_INCREMENT);
    }
}
