package ru.itis.tanks.game.model.impl;

public enum Texture {
    MISSING(0),
    PLAYER_TANK(1),
    ENEMY_TANK(2),
    BULLET(3),
    ROCKET_BULLET(4),
    HP_POWERUP(5),
    SPEED_POWERUP(6),
    ROCKET_GUN_POWERUP(7),
    GRASS(8),
    ROCK(9),
    STEEL(10),
    COLLIDEABLE_GLASS(11),
    BRICK(12),
    WOOD(13);

    Texture(int num) {
    }

    public static Texture fromInt(int i) {
        return Texture.values()[i];
    }
}
