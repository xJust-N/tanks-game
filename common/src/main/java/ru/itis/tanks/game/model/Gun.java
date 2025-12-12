package ru.itis.tanks.game.model;

import ru.itis.tanks.game.model.impl.Projectile;

//Определяет какой тип снаряда вылетит
public abstract class Gun{

    public abstract Projectile getProjectile();

}
