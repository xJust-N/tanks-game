package ru.itis.tanks.game.model.impl.tank;


import lombok.Getter;

@Getter
public enum Command {
    START_MOVING(0),
    STOP_MOVING(1),
    SHOOT(2),
    UP(3),
    DOWN(4),
    LEFT(5),
    RIGHT(6);

    private final int code;

    Command(int code) {
        this.code = code;
    }

    public static Command fromCode(int code) {
        if(code < 0 || code >= values().length)
            throw new IllegalArgumentException("Invalid command code: " + code);
        return Command.values()[code];
    }
}