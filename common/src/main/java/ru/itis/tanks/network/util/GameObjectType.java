package ru.itis.tanks.network.util;

//Enum для определения типа сущности при десериализации
public enum GameObjectType {
    BLOCK(1),
    COLLIDEABLE_BLOCK(2),
    DESTROYABLE_BLOCK(3),
    TANK(4),
    PROJECTILE(5),
    DEFAULT_GUN(6),
    ROCKET_GUN(7),
    POWER_UP(8);

    GameObjectType(int i) {

    }
}
