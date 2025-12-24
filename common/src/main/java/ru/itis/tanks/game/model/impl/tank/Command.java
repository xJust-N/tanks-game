package ru.itis.tanks.game.model.impl.tank;


import lombok.Getter;

@Getter
public enum Command {
    START_MOVING(0),
    STOP_MOVING(1),
    SHOOT(2),
    DIRECTION_CHANGE(3);

    private final int code;

    Command(int code) {
        this.code = code;
    }

    public static Command fromCode(int code) {
        return Command.values()[code];
    }
}