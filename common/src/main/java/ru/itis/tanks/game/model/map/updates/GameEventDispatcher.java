package ru.itis.tanks.game.model.map.updates;

public interface GameEventDispatcher {

    void addWorldUpdateListener(GameEventListener listener);

    void notifyWorldUpdate(GameEvent update);
}
