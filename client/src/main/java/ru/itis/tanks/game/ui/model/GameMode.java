package ru.itis.tanks.game.ui.model;

import lombok.Getter;

@Getter
public enum GameMode {
    LOCAL_MULTIPLAYER(0),
    JOIN_GAME(1);

    private final int code;

    GameMode(int i) {
       this.code = i;
    }
}
