package ru.itis.tanks.game.model;

public interface Destroyable extends GameObject, Removable{

    void takeDamage(int damageValue);

    int getMaxHp();

    int getHp();
}
