package ru.itis.tanks.network.util;

import lombok.Getter;

//Enum для определения типа сущности при десериализации
@Getter
public enum GameObjectType {
    BLOCK(1),
    COLLIDEABLE_BLOCK(2),
    DESTROYABLE_BLOCK(3),
    TANK(4),
    PROJECTILE(5),
    DEFAULT_GUN(6),
    ROCKET_GUN(7),
    ROCKET_GUN_POWERUP(8),
    HEALTH_POWERUP(9),
    SPEED_POWERUP(10);

    private final int code;

    GameObjectType(int i) {
        code = i;
    }
}
