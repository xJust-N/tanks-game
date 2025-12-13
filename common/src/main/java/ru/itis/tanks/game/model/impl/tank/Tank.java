package ru.itis.tanks.game.model.impl.tank;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.*;
import ru.itis.tanks.game.model.impl.Texture;
import ru.itis.tanks.game.model.map.GameWorld;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Tank extends MovingObject implements Destroyable {

    private static final int DEFAULT_MAX_XP = 100;

    private static final int DEFAULT_WIDTH = 32;

    private static final int DEFAULT_HEIGHT = 32;

    private static final int DEFAULT_VELOCITY = 5;

    private static final int COLLIDE_DAMAGE = 10;

    private final int maxHp;

    private final AtomicInteger hp;

    private long lastShootTime;

    @Setter
    private boolean currentPlayerTank;

    @Setter
    private Gun gun;

    public Tank(int maxHp, GameWorld world, long velocity, Direction direction,
                Texture texture, long x, long y, int width, int height) {
        super(world, velocity, direction, false, texture, x, y, width, height);
        this.hp = new AtomicInteger(maxHp);
        this.maxHp = maxHp;
        lastShootTime = 0;
        currentPlayerTank = false;
    }

    public Tank(GameWorld world, long x, long y) {
        this(DEFAULT_MAX_XP, world, DEFAULT_VELOCITY,
                Direction.UP, Texture.PLAYER_TANK, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }


    @Override
    public synchronized void takeDamage(int damageValue) {
        int newHp = hp.addAndGet(-damageValue);
        if (newHp <= 0)
            destroy();
    }

    @Override
    public synchronized void destroy() {
        world.removeObject(this);
        gun = null;
    }

    @Override
    public int getHp() {
        return hp.get();
    }

    public void shoot() {
        if (gun == null)
            return;
        long currentTime = System.currentTimeMillis();
        double shootDelay = gun.getReloadDelay();
        if (currentTime - lastShootTime >= shootDelay) {
            world.addObject(gun.getProjectile());
            lastShootTime = currentTime;
        }
    }

    public long getReloadRemaining() {
        long remaining = System.currentTimeMillis() - lastShootTime;
        if (remaining - gun.getReloadDelay() > 0)
            return remaining;
        return 0;
    }

    public void takeCollideDamage() {
        takeDamage(COLLIDE_DAMAGE);
    }
}
