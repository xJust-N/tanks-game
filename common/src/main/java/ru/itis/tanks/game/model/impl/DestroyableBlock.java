package ru.itis.tanks.game.model.impl;

import ru.itis.tanks.game.model.Collideable;
import ru.itis.tanks.game.model.Destroyable;
import ru.itis.tanks.game.model.map.GameWorld;

public class DestroyableBlock extends Block implements Destroyable {

    private static final int MAX_HP = 250;

    private final GameWorld world;

    private int hp;

    public DestroyableBlock(GameWorld world, long x, long y) {
        super(x, y);
        hp = MAX_HP;
        this.world = world;
    }

    @Override
    public void takeDamage(int damageValue) {
        hp -= damageValue;
        if(hp <= 0)
            destroy();
    }

    @Override
    public void destroy() {
        if (world != null) {
            world.removeObject(this);
        }
    }
}
