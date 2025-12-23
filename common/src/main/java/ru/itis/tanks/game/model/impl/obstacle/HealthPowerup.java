package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractCollectable;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.ServerGameWorld;

public class HealthPowerup extends AbstractCollectable {

    private static final int HEALTH_INCREMENT = 50;

    private static final int DEFAULT_WIDTH = 32;

    private static final int DEFAULT_HEIGHT = 32;

    public HealthPowerup(ServerGameWorld world, int x, int y) {
        super(world, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Texture.HP_POWERUP);
    }

    @Override
    public void onTankCollect(Tank tank) {
        tank.incMaxHp(HEALTH_INCREMENT);
        tank.incHp(HEALTH_INCREMENT);
    }
}
