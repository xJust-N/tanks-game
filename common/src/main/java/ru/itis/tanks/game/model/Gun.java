package ru.itis.tanks.game.model;

import ru.itis.tanks.game.model.impl.Projectile;

//Определяет какой тип снаряда вылетит
public abstract class Gun{

    protected static final long SPAWN_OFFSET = 2L;

    public abstract Projectile getProjectile();

}
