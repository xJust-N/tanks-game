package ru.itis.tanks.game.controller;

import lombok.Getter;
import lombok.Setter;
import ru.itis.tanks.game.model.Direction;
import ru.itis.tanks.game.model.impl.Projectile;
import ru.itis.tanks.game.model.impl.Tank;

@Getter
@Setter
public class TankController {

    private Tank tank;

    private boolean isMoving;

    @Getter
    private Direction direction;
    
    public TankController(Tank tank, Direction direction) {
        this.tank = tank;
        this.direction = direction;
        this.isMoving = false;
        this.tank.setDirection(direction);
    }

    public void setDirection(Direction direction) {
        if (direction != null) {
            this.direction = direction;
            tank.setDirection(direction);
        }
    }

    public void startMoving() {
        this.isMoving = true;
        tank.setMoving(true);
    }

    public void stopMoving() {
        this.isMoving = false;
        tank.setMoving(false);
    }
    public void shoot() {
        //todo
        Projectile projectile = tank.shoot();
    }
}