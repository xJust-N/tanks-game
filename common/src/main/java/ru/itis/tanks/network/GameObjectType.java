package ru.itis.tanks.network;

import lombok.Getter;

//Enum для определения типа сущности при десериализации
@Getter
public enum GameObjectType {
    SPEED_POWERUP(0),
    BLOCK(1),
    COLLIDEABLE_BLOCK(2),
    DESTROYABLE_BLOCK(3),
    TANK(4),
    PROJECTILE(5),
    DEFAULT_GUN(6),
    ROCKET_GUN(7),
    ROCKET_GUN_POWERUP(8),
    HEALTH_POWERUP(9);


    private final int code;

    GameObjectType(int i) {
        code = i;
    }

    public static GameObjectType fromCode(int i) {
        return GameObjectType.values()[i];
    }
}
