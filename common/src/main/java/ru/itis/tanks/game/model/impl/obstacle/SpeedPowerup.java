package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractPowerup;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.GameWorld;

public class SpeedPowerup extends AbstractPowerup {

    private static final int DEFAULT_WIDTH = 32;

    private static final int DEFAULT_HEIGHT = 32;

    private static final int VELOCITY_INC = 5;

    public SpeedPowerup(GameWorld world, long x, long y) {
        super(world, Texture.SPEED_POWERUP, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void onTankCollect(Tank tank) {
        tank.setVelocity(tank.getVelocity() + VELOCITY_INC);
    }
}
