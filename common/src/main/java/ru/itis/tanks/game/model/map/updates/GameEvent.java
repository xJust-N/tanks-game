package ru.itis.tanks.game.model.map.updates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itis.tanks.game.model.GameObject;

@AllArgsConstructor
@Getter
public class GameEvent {

    private final GameObject object;

    private final GameEventType type;

}
