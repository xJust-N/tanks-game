package ru.itis.tanks.game.model.map.updates;

//TODO сюда можно очень хорошо прикрутить сообщения сокетов
public enum GameEventType {
    ADDED_OBJECT(0),
    REMOVED_OBJECT(1),
    MOVED_OBJECT(2),
    MODIFIED_OBJECT(3),
    GAME_OVER(4);

    GameEventType(int i) {}
}
