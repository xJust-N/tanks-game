package ru.itis.tanks.game.model;

public interface Destroyable extends GameObject{

    void takeDamage(int damageValue);

    void destroy();

    int getMaxHp();

    int getHp();
}
