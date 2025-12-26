package ru.itis.tanks.game.model;

public interface Destroyable extends Removable{

    void takeDamage(int damageValue);

    int getMaxHp();

    int getHp();

    boolean isDestroyed();
}
