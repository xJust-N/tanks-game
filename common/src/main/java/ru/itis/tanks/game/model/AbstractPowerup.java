package ru.itis.tanks.game.model;

import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.GameWorld;

public abstract class AbstractPowerup extends AbstractGameObject implements Collectable {

    protected final GameWorld world;

    public AbstractPowerup(GameWorld world, Texture texture, long x, long y, int width, int height) {
        super(texture, x, y, width, height);
        this.world = world;
    }

    @Override
    public void remove() {
        world.removeObject(this);
    }
}

