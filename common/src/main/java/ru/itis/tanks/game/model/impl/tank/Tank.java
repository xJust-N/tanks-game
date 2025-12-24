package ru.itis.tanks.game.model.impl.tank;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.*;
import ru.itis.tanks.game.model.impl.IdManager;
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

    private int maxHp;

    private final AtomicInteger hp;

    private long lastShootTime;

    private final String username;

    @Setter
    private boolean currentPlayerTank;

    @Setter
    private Gun gun;

    public Tank(GameWorld world, int id, int maxHp, int velocity, Direction direction,
                Texture texture, int x, int y, int width, int height) {
        this(world, id, maxHp, maxHp, 0, null, velocity, direction, texture, x, y, width, height);
    }

    public Tank(GameWorld world, int x, int y) {
        this(world, IdManager.getNextId(), DEFAULT_MAX_XP, DEFAULT_VELOCITY,
                Direction.UP, Texture.PLAYER_TANK, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    public Tank(GameWorld world, int id, int maxHp, int hp, long lastShootTime, Gun gun, int velocity, Direction direction,
                Texture texture, int x, int y, int width, int height) {
        super(world, id, velocity, direction, false, texture, x, y, width, height);
        this.hp = new AtomicInteger(hp);
        this.maxHp = maxHp;
        this.lastShootTime = lastShootTime;
        this.gun = gun;
        currentPlayerTank = false;
        this.username = "bot";
    }

    @Override
    public void takeDamage(int damageValue) {
        int newHp = hp.addAndGet(-damageValue);
        if (newHp <= 0)
            remove();
    }

    @Override
    public void remove() {
        world.removeObject(this);
        gun = null;
    }

    @Override
    public int getHp() {
        return hp.get();
    }

    public void incHp(int hp) {
        this.hp.addAndGet(hp);
    }
    public void incMaxHp(int maxHp) {
        this.maxHp += maxHp;
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
        long time = System.currentTimeMillis() - lastShootTime;
        if (time >= gun.getReloadDelay())
            return 0;
        return gun.getReloadDelay() - time;
    }
}
