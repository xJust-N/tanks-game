package ru.itis.tanks.game.model;

import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.ServerGameWorld;

public abstract class AbstractCollectable extends AbstractGameObject implements Collectable {

    protected final ServerGameWorld world;

    public AbstractCollectable(ServerGameWorld world, int x, int y, int width, int height, Texture texture) {
        super(x, y, width, height, texture);
        this.world = world;
    }

    @Override
    public void remove() {
        world.removeObject(this);
    }
}

