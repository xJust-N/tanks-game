package ru.itis.tanks.game.model.impl.obstacle;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.Destroyable;
import ru.itis.tanks.game.model.impl.IdManager;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.GameWorld;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class DestroyableBlock extends CollideableBlock implements Destroyable {

    private static final int DEFAULT_MAX_HP = 250;

    private final int id;

    private final GameWorld world;

    private final AtomicInteger hp;

    private int maxHp;

    public DestroyableBlock(GameWorld world, int maxHp, int x, int y) {
        super(x, y);
        this.maxHp = maxHp;
        hp = new AtomicInteger(maxHp);
        this.world = world;
        id = IdManager.getNextId();
    }

    public DestroyableBlock(GameWorld world, int id, int maxHp, int hp, int x, int y, int width, int height, Texture texture) {
        super(x, y, width, height, texture);
        this.world = world;
        this.maxHp = maxHp;
        this.hp = new AtomicInteger(hp);
        this.id = id;
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

    @Override
    public boolean isDestroyed() {
        return hp.get() <= 0;
    }

    public void setHp(int hp) {
        this.hp.set(hp);
    }
}
