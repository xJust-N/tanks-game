package ru.itis.tanks.game.model;

import lombok.Getter;
import ru.itis.tanks.game.model.impl.IdManager;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.GameWorld;

@Getter
public abstract class AbstractCollectable extends AbstractGameObject implements Collectable {

    protected final int id;

    protected final GameWorld world;

    public AbstractCollectable(GameWorld world, int x, int y, int width, int height, Texture texture) {
        super(x, y, width, height, texture);
        this.world = world;
        id = IdManager.getNextId();
    }

    @Override
    public void remove() {
        world.removeObject(this);
    }
}

