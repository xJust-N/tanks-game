package ru.itis.tanks.game.model.impl;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.*;
import ru.itis.tanks.game.model.map.GameWorld;

@Getter
public class Tank extends MovingObject implements Destroyable{

    private static final int MAX_HP = 100;

    private int hp;

    @Setter
    private Gun gun;

    public Tank(int hp, Gun gun, GameWorld world, long velocity,
                Direction direction, long x, long y, int width, int height) {
        super(world, velocity, direction, x, y, width, height);
        this.hp = hp;
        this.gun = gun;
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

    public Projectile shoot() {
        if(gun != null)
            return gun.getProjectile();
        return null;
    }
}
