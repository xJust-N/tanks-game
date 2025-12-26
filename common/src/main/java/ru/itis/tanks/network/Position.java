package ru.itis.tanks.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.itis.tanks.game.model.Direction;

@AllArgsConstructor
@Getter
public class Position {

    private final Integer entityId;

    private final int x;

    private final int y;

    private final Direction direction;

    public Position(int x, int y, Direction direction) {
        this(null, x, y, direction);
    }

    public Position(int x, int y){
        this(x, y, null);
    }
}
