package ru.itis.tanks.game.model.impl;

import java.util.Random;

public enum Texture {
    MISSING(0),
    PLAYER_TANK(1),
    ENEMY_TANK(2),
    ROCK(3),
    STEEL(4),
    GRASS(5);

    private final int num;

    Texture(int num) {
        this.num = num;
    }

    Texture(){
        this.num = -1;
    }

    public static Texture getRandomBlockTexture() {
        Random r = new Random();
        return fromInt(3 + r.nextInt(3));
    }

    public static Texture fromInt(int i) {
        return Texture.values()[i];
    }
}
