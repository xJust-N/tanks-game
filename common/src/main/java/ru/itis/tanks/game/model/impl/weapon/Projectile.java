package ru.itis.tanks.game.model.impl.weapon;

import lombok.Getter;
import ru.itis.tanks.game.model.Destroyable;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.Texture;

@Getter
public class Projectile extends MovingObject implements Destroyable {

    private final int damage;

    private final Tank tank;

    public Projectile(Tank tank, long velocity, int damage,
                      Texture texture, long x, long y, int width, int height) {
        super(tank.getWorld(), velocity, tank.getDirection(),
                texture, x, y, width, height);
        this.tank = tank;
        this.damage = damage;
    }

    @Override
    public void takeDamage(int damageValue) {
        destroy();
    }

    @Override
    public void destroy() {
        world.removeObject(this);
    }

    @Override
    public int getMaxHp() {
        return 1;
    }

    @Override
    public int getHp() {
        return 1;
    }
}
