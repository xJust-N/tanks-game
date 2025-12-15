package ru.itis.tanks.game.model.impl.obstacle;

import lombok.Getter;
import ru.itis.tanks.game.model.Destroyable;
import ru.itis.tanks.game.model.map.GameWorld;

import java.util.concurrent.atomic.AtomicInteger;

public class DestroyableBlock extends CollideableBlock implements Destroyable {

    private static final int DEFAULT_MAX_HP = 250;

    private final GameWorld world;

    @Getter
    private final int maxHp;

    private final AtomicInteger hp;

    public DestroyableBlock(GameWorld world, int maxHp, long x, long y) {
        super(x, y);
        this.maxHp = maxHp;
        hp = new AtomicInteger(maxHp);
        this.world = world;
    }

    public DestroyableBlock(GameWorld world, long x, long y) {
        this(world, DEFAULT_MAX_HP, x, y);
    }

    @Override
    public void takeDamage(int damageValue) {
        int newHp = hp.addAndGet(-damageValue);
        if(newHp <= 0)
            remove();
    }

    @Override
    public void remove() {
        world.removeObject(this);
    }

    @Override
    public int getHp() {
        return hp.get();
    }
}
