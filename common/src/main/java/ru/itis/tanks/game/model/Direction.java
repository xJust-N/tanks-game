package ru.itis.tanks.game.model;

import lombok.Getter;

@Getter
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int x;

    private final int y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Direction opposite() {
        return ofValue(x * -1, y * -1);
    }

    public static Direction ofValue(int x, int y) {
        for(Direction dir : Direction.values()) {
            if(dir.x == x && dir.y == y)
                return dir;
        }
        return null;
    }
}
