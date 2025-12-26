package ru.itis.tanks.game.model;

import ru.itis.tanks.game.model.impl.tank.Tank;

public interface Collectable extends Collideable, Removable, GameObject {

    void onTankCollect(Tank tank);
}
