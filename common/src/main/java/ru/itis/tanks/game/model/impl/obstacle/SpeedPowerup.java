package ru.itis.tanks.game.model.impl.obstacle;

import ru.itis.tanks.game.model.AbstractCollectable;
import ru.itis.tanks.game.model.impl.IdManager;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.map.GameWorld;

public class SpeedPowerup extends AbstractCollectable {

    private static final int DEFAULT_WIDTH = 32;

    private static final int DEFAULT_HEIGHT = 32;

    private static final int VELOCITY_INC = 5;

    public SpeedPowerup(GameWorld world, int x, int y) {
        this(world, IdManager.getNextId(), x, y);
    }

    public SpeedPowerup(GameWorld world, int id, int x, int y) {
        super(world, id, DEFAULT_WIDTH, DEFAULT_HEIGHT, x, y, Texture.SPEED_POWERUP);
    }

    @Override
    public void onTankCollect(Tank tank) {
        tank.setVelocity(tank.getVelocity() + VELOCITY_INC);
    }
}
