package ru.itis.tanks.game;

import lombok.Getter;
import ru.itis.tanks.game.model.map.GameWorld;

public class LocalMultiplayerGame implements Runnable {



    @Getter
    private final GameWorld gameWorld;

    private final Thread gameThread = new Thread(this);

    private final long updateInterval;

    private boolean isRunning;

    public LocalMultiplayerGame(GameWorld gameWorld, long updateInterval) {
        this.gameWorld = gameWorld;
        this.updateInterval = updateInterval;
        this.isRunning = false;
    }

    public void start() {
        isRunning = true;
        gameThread.start();
    }

    public void run() {
        long lastUpdateTime = System.currentTimeMillis();
        while (isRunning) {
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastUpdateTime;

            if (deltaTime >= updateInterval) {
                update(deltaTime);
                lastUpdateTime = currentTime;
            }
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void update(long delta) {
        gameWorld.getUpdatables().forEach(updatable -> {
            updatable.notifyUpdate();
            updatable.update(delta);
        });
    }
}