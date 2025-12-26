package ru.itis.tanks.game.model.impl.weapon;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.MovingObject;
import ru.itis.tanks.game.model.Removable;
import ru.itis.tanks.game.model.impl.tank.Tank;
import ru.itis.tanks.game.model.impl.Texture;

@Getter
@Setter
public class Projectile extends MovingObject implements Removable {

    private final Tank tank;

    private int damage;

    public Projectile(Tank tank, int velocity, int damage,
                      Texture texture, int x, int y, int width, int height) {
        super(tank.getWorld(), velocity, tank.getDirection(),
                texture, x, y, width, height);
        this.tank = tank;
        this.damage = damage;
    }

    public Projectile(int id, Tank tank, int velocity, int damage, Texture texture, int x, int y, int width, int height) {
        super(tank.getWorld(),id,velocity, tank.getDirection(), true, texture, x, y, width, height);
        this.tank = tank;
        this.damage = damage;
    }

    @Override
    public void remove() {
        world.removeObject(this);
    }
}
