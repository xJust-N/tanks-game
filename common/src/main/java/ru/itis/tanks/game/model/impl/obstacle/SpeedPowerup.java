package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractCollectable;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.ServerGameWorld;

public class SpeedPowerup extends AbstractCollectable {

    private static final int DEFAULT_WIDTH = 32;

    private static final int DEFAULT_HEIGHT = 32;

    private static final int VELOCITY_INC = 5;

    public SpeedPowerup(ServerGameWorld world, int x, int y) {
        super(world, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, Texture.SPEED_POWERUP);
    }

    public SpeedPowerup(ServerGameWorld world, Texture texture, int x, int y, int width, int height) {
        super(world, x, y, width, height, texture);
    }

    @Override
    public void onTankCollect(Tank tank) {
        tank.setVelocity(tank.getVelocity() + VELOCITY_INC);
    }
}
