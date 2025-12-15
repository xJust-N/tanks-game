package ru.itis.tanks.game.model.impl.weapon;

import lombok.Getter;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.Removable;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.Texture;

@Getter
public class Projectile extends MovingObject implements Removable {

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
    public void remove() {
        world.removeObject(this);
    }
}
